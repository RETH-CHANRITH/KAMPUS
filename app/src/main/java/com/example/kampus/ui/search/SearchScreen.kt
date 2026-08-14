@file:Suppress("SpellCheckingInspection")
package com.example.kampus.ui.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kampus.utils.ProfileImageUtils
import java.text.SimpleDateFormat
import java.util.*

// ── Design tokens ─────────────────────────────────────────────────────────────

private val BgPage        = Color(0xFF0A0C12)
private val SearchBarBg   = Color(0xFF14161E)
private val SearchBarBdr  = Color(0xFF23263A)
private val TabActiveBg   = Color(0xFF1A3AFF)   // solid blue for active tab pill
private val TabInactiveBg = Color(0xFF14161E)
private val TabBdr        = Color(0xFF23263A)
private val CardBg        = Color(0xFF111318)
private val CardBdr       = Color(0xFF1E2130)
private val Accent        = Color(0xFF1A6AFF)
private val AccentLight   = Color(0xFF4C8FFF)
private val TextWhite     = Color(0xFFEFF0F3)
private val TextGray      = Color(0xFF8A8D9A)
private val TextLabel     = Color(0xFF5A5D6A)
private val GreenFollow   = Color(0xFF22C55E)
private val ShimmerBg     = Color(0xFF161820)
private val ShimmerHi     = Color(0xFF22252F)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onPostClick: (String) -> Unit = {},
    onProfileClick: (String) -> Unit = {},
    onGroupClick: (String) -> Unit = {},
    onEventClick: (String) -> Unit = {},
    viewModel: SearchViewModel = viewModel(),
) {
    val state        = viewModel.uiState.collectAsStateWithLifecycle().value
    val focusRequester = remember { FocusRequester() }
    val focusManager   = LocalFocusManager.current

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            // ── Search bar row ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Back icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SearchBarBg)
                        .border(1.dp, SearchBarBdr, CircleShape)
                        .clickable {
                            focusManager.clearFocus()
                            onBack()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Search field
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SearchBarBg)
                        .border(1.dp, SearchBarBdr, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    BasicTextField(
                        value = state.query,
                        onValueChange = viewModel::onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        singleLine = true,
                        textStyle = TextStyle(
                            color     = TextWhite,
                            fontSize  = 15.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        cursorBrush = SolidColor(AccentLight),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        decorationBox = { inner ->
                            Box {
                                if (state.query.isEmpty()) {
                                    Text(
                                        "Search posts, people, groups…",
                                        color = TextLabel,
                                        fontSize = 15.sp,
                                    )
                                }
                                inner()
                            }
                        }
                    )
                    AnimatedVisibility(visible = state.query.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Clear",
                            tint = TextGray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { viewModel.onClearQuery() }
                        )
                    }
                }

                // Cancel text
                AnimatedVisibility(visible = state.query.isNotEmpty()) {
                    Text(
                        text = "Cancel",
                        color = AccentLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                viewModel.onClearQuery()
                                focusManager.clearFocus()
                                onBack()
                            }
                    )
                }
            }

            // ── Filter tabs ───────────────────────────────────────────────────
            AnimatedVisibility(visible = state.query.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                ) {
                    items(SearchTab.entries) { tab ->
                        FilterTabChip(
                            label  = tab.label,
                            active = state.selectedTab == tab,
                            onClick = { viewModel.onTabSelect(tab) }
                        )
                    }
                }
            }

            // ── Content ───────────────────────────────────────────────────────
            AnimatedContent(
                targetState = state.query.isEmpty(),
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                label = "search_content"
            ) { isEmpty ->
                if (isEmpty) {
                    EmptyQueryState()
                } else {
                    SearchResultsContent(
                        state         = state,
                        onPostClick   = onPostClick,
                        onProfileClick = onProfileClick,
                        onGroupClick  = onGroupClick,
                        onEventClick  = onEventClick,
                        onFollow      = { viewModel.toggleFollow(it) },
                        onJoinGroup   = { viewModel.toggleJoinGroup(it) },
                    )
                }
            }
        }
    }
}

