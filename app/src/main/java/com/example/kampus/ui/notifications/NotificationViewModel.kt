package com.example.kampus.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kampus.domain.model.AppNotification
import com.example.kampus.utils.NotificationLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ── Domain models ─────────────────────────────────────────────────────────────

data class ActorProfile(
    val userId: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val avatarEmoji: String = "👤",
)

data class GroupedNotification(
    val id: String,
    val type: String,
    val targetId: String,
    val latestCreatedAt: Long,
    val isRead: Boolean,
    val actors: List<ActorProfile>,
    val actorUserIds: List<String>,
    val count: Int,
    val title: String,
    val body: String,
    val postImageUrl: String = "",
    val rawNotifications: List<AppNotification>,
)

data class NotificationUiState(
    val groupedNotifications: List<GroupedNotification> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    // Optimistic overrides: ids that are locally marked read before Firebase confirms
    val localReadIds: Set<String> = emptySet(),
)

// Helper that applies local optimistic read state on top of server state
fun GroupedNotification.effectiveIsRead(localReadIds: Set<String>): Boolean =
    isRead || rawNotifications.any { it.id in localReadIds }

// ── ViewModel ─────────────────────────────────────────────────────────────────

class NotificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private var listener: ListenerRegistration? = null
    private var resolveJob: kotlinx.coroutines.Job? = null
    private val actorProfiles   = mutableMapOf<String, ActorProfile>()
    private val postThumbnails  = mutableMapOf<String, String>()

    init { observeNotifications() }

    // ── Real-time listener ────────────────────────────────────────────────────

    private fun observeNotifications() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Not authenticated") }
            return
        }

        listener?.remove()
        listener = NotificationLogger.observeUserNotifications(userId) { result ->
            result.onSuccess { rows ->
                val mapped = rows.map {
                    AppNotification(
                        id                  = it["id"]                  as? String  ?: "",
                        type                = it["type"]                as? String  ?: "system",
                        title               = it["title"]               as? String  ?: "Notification",
                        body                = it["body"]                as? String  ?: "",
                        toUserId            = it["toUserId"]            as? String  ?: "",
                        actorUserId         = it["actorUserId"]         as? String  ?: "",
                        actorDisplayName    = it["actorDisplayName"]    as? String  ?: "",
                        targetId            = it["targetId"]            as? String  ?: "",
                        createdAt           = it["createdAt"]           as? Long    ?: 0L,
                        isRead              = it["isRead"]              as? Boolean ?: false,
                    )
                }
                resolveAndPublish(mapped)
            }
            result.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Failed to load") }
            }
        }
    }

    // ── Profile + thumbnail resolution ───────────────────────────────────────

    private fun resolveAndPublish(rawList: List<AppNotification>) {
        resolveJob?.cancel()
        resolveJob = viewModelScope.launch {
            // Show immediately with cached data
            publishGrouped(rawList)

            val missingActorIds = rawList.map { it.actorUserId }
                .filter { it.isNotBlank() && !actorProfiles.containsKey(it) }
                .distinct()

            val postTypes = listOf("like", "comment", "love", "reaction")
            val missingPostIds = rawList.filter { it.type in postTypes }
                .map { it.targetId }
                .filter { it.isNotBlank() && !postThumbnails.containsKey(it) }
                .distinct()

            if (missingActorIds.isNotEmpty() || missingPostIds.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()

                if (missingActorIds.isNotEmpty()) {
                    missingActorIds.chunked(30).forEach { chunk ->
                        runCatching {
                            val snap = db.collection("users")
                                .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk)
                                .get().await()
                            snap.documents.forEach { doc ->
                                actorProfiles[doc.id] = ActorProfile(
                                    userId           = doc.id,
                                    displayName      = doc.getString("displayName") ?: doc.getString("name") ?: "Someone",
                                    profileImageUrl  = doc.getString("profileImageUrl") ?: "",
                                    avatarEmoji      = doc.getString("avatarEmoji") ?: "👤"
                                )
                            }
                        }
                    }
                }

                if (missingPostIds.isNotEmpty()) {
                    missingPostIds.chunked(30).forEach { chunk ->
                        runCatching {
                            val snap = db.collection("posts")
                                .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk)
                                .get().await()
                            snap.documents.forEach { doc ->
                                val mediaUrls = (doc.get("mediaUrls") as? List<*>)?.mapNotNull { it as? String }
                                val legacy = doc.getString("imageUrl") ?: doc.getString("imageUri") ?: ""
                                postThumbnails[doc.id] = mediaUrls?.firstOrNull() ?: legacy
                            }
                        }
                    }
                }

                publishGrouped(rawList)
            }
        }
    }

    private fun publishGrouped(rawList: List<AppNotification>) {
        val grouped  = mutableListOf<GroupedNotification>()
        val processed = mutableSetOf<String>()

        for (notif in rawList) {
            if (notif.id in processed) continue

            val groupMembers = when {
                notif.targetId.isNotBlank() && notif.type in listOf(
                    "like","comment","love","reaction","story_reply","mention","chat_message"
                ) -> rawList.filter { it.type == notif.type && it.targetId == notif.targetId }
                notif.type in listOf("follow","friend_request") -> rawList.filter { it.type == notif.type }
                else -> listOf(notif)
            }

            processed.addAll(groupMembers.map { it.id })

            val actorIds = groupMembers.map { it.actorUserId }.distinct()
            val actors   = actorIds.map { id ->
                actorProfiles[id] ?: ActorProfile(
                    userId      = id,
                    displayName = groupMembers.firstOrNull { it.actorUserId == id }?.actorDisplayName ?: "Someone",
                    avatarEmoji = "👤"
                )
            }

            val latestNotif       = groupMembers.maxByOrNull { it.createdAt } ?: notif
            val allRead           = groupMembers.all { it.isRead }
            val count             = groupMembers.size
            val firstActorName    = actors.firstOrNull()?.displayName ?: notif.actorDisplayName.ifBlank { "Someone" }

            val (title, body) = buildTitleBody(notif.type, count, firstActorName, notif.body)

            grouped.add(
                GroupedNotification(
                    id              = latestNotif.id,
                    type            = notif.type,
                    targetId        = notif.targetId,
                    latestCreatedAt = latestNotif.createdAt,
                    isRead          = allRead,
                    actors          = actors,
                    actorUserIds    = actorIds,
                    count           = count,
                    title           = title,
                    body            = body,
                    postImageUrl    = postThumbnails[notif.targetId] ?: "",
                    rawNotifications = groupMembers,
                )
            )
        }

        _uiState.update { it.copy(groupedNotifications = grouped, isLoading = false, error = null) }
    }

    private fun buildTitleBody(type: String, count: Int, firstActor: String, rawBody: String): Pair<String, String> =
        when (type) {
            "like","love","reaction" ->
                (if (count > 1) "$firstActor and ${count - 1} others" else firstActor) to "liked your post"
            "comment" ->
                (if (count > 1) "$firstActor and ${count - 1} others" else firstActor) to
                (if (count > 1) "commented on your post" else "commented: \"$rawBody\"")
            "chat_message","direct_message" ->
                firstActor to (if (count > 1) "sent you $count messages" else rawBody)
            "follow","friend_request" ->
                (if (count > 1) "$firstActor and ${count - 1} others" else firstActor) to
                (if (type == "friend_request") "sent you a follow request" else "started following you")
            "story_reply" -> firstActor to "replied to your story: \"$rawBody\""
            "mention"     ->
                (if (count > 1) "$firstActor and ${count - 1} others" else firstActor) to "mentioned you in a post"
            "share"       ->
                (if (count > 1) "$firstActor and ${count - 1} others" else firstActor) to "shared your post"
            "story"       -> firstActor to "added a new story"
            else          -> firstActor to rawBody
        }

    // ── Public actions ────────────────────────────────────────────────────────

    /** Optimistically marks a notification group as read in the UI, then persists to Firestore. */
    fun markGroupAsRead(grouped: GroupedNotification) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (grouped.effectiveIsRead(_uiState.value.localReadIds)) return

        // Optimistic: add raw notification ids to local set immediately
        val idsToMark = grouped.rawNotifications.filter { !it.isRead }.map { it.id }.toSet()
        if (idsToMark.isEmpty()) return
        _uiState.update { it.copy(localReadIds = it.localReadIds + idsToMark) }

        viewModelScope.launch {
            idsToMark.forEach { id ->
                runCatching { NotificationLogger.markNotificationRead(userId, id) }
            }
        }
    }

    /** Marks every unread notification as read at once. */
    fun markAllAsRead() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val currentState = _uiState.value
        val unreadGroups = currentState.groupedNotifications.filter {
            !it.effectiveIsRead(currentState.localReadIds)
        }
        if (unreadGroups.isEmpty()) return

        val allIds = unreadGroups
            .flatMap { it.rawNotifications }
            .filter { !it.isRead }
            .map { it.id }
            .toSet()

        _uiState.update { it.copy(localReadIds = it.localReadIds + allIds) }

        viewModelScope.launch {
            allIds.forEach { id ->
                runCatching { NotificationLogger.markNotificationRead(userId, id) }
            }
        }
    }

    /** Force-refreshes by detaching and re-attaching the Firestore listener. */
    fun refresh() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        listener?.remove()
        observeNotifications()
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
