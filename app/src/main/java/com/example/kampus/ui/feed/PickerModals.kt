package com.example.kampus.ui.feed

import android.Manifest
import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.os.Looper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlin.concurrent.thread
import com.example.kampus.ui.theme.ThemeController

// Palette definition
internal data class ComposerPalette(
    val bg: Color,
    val card: Color,
    val border: Color,
    val primary: Color,
    val text: Color,
    val textMuted: Color,
    val placeholder: Color,
)

@Composable
internal fun getComposerPalette(): ComposerPalette {
    val isDark = ThemeController.isDark
    return remember(isDark) {
        if (isDark) {
            ComposerPalette(
                bg = Color(0xFF080B11),
                card = Color(0xFF0F1520),
                border = Color(0xFF1A2333),
                primary = Color(0xFF3B82F6),
                text = Color(0xFFFFFFFF),
                textMuted = Color(0xFF9CA3AF),
                placeholder = Color(0xFF374151),
            )
        } else {
            ComposerPalette(
                bg = Color(0xFFF3F4F8),
                card = Color(0xFFFFFFFF),
                border = Color(0xFFD1D5DB),
                primary = Color(0xFF3B82F6),
                text = Color(0xFF111827),
                textMuted = Color(0xFF6B7280),
                placeholder = Color(0xFF9CA3AF),
            )
        }
    }
}

internal val FbDark = ComposerPalette(
    bg = Color(0xFF080B11),
    card = Color(0xFF0F1520),
    border = Color(0xFF1A2333),
    primary = Color(0xFF3B82F6),
    text = Color(0xFFFFFFFF),
    textMuted = Color(0xFF9CA3AF),
    placeholder = Color(0xFF374151),
)

internal val FbLight = ComposerPalette(
    bg = Color(0xFFFFFFFF),
    card = Color(0xFFF3F4F6),
    border = Color(0xFFE5E7EB),
    primary = Color(0xFF1877F2),
    text = Color(0xFF111827),
    textMuted = Color(0xFF6B7280),
    placeholder = Color(0xFF9CA3AF),
)

// Mock data
private val mockFriends = listOf("John", "Sarah", "Mike", "Emma", "Carlos", "Luna", "Jacob", "Mia")
private val mockLocations = listOf(
    "Phnom Penh",
    "Banteay Meanchey",
    "Battambang",
    "Kampong Cham",
    "Kampong Chhnang",
    "Kampong Speu",
    "Kampong Thom",
    "Kampot",
    "Kandal",
    "Kep",
    "Koh Kong",
    "Kratie",
    "Mondulkiri",
    "Oddar Meanchey",
    "Pailin",
    "Preah Sihanouk",
    "Preah Vihear",
    "Prey Veng",
    "Pursat",
    "Ratanakiri",
    "Siem Reap",
    "Stung Treng",
    "Svay Rieng",
    "Takeo",
    "Tboung Khmum",
)
private val mockEvents = listOf("Birthday", "Anniversary", "Wedding", "Graduation", "New Job", "Vacation", "Festival", "Concert", "Sports Event", "Movie Night", "Dinner", "Party")
private val feelingEmojis = listOf(
    "😊", "😂", "❤️", "😍", "🤔", "😎", "🥳", "😴",
    "😤", "😢", "😱", "🤨", "😌", "🤗", "😳", "🙃"
)
private data class GifItem(val url: String, val title: String, val id: String)