// ── Tab chip ──────────────────────────────────────────────────────────────────

@Composable
private fun FilterTabChip(label: String, active: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        if (active) TabActiveBg else TabInactiveBg,
        animationSpec = tween(180), label = "tab_color"
    )
    val textColor by animateColorAsState(
        if (active) Color.White else TextGray,
        animationSpec = tween(180), label = "tab_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .then(if (!active) Modifier.border(1.dp, TabBdr, RoundedCornerShape(20.dp)) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = label,
            color      = textColor,
            fontSize   = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

// ── Empty query / discover state ──────────────────────────────────────────────

@Composable
private fun EmptyQueryState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = TextLabel,
                modifier = Modifier.size(52.dp)
            )
            Text("Search Kampus", color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "Find posts, people, groups, events and trending tags",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

// ── Results layout ────────────────────────────────────────────────────────────

@Composable
private fun SearchResultsContent(
    state: SearchUiState,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onGroupClick: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onFollow: (String) -> Unit,
    onJoinGroup: (String) -> Unit,
) {
    if (state.isLoading) {
        SearchShimmer()
        return
    }

    val noResults = state.posts.isEmpty() && state.users.isEmpty() &&
        state.groups.isEmpty() && state.events.isEmpty() && state.tags.isEmpty()

    if (noResults) {
        NoResultsState(query = state.query)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val tab = state.selectedTab

        // ── Posts section ─────────────────────────────────────────────────────
        if ((tab == SearchTab.ALL || tab == SearchTab.POSTS) && state.posts.isNotEmpty()) {
            item(key = "header_posts") {
                SectionHeader(
                    title   = "Top Posts",
                    count   = state.posts.size,
                    showSeeAll = state.posts.size > 3 && tab == SearchTab.ALL,
                    onSeeAll = {},
                )
            }
            val postsToShow = if (tab == SearchTab.ALL) state.posts.take(3) else state.posts
            items(postsToShow, key = { "post_${it.id}" }) { post ->
                PostResultCard(
                    post    = post,
                    query   = state.query,
                    onClick = { onPostClick(post.id) }
                )
            }
        }

        // ── People section ────────────────────────────────────────────────────
        if ((tab == SearchTab.ALL || tab == SearchTab.PEOPLE) && state.users.isNotEmpty()) {
            item(key = "header_people") {
                SectionHeader(
                    title    = "People",
                    count    = state.users.size,
                    showSeeAll = state.users.size > 4 && tab == SearchTab.ALL,
                    onSeeAll = {},
                    topPad   = if (tab == SearchTab.ALL) 12.dp else 0.dp,
                )
            }

            if (tab == SearchTab.ALL) {
                item(key = "people_row") {
                    PeopleRow(
                        users      = state.users.take(5),
                        query      = state.query,
                        onProfile  = onProfileClick,
                        onFollow   = onFollow,
                    )
                }
            } else {
                items(state.users, key = { "user_${it.id}" }) { user ->
                    PeopleListCard(
                        user     = user,
                        query    = state.query,
                        onProfile = { onProfileClick(user.id) },
                        onFollow = { onFollow(user.id) },
                    )
                }
            }
        }

        // ── Groups section ────────────────────────────────────────────────────
        if ((tab == SearchTab.ALL || tab == SearchTab.GROUPS) && state.groups.isNotEmpty()) {
            item(key = "header_groups") {
                SectionHeader(
                    title    = "Groups",
                    count    = state.groups.size,
                    showSeeAll = state.groups.size > 3 && tab == SearchTab.ALL,
                    onSeeAll = {},
                    topPad   = if (tab == SearchTab.ALL) 12.dp else 0.dp,
                )
            }
            val groupsToShow = if (tab == SearchTab.ALL) state.groups.take(3) else state.groups
            items(groupsToShow, key = { "group_${it.id}" }) { group ->
                GroupResultCard(
                    group   = group,
                    query   = state.query,
                    onClick = { onGroupClick(group.id) },
                    onJoin  = { onJoinGroup(group.id) },
                )
            }
        }

        // ── Events section ────────────────────────────────────────────────────
        if ((tab == SearchTab.ALL || tab == SearchTab.EVENTS) && state.events.isNotEmpty()) {
            item(key = "header_events") {
                SectionHeader(
                    title    = "Events",
                    count    = state.events.size,
                    showSeeAll = state.events.size > 3 && tab == SearchTab.ALL,
                    onSeeAll = {},
                    topPad   = if (tab == SearchTab.ALL) 12.dp else 0.dp,
                )
            }
            val eventsToShow = if (tab == SearchTab.ALL) state.events.take(3) else state.events
            items(eventsToShow, key = { "event_${it.id}" }) { event ->
                EventResultCard(
                    event   = event,
                    query   = state.query,
                    onClick = { onEventClick(event.id) },
                )
            }
        }

        // ── Tags section ──────────────────────────────────────────────────────
        if ((tab == SearchTab.ALL || tab == SearchTab.TAGS) && state.tags.isNotEmpty()) {
            item(key = "header_tags") {
                SectionHeader(
                    title    = "Tags",
                    count    = state.tags.size,
                    showSeeAll = false,
                    onSeeAll = {},
                    topPad   = if (tab == SearchTab.ALL) 12.dp else 0.dp,
                )
            }
            items(state.tags, key = { "tag_${it.tag}" }) { tagItem ->
                TagResultRow(tagItem = tagItem)
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    showSeeAll: Boolean,
    onSeeAll: () -> Unit,
    topPad: androidx.compose.ui.unit.Dp = 0.dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPad, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text       = title,
            color      = TextWhite,
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        if (showSeeAll) {
            Text(
                text       = "See all",
                color      = AccentLight,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier   = Modifier.clickable(onClick = onSeeAll),
            )
        }
    }
}

// ── Post result card ──────────────────────────────────────────────────────────

@Composable
private fun PostResultCard(post: SearchPost, query: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, CardBdr, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Avatar
        AvatarCircle(url = post.authorProfileUrl, emoji = post.authorAvatar, name = post.authorName, size = 38.dp)

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text       = post.authorName,
                    color      = TextWhite,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                )
                if (post.campus.isNotBlank()) {
                    Text("·", color = TextLabel, fontSize = 12.sp)
                    Text(
                        text     = post.campus,
                        color    = TextLabel,
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text     = formatShortDate(post.timestamp),
                    color    = TextLabel,
                    fontSize = 11.sp,
                )
            }
            Spacer(Modifier.height(4.dp))
            HighlightedText(
                text       = post.content,
                query      = query,
                maxLines   = 2,
                fontSize   = 13.sp,
                baseColor  = TextGray,
            )
            Spacer(Modifier.height(8.dp))
            // Engagement row
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                EngagementStat(icon = Icons.Outlined.FavoriteBorder, value = post.likes)
                EngagementStat(icon = Icons.Outlined.ChatBubbleOutline, value = post.comments)
                EngagementStat(icon = Icons.Outlined.Send, value = post.shares)
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector    = Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint           = TextLabel,
                    modifier       = Modifier.size(16.dp)
                )
            }
        }

        // Thumbnail
        if (post.firstImageUrl.isNotBlank()) {
            AsyncImage(
                model          = post.firstImageUrl,
                contentDescription = null,
                modifier       = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2030)),
                contentScale   = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun EngagementStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextLabel, modifier = Modifier.size(14.dp))
        Text(text = value.toString(), color = TextLabel, fontSize = 12.sp)
    }
}

