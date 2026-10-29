package eu.kanade.presentation.reader.appbars

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.catalog.components.LiquidIconButton
import com.kyant.shapes.Capsule
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopup
import tachiyomi.presentation.core.components.material.IconButton
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.MoreHoriz
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderTopBar(
    mangaTitle: String?,
    chapterTitle: String?,
    navigateUp: () -> Unit,
    bookmarked: Boolean,
    onToggleBookmarked: () -> Unit,
    onOpenInWebView: (() -> Unit)?,
    onOpenInBrowser: (() -> Unit)?,
    onShare: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onClickTopAppBar: (() -> Unit)? = null,
    currentPage: Int = 0,
    totalPages: Int = 0,
    readingModeName: String? = null,
    orientationName: String? = null,
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    if (isGlass) {
        val glassTintColor = if (isDark) Color(0x1218181A) else Color(0x12FFFFFF)
        val pillBorderColor = if (isDark) Color(0x22FFFFFF) else Color(0x18000000)
        var showOverflow by remember { mutableStateOf(false) }
        var isExpanded by remember { mutableStateOf(false) }
        val hasOverflow = onOpenInWebView != null || onOpenInBrowser != null || onShare != null

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Apple HIG Frosted Circular Back Button (44dp)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .liquidGlassSurface(
                        shape = CircleShape,
                        blurRadius = 3.dp,
                        refractionHeight = 16.dp,
                        refractionAmount = 24.dp,
                        tintColor = glassTintColor,
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 22.dp),
                        onClick = navigateUp,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = stringResource(MR.strings.action_bar_up_description),
                    tint = if (isDark) Color.White else Color.Black,
                )
            }

            // Apple HIG Smoothly Expandable Frosted Glass Bubble
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                val bubbleShape = if (isExpanded) RoundedCornerShape(20.dp) else Capsule()
                Column(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        )
                        .liquidGlassSurface(
                            shape = bubbleShape,
                            blurRadius = if (isExpanded) 5.dp else 3.dp,
                            refractionHeight = if (isExpanded) 20.dp else 16.dp,
                            refractionAmount = if (isExpanded) 32.dp else 26.dp,
                            tintColor = glassTintColor,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            onClick = { isExpanded = !isExpanded },
                        )
                        .padding(
                            horizontal = if (isExpanded) 18.dp else 16.dp,
                            vertical = if (isExpanded) 10.dp else 6.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (!mangaTitle.isNullOrEmpty()) {
                        Text(
                            text = mangaTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = if (isExpanded) 14.5.sp else 14.sp,
                                color = if (isDark) Color.White else Color.Black,
                            ),
                            maxLines = if (isExpanded) 2 else 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (!chapterTitle.isNullOrEmpty()) {
                        Text(
                            text = chapterTitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = if (isExpanded) 11.5.sp else 11.sp,
                                color = (if (isDark) Color.White else Color.Black).copy(alpha = if (isExpanded) 0.85f else 0.7f),
                            ),
                            maxLines = if (isExpanded) 2 else 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.15f),
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Live statistics row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            if (totalPages > 0) {
                                val percent = if (totalPages > 0) (currentPage * 100 / totalPages.coerceAtLeast(1)) else 0
                                Text(
                                    text = "Page $currentPage / $totalPages ($percent%)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                    ),
                                )
                            }
                            if (!readingModeName.isNullOrEmpty()) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.4f),
                                    ),
                                )
                                Text(
                                    text = readingModeName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.75f),
                                        fontSize = 11.sp,
                                    ),
                                )
                            }
                            if (!orientationName.isNullOrEmpty()) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.4f),
                                    ),
                                )
                                Text(
                                    text = orientationName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.75f),
                                        fontSize = 11.sp,
                                    ),
                                )
                            }
                        }

                        if (onClickTopAppBar != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap again to collapse",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.5.sp,
                                    color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.45f),
                                ),
                            )
                        }
                    }
                }
            }

            // Right Actions: Detached Circular Frosted Glass Bookmark & Overflow (44dp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .liquidGlassSurface(
                            shape = CircleShape,
                            blurRadius = 3.dp,
                            refractionHeight = 16.dp,
                            refractionAmount = 24.dp,
                            tintColor = glassTintColor,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 22.dp),
                            onClick = onToggleBookmarked,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = stringResource(
                            if (bookmarked) MR.strings.action_remove_bookmark else MR.strings.action_bookmark,
                        ),
                        tint = if (bookmarked) MaterialTheme.colorScheme.primary else (if (isDark) Color.White else Color.Black),
                    )
                }

                if (hasOverflow) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .liquidGlassSurface(
                                    shape = CircleShape,
                                    blurRadius = 3.dp,
                                    refractionHeight = 16.dp,
                                    refractionAmount = 24.dp,
                                    tintColor = glassTintColor,
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, radius = 22.dp),
                                    onClick = { showOverflow = true },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreHoriz,
                                contentDescription = stringResource(MR.strings.label_more),
                                tint = if (isDark) Color.White else Color.Black,
                            )
                        }

                        CupertinoFrostedPopup(
                            expanded = showOverflow,
                            onDismissRequest = { showOverflow = false },
                        ) {
                            onOpenInWebView?.let {
                                DropdownMenuItem(
                                    text = { Text(stringResource(MR.strings.action_open_in_web_view)) },
                                    onClick = {
                                        showOverflow = false
                                        it()
                                    },
                                )
                            }
                            onOpenInBrowser?.let {
                                DropdownMenuItem(
                                    text = { Text(stringResource(MR.strings.action_open_in_browser)) },
                                    onClick = {
                                        showOverflow = false
                                        it()
                                    },
                                )
                            }
                            onShare?.let {
                                DropdownMenuItem(
                                    text = { Text(stringResource(MR.strings.action_share)) },
                                    onClick = {
                                        showOverflow = false
                                        it()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        AppBar(
            modifier = modifier,
            backgroundColor = Color.Transparent,
            title = mangaTitle,
            subtitle = chapterTitle,
            navigateUp = navigateUp,
            actions = {
                AppBarActions(
                    actions = persistentListOf<AppBar.AppBarAction>().builder()
                        .apply {
                            add(
                                AppBar.Action(
                                    title = stringResource(
                                        if (bookmarked) {
                                            MR.strings.action_remove_bookmark
                                        } else {
                                            MR.strings.action_bookmark
                                        },
                                    ),
                                    icon = if (bookmarked) {
                                        Icons.Outlined.Bookmark
                                    } else {
                                        Icons.Outlined.BookmarkBorder
                                    },
                                    onClick = onToggleBookmarked,
                                ),
                            )
                            onOpenInWebView?.let {
                                add(
                                    AppBar.OverflowAction(
                                        title = stringResource(MR.strings.action_open_in_web_view),
                                        onClick = it,
                                    ),
                                )
                            }
                            onOpenInBrowser?.let {
                                add(
                                    AppBar.OverflowAction(
                                        title = stringResource(MR.strings.action_open_in_browser),
                                        onClick = it,
                                    ),
                                )
                            }
                            onShare?.let {
                                add(
                                    AppBar.OverflowAction(
                                        title = stringResource(MR.strings.action_share),
                                        onClick = it,
                                    ),
                                )
                            }
                        }
                        .build(),
                )
            },
        )
    }
}
