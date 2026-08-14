package com.example.kampus.ui.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kampus.navigation.Routes

// ── Screen-level tokens ───────────────────────────────────────────────────────

private val BgScreen      = Color(0xFF0D0E12)
private val PillActiveBg  = Color(0xFF1F2128)
private val PillActiveBdr = Color(0xFF3A3D4A)
private val TabLabelOff   = Color(0xFF6B6E7A)
private val TabLabelOn    = Color.White
private val SectionColor  = Color(0xFFCCCDD6)
private val AccentBlue    = Color(0xFF4C8FFF)
private val ShimmerBase   = Color(0xFF1A1C24)
private val ShimmerHigh   = Color(0xFF252830)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: NotificationViewModel = viewModel(),
) {
    val state        = viewModel.uiState.collectAsStateWithLifecycle().value
    val localReadIds = state.localReadIds

    var selectedTab  by remember { mutableStateOf(0) } // 0=All 1=Unread

    val listState    = rememberLazyListState()

    // ── Live timestamp ticker — forces recomposition every 60 s ──────────────
    var tickMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            tickMs = System.currentTimeMillis()
        }
    }

    // Pull-to-refresh
    var isRefreshing by remember { mutableStateOf(false) }
    val ptrState     = rememberPullToRefreshState()
    LaunchedEffect(state.isLoading) { if (!state.isLoading) isRefreshing = false }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgScreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Back
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1C22))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Title
                Text(
                    text = "Notifications",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp)
                )

                // Mark all as read (only when there are unread items)
                val hasUnread = state.groupedNotifications.any { !it.effectiveIsRead(localReadIds) }
                AnimatedVisibility(
                    visible = hasUnread,
                    enter = fadeIn() + slideInHorizontally { it },
                    exit  = fadeOut() + slideOutHorizontally { it },
                ) {
                    Text(
                        text = "Mark all as read",
                        color = AccentBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.markAllAsRead() }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    )
                }
            }

            // ── All / Unread pill tabs ─────────────────────────────────────────
            val unreadCount = state.groupedNotifications.count { !it.effectiveIsRead(localReadIds) }
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TabPill(
                    label  = "All",
                    active = selectedTab == 0,
                    badge  = null,
                    onClick = { selectedTab = 0 },
                )
                TabPill(
                    label  = "Unread",
                    active = selectedTab == 1,
                    badge  = if (unreadCount > 0) unreadCount else null,
                    onClick = { selectedTab = 1 },
                )
            }

            // ── Content ───────────────────────────────────────────────────────
            when {
                // Loading shimmer
                state.isLoading -> {
                    ShimmerList()
                }

                // Error
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = state.error,
                                color = Color(0xFFFF5252),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text("Try again", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // List
                else -> {
                    val filtered = if (selectedTab == 1)
                        state.groupedNotifications.filter { !it.effectiveIsRead(localReadIds) }
                    else
                        state.groupedNotifications

                    if (filtered.isEmpty()) {
                        EmptyState(isUnreadTab = selectedTab == 1)
                    } else {
                        val nowMs        = System.currentTimeMillis()
                        val todayCutoff  = nowMs - 24 * 60 * 60 * 1000L
                        val todayItems   = filtered.filter { it.latestCreatedAt >= todayCutoff }
                        val earlierItems = filtered.filter { it.latestCreatedAt  < todayCutoff }

                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh    = { isRefreshing = true; viewModel.refresh() },
                            state        = ptrState,
                            modifier     = Modifier.fillMaxSize(),
                        ) {
                            LazyColumn(
                                state          = listState,
                                modifier       = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                if (todayItems.isNotEmpty()) {
                                    item(key = "header_today") {
                                        SectionLabel(text = "Today")
                                    }
                                    items(todayItems, key = { "n_${it.id}" }) { item ->
                                        NotificationItemCard(
                                            item         = item,
                                            localReadIds = localReadIds,
                                            tickMs       = tickMs,
                                            onClick      = {
                                                viewModel.markGroupAsRead(item)
                                                navigateForItem(item, onNavigate)
                                            },
                                            onSwipeRead  = { viewModel.markGroupAsRead(item) },
                                        )
                                    }
                                }

                                if (earlierItems.isNotEmpty()) {
                                    item(key = "header_earlier") {
                                        SectionLabel(
                                            text = "Earlier",
                                            modifier = Modifier.padding(
                                                top = if (todayItems.isNotEmpty()) 10.dp else 0.dp
                                            )
                                        )
                                    }
                                    items(earlierItems, key = { "n_${it.id}" }) { item ->
                                        NotificationItemCard(
                                            item         = item,
                                            localReadIds = localReadIds,
                                            tickMs       = tickMs,
                                            onClick      = {
                                                viewModel.markGroupAsRead(item)
                                                navigateForItem(item, onNavigate)
                                            },
                                            onSwipeRead  = { viewModel.markGroupAsRead(item) },
                                        )
                                    }
                                }

                                item { Spacer(Modifier.height(24.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Tab pill ──────────────────────────────────────────────────────────────────

@Composable
private fun TabPill(
    label: String,
    active: Boolean,
    badge: Int?,
    onClick: () -> Unit,
) {
    val bgAnim by animateColorAsState(
        targetValue    = if (active) PillActiveBg else Color.Transparent,
        animationSpec  = tween(200),
        label          = "pill_bg_$label"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgAnim)
            .then(
                if (active) Modifier.border(1.dp, PillActiveBdr, RoundedCornerShape(20.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text       = label,
            color      = if (active) TabLabelOn else TabLabelOff,
            fontSize   = 14.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        )
        // Unread badge count
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentBlue)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = badge.toString(),
                    color    = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text,
        color      = SectionColor,
        fontSize   = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier   = modifier.padding(vertical = 4.dp)
    )
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(isUnreadTab: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1C22)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    tint = TabLabelOff,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text       = if (isUnreadTab) "All caught up!" else "No notifications yet",
                color      = Color.White,
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text       = if (isUnreadTab)
                    "You have no unread notifications right now."
                else
                    "Likes, comments, messages and more will appear here.",
                color      = TabLabelOff,
                fontSize   = 13.sp,
                textAlign  = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// ── Shimmer loading placeholder ───────────────────────────────────────────────

@Composable
private fun ShimmerList() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by transition.animateFloat(
        initialValue   = 0.4f,
        targetValue    = 1f,
        animationSpec  = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label          = "shimmer_alpha"
    )

    LazyColumn(
        modifier       = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { ShimmerSectionLabel(alpha = shimmerAlpha) }
        items(4) { ShimmerRow(alpha = shimmerAlpha) }
        item { ShimmerSectionLabel(alpha = shimmerAlpha, topPad = 10.dp) }
        items(3) { ShimmerRow(alpha = shimmerAlpha) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun ShimmerRow(alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ShimmerBase)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(ShimmerHigh.copy(alpha = alpha))
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(13.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ShimmerHigh.copy(alpha = alpha))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(11.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ShimmerHigh.copy(alpha = alpha * 0.7f))
            )
        }
    }
}

@Composable
private fun ShimmerSectionLabel(alpha: Float, topPad: Dp = 0.dp) {
    Box(
        modifier = Modifier
            .padding(top = topPad, bottom = 4.dp)
            .width(60.dp)
            .height(13.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(ShimmerHigh.copy(alpha = alpha))
    )
}

// ── Navigation helper ─────────────────────────────────────────────────────────

private fun navigateForItem(item: GroupedNotification, onNavigate: (String) -> Unit) {
    val targetId    = item.targetId
    val actorUserId = item.actorUserIds.firstOrNull().orEmpty()
    when (item.type) {
        "chat_message", "story_reply" ->
            if (targetId.isNotBlank()) onNavigate(Routes.chatScreen(targetId))
        "like", "love", "reaction" ->
            targetId.toIntOrNull()?.let { onNavigate(Routes.postDetail(it)) }
        "comment" ->
            targetId.toIntOrNull()?.let { onNavigate(Routes.postDetail(it, openComposer = true)) }
        "follow", "friend_request" ->
            if (actorUserId.isNotBlank()) onNavigate(Routes.profilePublic(actorUserId))
        "mention", "share" ->
            targetId.toIntOrNull()?.let { onNavigate(Routes.postDetail(it)) }
    }
}