// ── People row (horizontal in All tab) ───────────────────────────────────────

@Composable
private fun PeopleRow(
    users: List<SearchUser>,
    query: String,
    onProfile: (String) -> Unit,
    onFollow: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 4.dp),
    ) {
        items(users, key = { "pr_${it.id}" }) { user ->
            PersonCard(user = user, query = query, onProfile = { onProfile(user.id) }, onFollow = { onFollow(user.id) })
        }
    }
}

@Composable
private fun PersonCard(user: SearchUser, query: String, onProfile: () -> Unit, onFollow: () -> Unit) {
    Column(
        modifier = Modifier
            .width(95.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, CardBdr, RoundedCornerShape(14.dp))
            .clickable(onClick = onProfile)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AvatarCircle(url = user.profileImageUrl, emoji = user.avatarEmoji, name = user.displayName, size = 52.dp)
        Text(
            text       = user.displayName,
            color      = TextWhite,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis,
        )
        Text(
            text     = user.role.ifBlank { "Student" },
            color    = TextGray,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        FollowButton(isFollowing = user.isFollowing, compact = true, onClick = onFollow)
    }
}

// ── People list card (in People tab) ─────────────────────────────────────────

@Composable
private fun PeopleListCard(
    user: SearchUser,
    query: String,
    onProfile: () -> Unit,
    onFollow: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, CardBdr, RoundedCornerShape(14.dp))
            .clickable(onClick = onProfile)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AvatarCircle(url = user.profileImageUrl, emoji = user.avatarEmoji, name = user.displayName, size = 46.dp)
        Column(modifier = Modifier.weight(1f)) {
            HighlightedText(text = user.displayName, query = query, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, baseColor = TextWhite)
            Spacer(Modifier.height(1.dp))
            Text(text = user.handle, color = AccentLight, fontSize = 12.sp)
            if (user.faculty.isNotBlank()) {
                Text(text = user.faculty, color = TextGray, fontSize = 12.sp)
            }
        }
        FollowButton(isFollowing = user.isFollowing, compact = false, onClick = onFollow)
    }
}

