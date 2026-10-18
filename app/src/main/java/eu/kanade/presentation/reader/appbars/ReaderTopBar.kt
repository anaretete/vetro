package eu.kanade.presentation.reader.appbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.kyant.shapes.Capsule
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import tachiyomi.presentation.core.components.material.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.kyant.backdrop.catalog.components.LiquidIconButton
import tachiyomi.presentation.core.components.material.CupertinoFrostedPopup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.IconButton
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
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
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val isDark = isSystemInDarkTheme()

    if (isGlass) {
        val glassSurfaceColor = if (isDark) Color(0x991C1C1E) else Color(0xBBFFFFFF)
        var showOverflow by remember { mutableStateOf(false) }
        val hasOverflow = onOpenInWebView != null || onOpenInBrowser != null || onShare != null

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Detached Circular Frosted Glass Back Button (42dp) with viscoelastic drag morphing
            LiquidIconButton(
                onClick = navigateUp,
                size = 42.dp,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = stringResource(MR.strings.action_bar_up_description),
                    tint = if (isDark) Color.White else Color.Black,
                )
            }

            // Compact Centered Glass Pill for Title/Chapter (floating glass capsule)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .liquidGlassSurface(
                            shape = Capsule(),
                            blurRadius = 16.dp,
                            refractionHeight = 18.dp,
                            refractionAmount = 24.dp,
                            surfaceColor = glassSurfaceColor,
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .then(if (onClickTopAppBar != null) Modifier.clickable(onClick = onClickTopAppBar) else Modifier),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (!mangaTitle.isNullOrEmpty()) {
                        Text(
                            text = mangaTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = if (isDark) Color.White else Color.Black,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (!chapterTitle.isNullOrEmpty()) {
                        Text(
                            text = chapterTitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.7f),
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // Right Actions: Detached Circular Frosted Glass Bookmark & Overflow (42dp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiquidIconButton(
                    onClick = onToggleBookmarked,
                    size = 42.dp,
                ) {
                    Icon(
                        imageVector = if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = stringResource(
                            if (bookmarked) MR.strings.action_remove_bookmark else MR.strings.action_bookmark,
                        ),
                        tint = if (bookmarked) Color(0xFFFF9F0A) else (if (isDark) Color.White else Color.Black),
                    )
                }

                if (hasOverflow) {
                    Box {
                        LiquidIconButton(
                            onClick = { showOverflow = true },
                            size = 42.dp,
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