// Reliable GIF URLs that work well
private val allGifs = listOf(
    GifItem("https://media0.giphy.com/media/3o85xIO33l7RlmLY1i/giphy-downsized.gif", "Thumbs Up", "1"),
    GifItem("https://media0.giphy.com/media/g9GznKK0ZX9zS/giphy-downsized.gif", "Happy Dance", "2"),
    GifItem("https://media0.giphy.com/media/l0NwPo3jdv8DDcAko/giphy-downsized.gif", "Laughing", "3"),
    GifItem("https://media0.giphy.com/media/l0HlNaQ9SnLEFagic/giphy-downsized.gif", "Clapping", "4"),
    GifItem("https://media0.giphy.com/media/3ohzdKfA0K7TT9qEJa/giphy-downsized.gif", "Waving", "5"),
    GifItem("https://media0.giphy.com/media/10tIjpzIu8fe0/giphy-downsized.gif", "Thinking", "6"),
    GifItem("https://media0.giphy.com/media/26uf1EUQzrPSAWmQ0/giphy-downsized.gif", "Fire", "7"),
    GifItem("https://media0.giphy.com/media/7Jp8V2pYvYzUi/giphy-downsized.gif", "Star", "8"),
    GifItem("https://media0.giphy.com/media/26BoCwvDMRf4HzDOo/giphy-downsized.gif", "Heart", "9"),
    GifItem("https://media0.giphy.com/media/3oKHWikxKmJiBHS19i/giphy-downsized.gif", "Party", "10"),
    GifItem("https://media0.giphy.com/media/jUwpNzg9IcyrK/giphy-downsized.gif", "Crying", "11"),
    GifItem("https://media0.giphy.com/media/l3q2K5jinAlZ9halO/giphy-downsized.gif", "Shocked", "12"),
    GifItem("https://media0.giphy.com/media/4Z9fSEFAuxpnm/giphy-downsized.gif", "Love", "13"),
    GifItem("https://media0.giphy.com/media/3o6Zt6KHxJTbXCnSvu/giphy-downsized.gif", "Cool", "14"),
    GifItem("https://media0.giphy.com/media/b5iP0mxC5g1xVXHJr5/giphy-downsized.gif", "Nope", "15"),
    GifItem("https://media0.giphy.com/media/rKeJMAq4zsJQA/giphy-downsized.gif", "Celebration", "16"),
    GifItem("https://media0.giphy.com/media/eMu6kVlr2vAFu/giphy-downsized.gif", "Cool Guy", "17"),
    GifItem("https://media0.giphy.com/media/d3aYslY45yT6sW8eYM/giphy-downsized.gif", "Excited", "18"),
    GifItem("https://media0.giphy.com/media/2gtoSIzMyJmg4rTg47/giphy-downsized.gif", "Haha", "19"),
    GifItem("https://media0.giphy.com/media/xT9IgEx8SbQ0teblQU/giphy-downsized.gif", "Nice", "20"),
)

@Composable
internal fun FeelingEmojiPicker(
    p: ComposerPalette,
    onSelect: (String) -> Unit,
    onClose: () -> Unit,
) {
    val strings = com.example.kampus.ui.localization.rememberUiStrings()
    var search by remember { mutableStateOf("") }
    val feelingLabels = mapOf(
        "😊" to "happy", "😂" to "laughing", "❤️" to "love", "😍" to "love struck",
        "🤔" to "thinking", "😎" to "cool", "🥳" to "party", "😴" to "tired",
        "😤" to "frustrated", "😢" to "sad", "😱" to "shocked", "🤨" to "skeptical",
        "😌" to "peaceful", "🤗" to "hugging", "😳" to "blushing", "🙃" to "smirk"
    )
    val filtered = feelingEmojis.filter { emoji ->
        feelingLabels[emoji]?.contains(search, ignoreCase = true) == true ||
                search.isEmpty()
    }

    Surface(color = p.bg) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(strings.chooseYourFeeling, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(p.card)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Close, "Close", tint = p.text, modifier = Modifier.size(16.dp))
                }
            }

            HorizontalDivider(color = p.border.copy(alpha = 0.5f), thickness = 0.5.dp)

            // Search box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(p.card)
                    .border(1.dp, p.border, RoundedCornerShape(10.dp))
                    .padding(12.dp),
            ) {
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    textStyle = TextStyle(color = p.text, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (search.isBlank()) {
                            Text(strings.searchFeelings, color = p.placeholder, fontSize = 14.sp)
                        }
                        inner()
                    },
                )
            }

            // Emoji grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(filtered) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(p.card)
                            .border(1.dp, p.border, RoundedCornerShape(12.dp))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                onSelect(emoji)
                                onClose()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(emoji, fontSize = 28.sp)
                    }
                }
            }
        }
    }
}