// ── Group result card ─────────────────────────────────────────────────────────

@Composable
private fun GroupResultCard(group: SearchGroup, query: String, onClick: () -> Unit, onJoin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, CardBdr, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Group emoji icon
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E2240)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = group.coverEmoji, fontSize = 22.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            HighlightedText(text = group.name, query = query, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, baseColor = TextWhite)
            Text(text = "Group · ${group.members} members", color = TextGray, fontSize = 12.sp)
            if (group.description.isNotBlank()) {
                Text(
                    text     = group.description,
                    color    = TextLabel,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Join button
        val isJoined = group.isJoined
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isJoined) SearchBarBg else Accent)
                .border(1.dp, if (isJoined) CardBdr else Color.Transparent, RoundedCornerShape(8.dp))
                .clickable(onClick = onJoin)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = if (isJoined) "Joined" else "Join",
                color      = if (isJoined) TextGray else Color.White,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ── Event result card ─────────────────────────────────────────────────────────

@Composable
private fun EventResultCard(event: SearchEvent, query: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, CardBdr, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (event.imageUrl.isNotBlank()) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2240)),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2240)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Event, contentDescription = null, tint = AccentLight, modifier = Modifier.size(26.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            HighlightedText(text = event.title, query = query, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, baseColor = TextWhite)
            if (event.startDate > 0L) {
                Text(text = formatEventDate(event.startDate), color = AccentLight, fontSize = 12.sp)
            }
            if (event.location.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = TextLabel, modifier = Modifier.size(12.dp))
                    Text(text = event.location, color = TextGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextLabel, modifier = Modifier.size(20.dp))
    }
}

// ── Tag result row ────────────────────────────────────────────────────────────

@Composable
private fun TagResultRow(tagItem: SearchTag) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.dp, CardBdr, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A2850)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Tag, contentDescription = null, tint = AccentLight, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "#${tagItem.tag}", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "${tagItem.postCount} posts", color = TextGray, fontSize = 12.sp)
        }
    }
}

// ── Follow button ─────────────────────────────────────────────────────────────

