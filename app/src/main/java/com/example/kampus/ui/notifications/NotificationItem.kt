package com.example.kampus.ui.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ── Design tokens ─────────────────────────────────────────────────────────────

private val CardBgUnread  = Color(0xFF161820)
private val CardBgRead    = Color(0xFF101114)
private val BorderUnread  = Color(0xFF263352)
private val BorderRead    = Color(0xFF1E2028)
private val LabelGray     = Color(0xFF7A7D8A)
private val UnreadDotBlue = Color(0xFF4C8FFF)
private val SwipeHintBg   = Color(0xFF1E4A30)

// ── Main composable ───────────────────────────────────────────────────────────

/**
 * A single notification row with:
 * - Animated enter (fade + slide from bottom)
 * - Press scale feedback
 * - Swipe-right-to-mark-read gesture
 * - Stacked avatars with coloured type badge
 * - Timestamp top-right, body below title
 * - Unread blue dot
 */
@Composable
fun NotificationItemCard(
    item: GroupedNotification,
    localReadIds: Set<String>,
    tickMs: Long,          // drives timestamp recomposition
    onClick: () -> Unit,
    onSwipeRead: () -> Unit,
) {
    val isEffectivelyRead = item.effectiveIsRead(localReadIds)
    val haptic = LocalHapticFeedback.current

    // ── Animated visibility on enter ─────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(item.id) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 3 },
    ) {
        // ── Press scale ───────────────────────────────────────────────────────
        val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.975f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "press_scale"
        )

        // ── Swipe-to-mark-read ────────────────────────────────────────────────
        var swipeOffset by remember { mutableFloatStateOf(0f) }
        val swipeThreshold = 120f
        val isSwipeRevealed = swipeOffset > 30f
        val animatedSwipe by animateFloatAsState(
            targetValue = swipeOffset,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "swipe_x"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
        ) {
            // ── Swipe hint background (check icon) ────────────────────────────
            if (isSwipeRevealed && !isEffectivelyRead) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(60.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                        .background(SwipeHintBg)
                        .align(Alignment.CenterStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Mark read",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ── Card ──────────────────────────────────────────────────────────
            val cardOffset = if (isEffectivelyRead) 0f else animatedSwipe.coerceAtMost(swipeThreshold)
            Box(
                modifier = Modifier
                    .offset(x = cardOffset.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isEffectivelyRead) CardBgRead else CardBgUnread)
                    .border(
                        width = 1.dp,
                        color = if (isEffectivelyRead) BorderRead else BorderUnread,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .pointerInput(item.id, isEffectivelyRead) {
                        if (isEffectivelyRead) return@pointerInput
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (swipeOffset >= swipeThreshold) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSwipeRead()
                                }
                                swipeOffset = 0f
                            },
                            onDragCancel = { swipeOffset = 0f },
                            onHorizontalDrag = { _, delta ->
                                swipeOffset = (swipeOffset + delta).coerceAtLeast(0f)
                            }
                        )
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar stack
                    NotifAvatarStack(item = item, bg = if (isEffectivelyRead) CardBgRead else CardBgUnread)

                    // Text content
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = item.title,
                                color = if (isEffectivelyRead) Color(0xFFB0B3BE) else Color.White,
                                fontWeight = if (isEffectivelyRead) FontWeight.Normal else FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = formatTimeAgo(item.latestCreatedAt, tickMs),
                                color = LabelGray,
                                fontSize = 12.sp,
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = item.body,
                            color = LabelGray,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    // Post thumbnail
                    if (item.postImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = item.postImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E2028)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Unread dot — animated appearance
                    AnimatedVisibility(
                        visible = !isEffectivelyRead,
                        enter = fadeIn() + scaleIn(),
                        exit  = fadeOut() + scaleOut(),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(UnreadDotBlue)
                        )
                    }
                }
            }
        }
    }
}

// ── Avatar stack ──────────────────────────────────────────────────────────────

@Composable
private fun NotifAvatarStack(item: GroupedNotification, bg: Color) {
    Box(modifier = Modifier.size(54.dp)) {
        if (item.actors.size > 1) {
            NotifAvatarCircle(actor = item.actors[1], size = 34.dp, borderColor = bg,
                modifier = Modifier.align(Alignment.BottomEnd))
            NotifAvatarCircle(actor = item.actors[0], size = 34.dp, borderColor = bg,
                modifier = Modifier.align(Alignment.TopStart))
        } else {
            val actor = item.actors.firstOrNull() ?: ActorProfile(displayName = "Someone")
            NotifAvatarCircle(actor = actor, size = 44.dp, borderColor = bg,
                modifier = Modifier.align(Alignment.Center))
        }
        // Badge
        Box(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(notifBadgeColor(item.type))
                .border(1.5.dp, bg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = notifBadgeIcon(item.type),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
private fun NotifAvatarCircle(
    actor: ActorProfile,
    size: Dp,
    borderColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .background(Color(0xFF252836)),
        contentAlignment = Alignment.Center
    ) {
        if (actor.profileImageUrl.isNotBlank()) {
            AsyncImage(
                model = actor.profileImageUrl,
                contentDescription = actor.displayName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = actor.avatarEmoji.ifBlank { "👤" },
                fontSize = (size.value * 0.42f).sp
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun notifBadgeIcon(type: String): ImageVector = when (type) {
    "like", "love", "reaction"                        -> Icons.Default.Favorite
    "comment"                                         -> Icons.AutoMirrored.Filled.Comment
    "chat_message", "direct_message", "story_reply"   -> Icons.AutoMirrored.Filled.Send
    "follow", "friend_request"                        -> Icons.Default.PersonAdd
    "mention"                                         -> Icons.Default.Tag
    "share"                                           -> Icons.Default.Share
    "story"                                           -> Icons.Default.CameraAlt
    else                                              -> Icons.Default.Notifications
}

private fun notifBadgeColor(type: String): Color = when (type) {
    "like", "love", "reaction"                        -> Color(0xFFEF4444)
    "comment"                                         -> Color(0xFF8B5CF6)
    "chat_message", "direct_message", "story_reply"   -> Color(0xFF3B82F6)
    "follow", "friend_request"                        -> Color(0xFF10B981)
    "mention"                                         -> Color(0xFFF59E0B)
    "share"                                           -> Color(0xFF673AB7)
    "story"                                           -> Color(0xFFFF9800)
    else                                              -> Color(0xFF6B7280)
}

internal fun formatTimeAgo(ts: Long, nowMs: Long = System.currentTimeMillis()): String {
    if (ts <= 0L) return "just now"
    val mins = ((nowMs - ts) / 60_000L).coerceAtLeast(0L)
    return when {
        mins < 1    -> "just now"
        mins < 60   -> "${mins}m"
        mins < 1440 -> "${mins / 60}h"
        else        -> "${mins / 1440}d"
    }
}
