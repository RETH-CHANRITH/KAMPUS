package com.example.kampus.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ── Result models ─────────────────────────────────────────────────────────────

data class SearchPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorHandle: String = "",
    val authorProfileUrl: String = "",
    val authorAvatar: String = "👤",
    val content: String = "",
    val timestamp: Long = 0L,
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val firstImageUrl: String = "",
    val campus: String = "",
)

data class SearchUser(
    val id: String = "",
    val displayName: String = "",
    val handle: String = "",
    val profileImageUrl: String = "",
    val avatarEmoji: String = "👤",
    val faculty: String = "",
    val role: String = "",
    val isFollowing: Boolean = false,
)

data class SearchGroup(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val members: String = "0",
    val description: String = "",
    val coverEmoji: String = "👥",
    val isJoined: Boolean = false,
    val memberAvatars: List<String> = emptyList(),
)

data class SearchEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val startDate: Long = 0L,
    val tags: List<String> = emptyList(),
)

data class SearchTag(
    val tag: String = "",
    val postCount: Int = 0,
)

data class SearchUiState(
    val query: String = "",
    val selectedTab: SearchTab = SearchTab.ALL,
    val posts: List<SearchPost> = emptyList(),
    val users: List<SearchUser> = emptyList(),
    val groups: List<SearchGroup> = emptyList(),
    val events: List<SearchEvent> = emptyList(),
    val tags: List<SearchTag> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

enum class SearchTab(val label: String) {
    ALL("All"),
    POSTS("Posts"),
    PEOPLE("People"),
    GROUPS("Groups"),
    EVENTS("Events"),
    TAGS("Tags"),
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {

    private val db       = FirebaseFirestore.getInstance()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Raw query flow with debounce so we don't fire on every keystroke
    private val _queryFlow = MutableStateFlow("")
    private var searchJob: Job? = null

    // Cache of user doc data to resolve author profiles in posts
    private val userCache = mutableMapOf<String, SearchUser>()

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(350L)
                .distinctUntilChanged()
                .collect { q ->
                    if (q.isBlank()) {
                        _uiState.update {
                            it.copy(
                                posts = emptyList(), users = emptyList(),
                                groups = emptyList(), events = emptyList(),
                                tags = emptyList(), isLoading = false, error = null
                            )
                        }
                    } else {
                        doSearch(q.trim())
                    }
                }
        }
    }

    fun onQueryChange(q: String) {
        _uiState.update { it.copy(query = q) }
        _queryFlow.value = q
    }

    fun onTabSelect(tab: SearchTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onClearQuery() {
        _uiState.update { it.copy(query = "", selectedTab = SearchTab.ALL) }
        _queryFlow.value = ""
    }

    // ── Core search ───────────────────────────────────────────────────────────

    private fun doSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val q = query.lowercase()
            val qEnd = q + "\uf8ff" // Firestore range trick for prefix matching

            try {
                // Fire all 5 searches in parallel
                val postsDeferred   = launch { searchPosts(q, qEnd) }
                val usersDeferred   = launch { searchUsers(q, qEnd) }
                val groupsDeferred  = launch { searchGroups(q, qEnd) }
                val eventsDeferred  = launch { searchEvents(q, qEnd) }
                val tagsDeferred    = launch { searchTags(q) }

                postsDeferred.join()
                usersDeferred.join()
                groupsDeferred.join()
                eventsDeferred.join()
                tagsDeferred.join()

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Search failed") }
            }
        }
    }

    // ── Posts ─────────────────────────────────────────────────────────────────

    private suspend fun searchPosts(q: String, qEnd: String) {
        try {
            val snap = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(200)
                .get().await()

            val authorIds = snap.documents.mapNotNull { it.getString("authorId") }.distinct()
            resolveUsers(authorIds)

            val results = snap.documents.mapNotNull { doc ->
                val content = doc.getString("content") ?: doc.getString("text") ?: ""
                val tags    = (doc.get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                val campus  = doc.getString("campus") ?: doc.getString("location") ?: "RUPP"
                val authorId = doc.getString("authorId") ?: ""

                val matchesContent = content.lowercase().contains(q)
                val matchesTags    = tags.any { it.lowercase().contains(q) }
                val matchesAuthor  = (userCache[authorId]?.displayName ?: "").lowercase().contains(q)
                val matchesCampus  = campus.lowercase().contains(q)

                if (!matchesContent && !matchesTags && !matchesAuthor && !matchesCampus) return@mapNotNull null

                val mediaUrls = (doc.get("mediaUrls") as? List<*>)?.mapNotNull { it as? String }
                val firstImg  = mediaUrls?.firstOrNull() ?: doc.getString("imageUrl") ?: ""
                val cached    = userCache[authorId]
                val profileUrl = cached?.profileImageUrl?.ifBlank { doc.extractProfileImageUrl() }
                    ?: doc.extractProfileImageUrl()

                SearchPost(
                    id               = doc.id,
                    authorId         = authorId,
                    authorName       = cached?.displayName?.ifBlank { null } ?: doc.getString("authorName") ?: doc.getString("author") ?: doc.getString("authorName") ?: "Someone",
                    authorHandle     = cached?.handle ?: "",
                    authorProfileUrl = profileUrl,
                    authorAvatar     = cached?.avatarEmoji ?: doc.extractAvatarEmoji(),
                    content          = content,
                    timestamp        = doc.getLong("timestamp") ?: 0L,
                    likes            = (doc.getLong("likes") ?: 0L).toInt(),
                    comments         = (doc.getLong("comments") ?: 0L).toInt(),
                    shares           = (doc.getLong("shares") ?: 0L).toInt(),
                    firstImageUrl    = firstImg,
                    campus           = campus,
                )
            }.sortedByDescending { it.likes + it.comments }

            _uiState.update { it.copy(posts = results) }
        } catch (_: Exception) {}
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    private suspend fun searchUsers(q: String, qEnd: String) {
        try {
            // Fetch users collection to perform comprehensive substring search
            val snap = db.collection("users")
                .limit(100)
                .get().await()

            val myFollowing = runCatching {
                db.collection("users").document(currentUid)
                    .collection("following").get().await()
                    .documents.map { it.id }.toSet()
            }.getOrDefault(emptySet())

            val results = snap.documents.mapNotNull { doc ->
                if (doc.id == currentUid) return@mapNotNull null
                val name   = doc.getString("displayName") ?: doc.getString("name") ?: ""
                val handle = doc.getString("handle") ?: doc.getString("username") ?: ""
                val email  = doc.getString("email") ?: ""
                val faculty = doc.getString("faculty") ?: doc.getString("major") ?: ""

                val matches = name.lowercase().contains(q) ||
                        handle.lowercase().contains(q) ||
                        email.lowercase().contains(q) ||
                        faculty.lowercase().contains(q)

                if (!matches) return@mapNotNull null

                val photoUrl = doc.extractProfileImageUrl().ifBlank {
                    if (doc.id == currentUid) FirebaseAuth.getInstance().currentUser?.photoUrl?.toString().orEmpty() else ""
                }

                SearchUser(
                    id              = doc.id,
                    displayName     = name,
                    handle          = if (handle.startsWith("@")) handle else "@$handle",
                    profileImageUrl = photoUrl,
                    avatarEmoji     = doc.extractAvatarEmoji(),
                    faculty         = faculty,
                    role            = doc.getString("role") ?: doc.getString("year") ?: "Student",
                    isFollowing     = doc.id in myFollowing,
                )
            }

            _uiState.update { it.copy(users = results) }
        } catch (_: Exception) {}
    }

    // ── Groups ────────────────────────────────────────────────────────────────

    private suspend fun searchGroups(q: String, qEnd: String) {
        try {
            val snap = db.collection("groups")
                .orderBy("name")
                .startAt(q.replaceFirstChar { it.uppercase() })
                .endAt(q.replaceFirstChar { it.uppercase() } + "\uf8ff")
                .limit(20)
                .get().await()

            val snapLc = db.collection("groups")
                .orderBy("name")
                .startAt(q)
                .endAt(qEnd)
                .limit(20)
                .get().await()

            val allDocs = (snap.documents + snapLc.documents).distinctBy { it.id }

            val results = allDocs.mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                if (!name.lowercase().contains(q) &&
                    !(doc.getString("description") ?: "").lowercase().contains(q) &&
                    !(doc.getString("category") ?: "").lowercase().contains(q)) return@mapNotNull null

                SearchGroup(
                    id          = doc.id,
                    name        = name,
                    category    = doc.getString("category") ?: "General",
                    members     = doc.getString("members") ?: "0",
                    description = doc.getString("description") ?: "",
                    coverEmoji  = doc.getString("coverEmoji") ?: "👥",
                    isJoined    = doc.getBoolean("isJoined") ?: false,
                )
            }

            _uiState.update { it.copy(groups = results) }
        } catch (_: Exception) {}
    }

    // ── Events ────────────────────────────────────────────────────────────────

    private suspend fun searchEvents(q: String, qEnd: String) {
        try {
            val snap = db.collection("events")
                .orderBy("title")
                .startAt(q.replaceFirstChar { it.uppercase() })
                .endAt(q.replaceFirstChar { it.uppercase() } + "\uf8ff")
                .limit(20)
                .get().await()

            val snapLc = db.collection("events")
                .orderBy("title")
                .startAt(q)
                .endAt(qEnd)
                .limit(20)
                .get().await()

            val allDocs = (snap.documents + snapLc.documents).distinctBy { it.id }

            val results = allDocs.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                if (!title.lowercase().contains(q) &&
                    !(doc.getString("description") ?: "").lowercase().contains(q) &&
                    !(doc.getString("location") ?: "").lowercase().contains(q)) return@mapNotNull null

                SearchEvent(
                    id          = doc.id,
                    title       = title,
                    description = doc.getString("description") ?: "",
                    location    = doc.getString("location") ?: "",
                    imageUrl    = doc.getString("image_url") ?: doc.getString("imageUrl") ?: "",
                    startDate   = doc.getLong("start_date") ?: doc.getLong("startDate") ?: 0L,
                    tags        = (doc.get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                )
            }

            _uiState.update { it.copy(events = results) }
        } catch (_: Exception) {}
    }

    // ── Tags ──────────────────────────────────────────────────────────────────

    private suspend fun searchTags(q: String) {
        try {
            val snap = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(500)
                .get().await()

            val tagCount = mutableMapOf<String, Int>()
            snap.documents.forEach { doc ->
                val tags = (doc.get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                tags.filter { it.lowercase().contains(q) }.forEach { tag ->
                    tagCount[tag] = (tagCount[tag] ?: 0) + 1
                }
            }

            val results = tagCount.entries
                .sortedByDescending { it.value }
                .take(20)
                .map { SearchTag(tag = it.key, postCount = it.value) }

            _uiState.update { it.copy(tags = results) }
        } catch (_: Exception) {}
    }

    // ── User resolution helper ────────────────────────────────────────────────

    private suspend fun resolveUsers(ids: List<String>) {
        val missing = ids.filter { it.isNotBlank() && !userCache.containsKey(it) }
        if (missing.isEmpty()) return
        missing.chunked(30).forEach { chunk ->
            runCatching {
                val snap = db.collection("users")
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk)
                    .get().await()
                snap.documents.forEach { doc ->
                    userCache[doc.id] = SearchUser(
                        id              = doc.id,
                        displayName     = doc.getString("displayName") ?: doc.getString("name") ?: "",
                        handle          = run {
                            val raw = doc.getString("handle") ?: doc.getString("username") ?: ""
                            if (raw.startsWith("@")) raw else "@$raw"
                        },
                        profileImageUrl = doc.extractProfileImageUrl(),
                        avatarEmoji     = doc.extractAvatarEmoji(),
                        faculty         = doc.getString("faculty") ?: "",
                        role            = doc.getString("role") ?: "Student",
                    )
                }
            }
        }
    }

    // ── Follow toggle ─────────────────────────────────────────────────────────

    fun toggleFollow(userId: String) {
        if (currentUid.isBlank()) return
        val current = _uiState.value.users.firstOrNull { it.id == userId } ?: return
        val nowFollowing = !current.isFollowing

        _uiState.update { state ->
            state.copy(users = state.users.map {
                if (it.id == userId) it.copy(isFollowing = nowFollowing) else it
            })
        }

        viewModelScope.launch {
            runCatching {
                val followRef = db.collection("users").document(currentUid)
                    .collection("following").document(userId)
                if (nowFollowing) {
                    followRef.set(mapOf("followedAt" to System.currentTimeMillis())).await()
                } else {
                    followRef.delete().await()
                }
            }
        }
    }

    // ── Join group toggle ─────────────────────────────────────────────────────

    fun toggleJoinGroup(groupId: String) {
        val current = _uiState.value.groups.firstOrNull { it.id == groupId } ?: return
        val nowJoined = !current.isJoined

        _uiState.update { state ->
            state.copy(groups = state.groups.map {
                if (it.id == groupId) it.copy(isJoined = nowJoined) else it
            })
        }

        viewModelScope.launch {
            runCatching {
                db.collection("groups").document(groupId)
                    .update("isJoined", nowJoined).await()
            }
        }
    }
}

// ── DocumentSnapshot Field Extraction Helpers ──────────────────────────

private fun com.google.firebase.firestore.DocumentSnapshot.extractProfileImageUrl(): String {
    return this.getString("profileImageUrl")
        ?: this.getString("profile_image_url")
        ?: this.getString("photoUrl")
        ?: this.getString("photoURL")
        ?: this.getString("avatarUrl")
        ?: this.getString("avatar_url")
        ?: this.getString("profileImage")
        ?: this.getString("imageUrl")
        ?: this.getString("imageUri")
        ?: ""
}

private fun com.google.firebase.firestore.DocumentSnapshot.extractAvatarEmoji(): String {
    return this.getString("avatarEmoji")
        ?: this.getString("avatar_emoji")
        ?: this.getString("avatar")
        ?: "👤"
}