@Composable
private fun FollowButton(isFollowing: Boolean, compact: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        if (isFollowing) SearchBarBg else Accent,
        animationSpec = tween(200), label = "follow_bg"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, if (isFollowing) CardBdr else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(
                horizontal = if (compact) 10.dp else 16.dp,
                vertical   = if (compact) 5.dp else 7.dp
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = if (isFollowing) "Following" else "Follow",
            color      = if (isFollowing) TextGray else Color.White,
            fontSize   = if (compact) 11.sp else 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ── Avatar ────────────────────────────────────────────────────────────────────

@Composable
private fun AvatarCircle(
    url: String,
    emoji: String,
    name: String = "",
    size: androidx.compose.ui.unit.Dp
) {
    val displayUrl = ProfileImageUtils.getEffectiveProfileImageUrl(name, url)
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF1E2240)),
        contentAlignment = Alignment.Center,
    ) {
        coil.compose.SubcomposeAsyncImage(
            model = displayUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(androidx.compose.material.icons.Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size((size.value * 0.5f).dp))
                }
            }
        )
    }
}

// ── Highlighted text ──────────────────────────────────────────────────────────

@Composable
private fun HighlightedText(
    text: String,
    query: String,
    maxLines: Int = Int.MAX_VALUE,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    baseColor: Color = TextGray,
) {
    if (query.isBlank()) {
        Text(text = text, color = baseColor, fontSize = fontSize, fontWeight = fontWeight, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
        return
    }

    val annotated = buildAnnotatedString {
        val lowerText  = text.lowercase()
        val lowerQuery = query.lowercase()
        var start = 0
        while (start < text.length) {
            val idx = lowerText.indexOf(lowerQuery, start)
            if (idx == -1) {
                withStyle(SpanStyle(color = baseColor, fontWeight = fontWeight)) {
                    append(text.substring(start))
                }
                break
            }
            if (idx > start) {
                withStyle(SpanStyle(color = baseColor, fontWeight = fontWeight)) {
                    append(text.substring(start, idx))
                }
            }
            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, background = Accent.copy(alpha = 0.25f))) {
                append(text.substring(idx, idx + lowerQuery.length))
            }
            start = idx + lowerQuery.length
        }
    }

    Text(
        text     = annotated,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

// ── No results ────────────────────────────────────────────────────────────────

@Composable
private fun NoResultsState(query: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = TextLabel, modifier = Modifier.size(48.dp))
            Text("No results for \"$query\"", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text("Try a different keyword or check your spelling.", color = TextGray, fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

// ── Shimmer ───────────────────────────────────────────────────────────────────

@Composable
private fun SearchShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue  = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label         = "alpha"
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { ShimmerBar(width = 0.3f, height = 14.dp, alpha = alpha) }
        items(3) { ShimmerCard(alpha = alpha) }
        item { Spacer(Modifier.height(8.dp)) }
        item { ShimmerBar(width = 0.25f, height = 14.dp, alpha = alpha) }
        items(3) { ShimmerCard(alpha = alpha, short = true) }
    }
}

@Composable
private fun ShimmerCard(alpha: Float, short: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ShimmerBg)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(42.dp).clip(CircleShape).background(ShimmerHi.copy(alpha = alpha)))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.fillMaxWidth(if (short) 0.4f else 0.6f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(ShimmerHi.copy(alpha = alpha)))
            Box(Modifier.fillMaxWidth(if (short) 0.6f else 0.85f).height(10.dp).clip(RoundedCornerShape(6.dp)).background(ShimmerHi.copy(alpha = alpha * 0.7f)))
        }
        if (!short) {
            Box(Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)).background(ShimmerHi.copy(alpha = alpha * 0.8f)))
        }
    }
}

@Composable
private fun ShimmerBar(width: Float, height: androidx.compose.ui.unit.Dp, alpha: Float) {
    Box(
        Modifier
            .fillMaxWidth(width)
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(ShimmerHi.copy(alpha = alpha))
    )
}

// ── Date formatters ───────────────────────────────────────────────────────────

private fun formatShortDate(ts: Long): String {
    if (ts <= 0L) return ""
    return try {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(ts))
    } catch (_: Exception) { "" }
}

private fun formatEventDate(ts: Long): String {
    if (ts <= 0L) return ""
    return try {
        SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault()).format(Date(ts))
    } catch (_: Exception) { "" }
}