private data class TagUserItem(
    val id: String = "",
    val displayName: String = "",
    val handle: String = "",
    val profileImageUrl: String = "",
    val subtitle: String = "",
)

private val defaultTagUsers = listOf(
    TagUserItem("u1", "John", "@john.doe", "", "@john.doe"),
    TagUserItem("u2", "Sarah", "@sarah.wilson", "", "@sarah.wilson"),
    TagUserItem("u3", "Mike", "@mike_smith", "", "@mike_smith"),
    TagUserItem("u4", "Emma", "@emma.jones", "", "@emma.jones"),
    TagUserItem("u5", "Carlos", "@carlos_c", "", "3 mutual friends"),
    TagUserItem("u6", "Luna", "@luna_m", "", "2 mutual friends"),
    TagUserItem("u7", "Jacob", "@jacob_lee", "", "@jacob_lee"),
    TagUserItem("u8", "Mia", "@mia_k", "", "@mia_k"),
)

@Composable
internal fun PeoplePicker(
    p: ComposerPalette,
    selected: List<String>,
    onSelect: (List<String>) -> Unit,
    onClose: () -> Unit,
) {
    var localSelected by remember { mutableStateOf(selected) }
    val strings = com.example.kampus.ui.localization.rememberUiStrings()
    var search by remember { mutableStateOf("") }
    var usersList by remember { mutableStateOf<List<TagUserItem>>(emptyList()) }

    DisposableEffect(Unit) {
        val db = runCatching { com.google.firebase.firestore.FirebaseFirestore.getInstance() }.getOrNull()
        val currentUid = runCatching { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }.getOrDefault("")

        var friendIds = emptySet<String>()
        var followingIds = emptySet<String>()

        var friendsListener: com.google.firebase.firestore.ListenerRegistration? = null
        var followingListener: com.google.firebase.firestore.ListenerRegistration? = null
        var usersListener: com.google.firebase.firestore.ListenerRegistration? = null

        fun rebuildUsersList(snap: com.google.firebase.firestore.QuerySnapshot?) {
            if (snap == null || snap.isEmpty) {
                if (usersList.isEmpty()) usersList = defaultTagUsers
                return
            }
            val fetched = snap.documents.mapNotNull { doc ->
                if (doc.id == currentUid) return@mapNotNull null
                val name = doc.getString("displayName") ?: doc.getString("name") ?: ""
                if (name.isBlank()) return@mapNotNull null
                val rawHandle = doc.getString("handle") ?: doc.getString("username") ?: ""
                val handle = if (rawHandle.startsWith("@")) rawHandle else if (rawHandle.isNotBlank()) "@$rawHandle" else ""
                val faculty = doc.getString("faculty") ?: doc.getString("major") ?: ""
                val photoUrl = doc.getString("profileImageUrl")
                    ?: doc.getString("photoUrl")
                    ?: doc.getString("avatarUrl")
                    ?: doc.getString("profile_image_url")
                    ?: ""

                val isFriend = doc.id in friendIds
                val isFollowing = doc.id in followingIds
                val statusSubtitle = when {
                    isFriend -> "Friend · ${handle.ifBlank { faculty.ifBlank { "Student" } }}"
                    isFollowing -> "Following · ${handle.ifBlank { faculty.ifBlank { "Student" } }}"
                    handle.isNotBlank() -> handle
                    faculty.isNotBlank() -> faculty
                    else -> "Student"
                }

                Pair(
                    isFriend || isFollowing,
                    TagUserItem(
                        id = doc.id,
                        displayName = name,
                        handle = handle,
                        profileImageUrl = photoUrl,
                        subtitle = statusSubtitle
                    )
                )
            }.sortedByDescending { it.first }.map { it.second }

            usersList = if (fetched.isNotEmpty()) fetched else defaultTagUsers
        }

        if (db != null && currentUid.isNotBlank()) {
            friendsListener = db.collection("users").document(currentUid)
                .collection("friends")
                .addSnapshotListener { snap, _ ->
                    friendIds = snap?.documents?.map { it.id }?.toSet().orEmpty()
                }

            followingListener = db.collection("users").document(currentUid)
                .collection("following")
                .addSnapshotListener { snap, _ ->
                    followingIds = snap?.documents?.map { it.id }?.toSet().orEmpty()
                }

            usersListener = db.collection("users")
                .limit(100)
                .addSnapshotListener { snap, err ->
                    if (err == null && snap != null) {
                        rebuildUsersList(snap)
                    }
                }
        } else {
            usersList = defaultTagUsers
        }

        onDispose {
            friendsListener?.remove()
            followingListener?.remove()
            usersListener?.remove()
        }
    }

    val displayList = remember(usersList) {
        if (usersList.isEmpty()) defaultTagUsers else usersList
    }

    val filtered = remember(search, displayList) {
        if (search.isBlank()) displayList
        else {
            val q = search.trim().lowercase()
            displayList.filter {
                it.displayName.lowercase().contains(q) ||
                        it.handle.lowercase().contains(q) ||
                        it.subtitle.lowercase().contains(q)
            }
        }
    }

    val selectedUsersMap = remember(localSelected, displayList) {
        localSelected.map { sel ->
            displayList.firstOrNull { it.displayName.equals(sel, ignoreCase = true) || it.id == sel || it.handle.equals(sel, ignoreCase = true) }
                ?: TagUserItem(id = sel, displayName = sel, handle = "", profileImageUrl = "", subtitle = "")
        }
    }

    Surface(color = p.bg, modifier = Modifier.fillMaxHeight(0.85f)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(strings.tagPeople, color = p.text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(p.card)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            onSelect(localSelected)
                            onClose()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Close, "Close", tint = p.text, modifier = Modifier.size(16.dp))
                }
            }

            HorizontalDivider(color = p.border.copy(alpha = 0.4f), thickness = 0.5.dp)

            // Search box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(p.card)
                    .border(1.dp, p.border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = p.placeholder,
                        modifier = Modifier.size(18.dp)
                    )
                    BasicTextField(
                        value = search,
                        onValueChange = { search = it },
                        textStyle = TextStyle(color = p.text, fontSize = 14.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        decorationBox = { inner ->
                            if (search.isBlank()) {
                                Text(strings.searchFriends, color = p.placeholder, fontSize = 14.sp)
                            }
                            inner()
                        },
                    )
                }
            }

            // Scrollable Friends list
            Box(modifier = Modifier.weight(1f)) {
                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(strings.noFriendsFound, color = p.textMuted, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        lazyItems(filtered, key = { it.id.ifBlank { it.displayName } }) { user ->
                            val isSelected = user.displayName in localSelected || user.id in localSelected || user.handle in localSelected

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(p.card)
                                    .border(1.dp, if (isSelected) p.primary.copy(alpha = 0.5f) else p.border, RoundedCornerShape(14.dp))
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        val tagKey = user.displayName
                                        localSelected = if (isSelected) {
                                            localSelected.filterNot { it == tagKey || it == user.id || it == user.handle }
                                        } else {
                                            localSelected + tagKey
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                // Real Firebase Avatar / Standard Person Placeholder
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E2240)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (user.profileImageUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = user.profileImageUrl,
                                            contentDescription = user.displayName,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = p.textMuted,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // Name and subtitle/handle
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.displayName,
                                        color = p.text,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    if (user.subtitle.isNotBlank()) {
                                        Text(
                                            text = user.subtitle,
                                            color = p.textMuted,
                                            fontSize = 12.sp,
                                        )
                                    }
                                }

                                // Radio/Checkmark selection indicator
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) p.primary else Color.Transparent)
                                        .border(2.dp, if (isSelected) p.primary else p.border, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (isSelected) {
                                        Text(
                                            text = "✓",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Selected Bar + Action Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(p.bg)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (localSelected.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${localSelected.size} selected",
                                color = p.text,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )

                             // Avatar preview badges row
                            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                selectedUsersMap.take(4).forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, p.bg, CircleShape)
                                            .background(Color(0xFF1E2240)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (item.profileImageUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = item.profileImageUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = p.textMuted,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Clear",
                            color = p.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                localSelected = emptyList()
                            }
                        )
                    }
                }

                // Done button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(p.primary)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            onSelect(localSelected)
                            onClose()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Done",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun LocationPicker(
    p: ComposerPalette,
    onSelect: (String) -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val strings = com.example.kampus.ui.localization.rememberUiStrings()
    var currentLocation by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasPermission by remember { mutableStateOf(false) }
    val cambodiaKeyword = "cambodia"

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (hasPermission) {
                isLoading = true
            } else {
                isLoading = false
                currentLocation = "Permission denied"
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Set up real-time location fetching when permissions granted
    DisposableEffect(hasPermission) {
        if (hasPermission) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdateDelayMillis(1500)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    isLoading = false
                    
                    thread {
                        try {
                            val geocoder = Geocoder(context)
                            val lat = location.latitude
                            val lng = location.longitude
                            
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(lat, lng, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                val locationName = buildString {
                                    if (address.thoroughfare != null) {
                                        append(address.thoroughfare)
                                        if (address.subThoroughfare != null) {
                                            append(" ")
                                            append(address.subThoroughfare)
                                        }
                                    }
                                    if (address.locality != null) {
                                        if (isNotEmpty()) append(", ")
                                        append(address.locality)
                                    } else if (address.adminArea != null) {
                                        if (isNotEmpty()) append(", ")
                                        append(address.adminArea)
                                    }
                                    if (address.countryName != null) {
                                        if (isNotEmpty()) append(", ")
                                        append(address.countryName)
                                    }
                                }
                                val countryName = address.countryName.orEmpty()
                                currentLocation = if (countryName.contains(cambodiaKeyword, ignoreCase = true)) {
                                    if (locationName.isNotEmpty()) locationName else "$lat, $lng"
                                } else {
                                    null
                                }
                            } else {
                                currentLocation = null
                            }
                        } catch (e: Exception) {
                            currentLocation = null
                        }
                    }
                }
            }

            @Suppress("MissingPermission")
            fusedClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

            onDispose {
                fusedClient.removeLocationUpdates(locationCallback)
            }
        } else {
            onDispose { }
        }
    }



    Surface(color = p.bg) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(strings.addLocationTitle, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(p.card)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Close, "Close", tint = p.text, modifier = Modifier.size(16.dp))
                }
            }

            HorizontalDivider(color = p.border.copy(alpha = 0.5f), thickness = 0.5.dp)

            // Search box for real-time location search
            var searchQuery by remember { mutableStateOf("") }
            val filteredLocations = mockLocations.filter { location ->
                location.contains(searchQuery, ignoreCase = true)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(p.card)
                    .border(1.dp, p.border, RoundedCornerShape(10.dp))
                    .padding(12.dp),
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(color = p.text, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (searchQuery.isBlank()) {
                            Text(strings.searchLocations, color = p.placeholder, fontSize = 14.sp)
                        }
                        inner()
                    },
                )
            }

            // Current location section (if available and search is empty)
            if (searchQuery.isBlank()) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(p.card)
                            .border(1.dp, p.border, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📍", fontSize = 24.sp)
                            Text(strings.gettingYourLocation, color = p.textMuted, fontSize = 13.sp)
                        }
                    }
                } else if (currentLocation != null && !currentLocation!!.contains("Permission") && !currentLocation!!.contains("denied")) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(p.primary.copy(alpha = 0.12f))
                            .border(1.5.dp, p.primary, RoundedCornerShape(12.dp))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                onSelect(currentLocation!!)
                                onClose()
                            }
                            .padding(16.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("📍", fontSize = 24.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(strings.myLocation, color = p.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    currentLocation!!,
                                    color = p.text,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = p.border.copy(alpha = 0.3f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
            }

            // Real-time search results - scrollable list
            if (filteredLocations.isEmpty() && searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(strings.noLocationsFound, color = p.textMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    lazyItems(filteredLocations) { location ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(p.card)
                                .border(1.dp, p.border, RoundedCornerShape(10.dp))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    onSelect(location)
                                    onClose()
                                }
                                .padding(12.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📍", fontSize = 16.sp)
                                Text(location, color = p.text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
internal fun GifPicker(
    p: ComposerPalette,
    onSelect: (String) -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val strings = com.example.kampus.ui.localization.rememberUiStrings()
    var search by remember { mutableStateOf("") }
    
    // Filter GIFs locally - instant, no network calls
    val filteredGifs = if (search.isEmpty()) {
        allGifs
    } else {
        allGifs.filter { gif ->
            gif.title.contains(search, ignoreCase = true)
        }
    }

    Surface(color = p.bg) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(strings.findGif, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(p.card)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Close, "Close", tint = p.text, modifier = Modifier.size(16.dp))
                }
            }

            HorizontalDivider(color = p.border.copy(alpha = 0.5f), thickness = 0.5.dp)

            // Search box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(p.card)
                    .border(1.dp, p.border, RoundedCornerShape(10.dp))
                    .padding(12.dp),
            ) {
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    textStyle = TextStyle(color = p.text, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (search.isBlank()) {
                            Text(strings.searchGifs, color = p.placeholder, fontSize = 14.sp)
                        }
                        inner()
                    },
                )
            }

            // No results message
            if (filteredGifs.isEmpty() && search.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${strings.noGifsFound}\n\"$search\"", color = p.textMuted, fontSize = 13.sp)
                }
            }

            // GIF grid - instant local search
            if (filteredGifs.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    items(filteredGifs) { gif ->
                        var imageLoading by remember { mutableStateOf(false) }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.3f),
                                    spotColor = Color.Black.copy(alpha = 0.4f)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .background(p.card)
                                .border(1.dp, p.border.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    onSelect(gif.title)
                                    onClose()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            // Loading skeleton placeholder
                            if (imageLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .background(
                                            color = p.card.copy(alpha = 0.6f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⏳", fontSize = 20.sp)
                                }
                            }
                            
                            // GIF image with better caching
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(gif.url)
                                    .crossfade(durationMillis = 150)
                                    .allowHardware(true)
                                    .build(),
                                contentDescription = gif.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                onLoading = { imageLoading = true },
                                onSuccess = { imageLoading = false },
                                onError = { imageLoading = false }
                            )
                            
                            // Dark overlay for text visibility
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(Color.Black.copy(alpha = 0.2f))
                            )

                            // GIF title at bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    gif.title,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun EventPicker(
    p: ComposerPalette,
    onSelect: (String) -> Unit,
    onClose: () -> Unit,
) {
    val strings = com.example.kampus.ui.localization.rememberUiStrings()
    var search by remember { mutableStateOf("") }
    val filtered = mockEvents.filter { it.contains(search, ignoreCase = true) }

    Surface(color = p.bg) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(strings.searchEvent, color = p.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(p.card)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Close, "Close", tint = p.text, modifier = Modifier.size(16.dp))
                }
            }

            HorizontalDivider(color = p.border.copy(alpha = 0.5f), thickness = 0.5.dp)

            // Search box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(p.card)
                    .border(1.dp, p.border, RoundedCornerShape(10.dp))
                    .padding(12.dp),
            ) {
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    textStyle = TextStyle(color = p.text, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (search.isBlank()) {
                            Text(strings.searchEvent, color = p.placeholder, fontSize = 14.sp)
                        }
                        inner()
                    },
                )
            }

            // Results - scrollable list
            if (filtered.isEmpty() && search.isNotEmpty()) {
                Text(strings.noEventsFound, color = p.textMuted, fontSize = 13.sp, modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    lazyItems(if (search.isEmpty()) mockEvents else filtered) { event ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(p.card)
                                .border(1.dp, p.border, RoundedCornerShape(10.dp))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    onSelect(event)
                                    onClose()
                                }
                                .padding(12.dp),
                        ) {
                            Text(event, color = p.text, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
