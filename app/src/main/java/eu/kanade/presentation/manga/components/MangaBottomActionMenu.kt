package eu.kanade.presentation.manga.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RemoveDone
import androidx.compose.material.icons.outlined.SwapCalls
import eu.kanade.presentation.components.DropdownMenuItem
import eu.kanade.presentation.components.DropdownMenuSeparator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kyant.shapes.Capsule
import eu.kanade.presentation.components.DownloadDropdownMenu
import eu.kanade.presentation.components.DropdownMenu
import eu.kanade.presentation.manga.DownloadAction
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.isTabletUi
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.material.GlassMenuAnchor
import tachiyomi.presentation.core.components.material.LocalInGlassBar
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.MoreHoriz
import tachiyomi.presentation.core.components.material.NativeGlassSurface
import tachiyomi.presentation.core.components.material.liquidGlassPopupSurface
import tachiyomi.presentation.core.components.material.liquidGlassSurface
import tachiyomi.presentation.core.components.material.supportsNativeGlassMenu
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun MangaBottomActionMenu(
    visible: Boolean,
    modifier: Modifier = Modifier,
    onBookmarkClicked: (() -> Unit)? = null,
    onRemoveBookmarkClicked: (() -> Unit)? = null,
    onMarkAsReadClicked: (() -> Unit)? = null,
    onMarkAsUnreadClicked: (() -> Unit)? = null,
    onMarkPreviousAsReadClicked: (() -> Unit)? = null,
    onDownloadClicked: (() -> Unit)? = null,
    onDeleteClicked: (() -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom),
    ) {
        val scope = rememberCoroutineScope()
        val isGlass = LocalLiquidGlassConfig.current.enabled
        val useNativeGlass = isGlass && supportsNativeGlassMenu
        val menuModifier = if (useNativeGlass) {
            modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        } else if (isGlass) {
            modifier
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .liquidGlassPopupSurface(shape = Capsule())
        } else {
            modifier
        }
        val menuShape = if (isGlass) Capsule() else MaterialTheme.shapes.large.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize)
        val menuColor = if (isGlass) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh
        androidx.compose.runtime.CompositionLocalProvider(LocalInGlassBar provides isGlass) {
            Surface(
                modifier = menuModifier,
                shape = menuShape,
                color = menuColor,
            ) {
                Box {
                    if (useNativeGlass) {
                        NativeGlassSurface(
                            modifier = Modifier.matchParentSize(),
                            cornerRadius = 28.dp,
                            topCornersOnly = false,
                        )
                    }
                    val haptic = LocalHapticFeedback.current
                    val confirm = remember { mutableStateListOf(false, false, false, false, false, false, false) }
                    var resetJob by remember { mutableStateOf<Job?>(null) }
                    val onLongClickItem: (Int) -> Unit = { toConfirmIndex ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        confirm.indices.forEach { i -> confirm[i] = i == toConfirmIndex }
                        resetJob?.cancel()
                        resetJob = scope.launch {
                            delay(1.seconds)
                            if (isActive) confirm[toConfirmIndex] = false
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(
                                WindowInsets.navigationBars
                                    .only(WindowInsetsSides.Bottom)
                                    .asPaddingValues(),
                            )
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                    ) {
                        if (onBookmarkClicked != null) {
                            Button(
                                title = stringResource(MR.strings.action_bookmark),
                                icon = Icons.Outlined.BookmarkAdd,
                                toConfirm = confirm[0],
                                onLongClick = { onLongClickItem(0) },
                                onClick = onBookmarkClicked,
                            )
                        }
                        if (onRemoveBookmarkClicked != null) {
                            Button(
                                title = stringResource(MR.strings.action_remove_bookmark),
                                icon = Icons.Outlined.BookmarkRemove,
                                toConfirm = confirm[1],
                                onLongClick = { onLongClickItem(1) },
                                onClick = onRemoveBookmarkClicked,
                            )
                        }
                        if (onMarkAsReadClicked != null) {
                            Button(
                                title = stringResource(MR.strings.action_mark_as_read),
                                icon = Icons.Outlined.DoneAll,
                                toConfirm = confirm[2],
                                onLongClick = { onLongClickItem(2) },
                                onClick = onMarkAsReadClicked,
                            )
                        }
                        if (onMarkAsUnreadClicked != null) {
                            Button(
                                title = stringResource(MR.strings.action_mark_as_unread),
                                icon = Icons.Outlined.RemoveDone,
                                toConfirm = confirm[3],
                                onLongClick = { onLongClickItem(3) },
                                onClick = onMarkAsUnreadClicked,
                            )
                        }
                        if (onMarkPreviousAsReadClicked != null) {
                            Button(
                                title = stringResource(MR.strings.action_mark_previous_as_read),
                                icon = ImageVector.vectorResource(R.drawable.ic_done_prev_24dp),
                                toConfirm = confirm[4],
                                onLongClick = { onLongClickItem(4) },
                                onClick = onMarkPreviousAsReadClicked,
                            )
                        }
                        if (onDownloadClicked != null) {
                            Button(
                                title = stringResource(MR.strings.action_download),
                                icon = Icons.Outlined.Download,
                                toConfirm = confirm[5],
                                onLongClick = { onLongClickItem(5) },
                                onClick = onDownloadClicked,
                            )
                        }
                        if (onDeleteClicked != null) {
                            Button(
                                title = stringResource(MR.strings.action_delete),
                                icon = Icons.Outlined.Delete,
                                toConfirm = confirm[6],
                                onLongClick = { onLongClickItem(6) },
                                onClick = onDeleteClicked,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RowScope.Button(
    title: String,
    icon: ImageVector,
    toConfirm: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    // KMK -->
    enabled: Boolean = true,
    // KMK <--
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val animatedWeight by animateFloatAsState(
        targetValue = if (toConfirm) 2f else 1f,
        label = "weight",
    )
    // KMK -->
    val animatedColor by animateColorAsState(
        if (enabled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.38f,
            )
        },
        label = "color",
    )
    // KMK <--
    Box(
        modifier = Modifier
            .size(48.dp)
            .weight(animatedWeight)
            .combinedClickable(
                interactionSource = null,
                indication = ripple(bounded = false),
                onLongClick = onLongClick,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content?.invoke(this)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                // KMK -->
                tint = animatedColor,
                // KMK <--
            )
            AnimatedVisibility(
                visible = toConfirm,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            ) {
                Text(
                    text = title,
                    overflow = TextOverflow.Visible,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                    // KMK -->
                    color = animatedColor,
                    // KMK <--
                )
            }
        }
    }
}

@Composable
fun LibraryBottomActionMenu(
    visible: Boolean,
    onChangeCategoryClicked: () -> Unit,
    onMarkAsReadClicked: () -> Unit,
    onMarkAsUnreadClicked: () -> Unit,
    onDownloadClicked: ((DownloadAction) -> Unit)?,
    onDeleteClicked: () -> Unit,
    onMigrateClicked: () -> Unit,
    // KMK -->
    onMergeClicked: () -> Unit,
    onSelectionUpdateClicked: () -> Unit,
    // KMK <--
    // SY -->
    onClickCleanTitles: (() -> Unit)?,
    onClickCollectRecommendations: (() -> Unit)?,
    onClickAddToMangaDex: (() -> Unit)?,
    onClickResetInfo: (() -> Unit)?,
    // SY <--
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(animationSpec = tween(delayMillis = 300)),
        exit = shrinkVertically(animationSpec = tween()),
    ) {
        val scope = rememberCoroutineScope()
        val isGlass = LocalLiquidGlassConfig.current.enabled
        val useNativeGlass = isGlass && supportsNativeGlassMenu
        val menuModifier = if (useNativeGlass) {
            modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        } else if (isGlass) {
            modifier
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .liquidGlassPopupSurface(shape = Capsule())
        } else {
            modifier
        }
        val menuShape = if (isGlass) Capsule() else MaterialTheme.shapes.large.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize)
        val menuColor = if (isGlass) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh
        androidx.compose.runtime.CompositionLocalProvider(LocalInGlassBar provides isGlass) {
            Surface(
                modifier = menuModifier,
                shape = menuShape,
                color = menuColor,
            ) {
                Box {
                    if (useNativeGlass) {
                        NativeGlassSurface(
                            modifier = Modifier.matchParentSize(),
                            cornerRadius = 28.dp,
                            topCornersOnly = false,
                        )
                    }
                    val haptic = LocalHapticFeedback.current
                    val confirm = remember { mutableStateListOf(false, false, false, false, false, false, false) }
                    var resetJob by remember { mutableStateOf<Job?>(null) }
                    val onLongClickItem: (Int) -> Unit = { toConfirmIndex ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        confirm.indices.forEach { i -> confirm[i] = i == toConfirmIndex }
                        resetJob?.cancel()
                        resetJob = scope.launch {
                            delay(1.seconds)
                            if (isActive) confirm[toConfirmIndex] = false
                        }
                    }
                    Row(
                        modifier = Modifier
                            .windowInsetsPadding(
                                WindowInsets.navigationBars
                                    .only(WindowInsetsSides.Bottom),
                            )
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                    ) {
                    Button(
                        title = stringResource(MR.strings.action_move_category),
                        icon = Icons.AutoMirrored.Outlined.Label,
                        toConfirm = confirm[0],
                        onLongClick = { onLongClickItem(0) },
                        onClick = onChangeCategoryClicked,
                    )
                    Button(
                        title = stringResource(MR.strings.action_mark_as_read),
                        icon = Icons.Outlined.DoneAll,
                        toConfirm = confirm[1],
                        onLongClick = { onLongClickItem(1) },
                        onClick = onMarkAsReadClicked,
                    )
                    Button(
                        title = stringResource(MR.strings.action_mark_as_unread),
                        icon = Icons.Outlined.RemoveDone,
                        toConfirm = confirm[2],
                        onLongClick = { onLongClickItem(2) },
                        onClick = onMarkAsUnreadClicked,
                    )
                    if (onDownloadClicked != null) {
                        var downloadExpanded by remember { mutableStateOf(false) }
                        Button(
                            title = stringResource(MR.strings.action_download),
                            icon = Icons.Outlined.Download,
                            toConfirm = confirm[3],
                            onLongClick = { onLongClickItem(3) },
                            onClick = { downloadExpanded = !downloadExpanded },
                        ) {
                            DownloadDropdownMenu(
                                expanded = downloadExpanded,
                                onDismissRequest = { downloadExpanded = false },
                                onDownloadClicked = onDownloadClicked,
                                modifier = Modifier.matchParentSize(),
                                offset = BottomBarMenuDpOffset,
                            )
                        }
                    }
                    // SY -->
                    val configuration = LocalConfiguration.current
                    val isTabletUi = remember { configuration.isTabletUi() }
                    // SY <--
                    // KMK -->
                    if (onDownloadClicked == null || isTabletUi) {
                        Button(
                            title = stringResource(KMR.strings.action_update),
                            icon = Icons.Outlined.Refresh,
                            toConfirm = confirm[4],
                            onLongClick = { onLongClickItem(4) },
                            onClick = onSelectionUpdateClicked,
                        )
                    }
                    if (isTabletUi) {
                        // KMK <--
                        Button(
                            title = stringResource(MR.strings.migrate),
                            icon = Icons.Outlined.SwapCalls,
                            toConfirm = confirm[5],
                            onLongClick = { onLongClickItem(5) },
                            onClick = onMigrateClicked,
                        )
                    }
                    var overflowMenuOpen by remember { mutableStateOf(false) }
                    val updateLabel = stringResource(KMR.strings.action_update)
                    val migrateLabel = stringResource(MR.strings.migrate)
                    val deleteLabel = stringResource(MR.strings.action_delete)
                    val mergeLabel = stringResource(SYMR.strings.merge)
                    val cleanTitlesLabel = stringResource(SYMR.strings.action_clean_titles)
                    val recSearchLabel = stringResource(SYMR.strings.rec_search_short)
                    val addToFollowsLabel = stringResource(SYMR.strings.mangadex_add_to_follows)
                    val resetInfoLabel = stringResource(SYMR.strings.reset_info)
                    Button(
                        title = stringResource(MR.strings.label_more),
                        icon = Icons.Outlined.MoreHoriz,
                        toConfirm = confirm[6],
                        onLongClick = { onLongClickItem(6) },
                        onClick = { overflowMenuOpen = true },
                    ) {
                        if (isGlass && supportsNativeGlassMenu) {
                            val items = buildList {
                                if (!isTabletUi) {
                                    if (onDownloadClicked != null) {
                                        add(
                                            GlassMenuItemRecord(
                                                id = "update",
                                                title = updateLabel,
                                                onClick = {
                                                    overflowMenuOpen = false
                                                    onSelectionUpdateClicked()
                                                },
                                            ),
                                        )
                                    }
                                    add(
                                        GlassMenuItemRecord(
                                            id = "migrate",
                                            title = migrateLabel,
                                            onClick = {
                                                overflowMenuOpen = false
                                                onMigrateClicked()
                                            },
                                        ),
                                    )
                                }
                                add(
                                    GlassMenuItemRecord(
                                        id = "delete",
                                        title = deleteLabel,
                                        destructive = true,
                                        onClick = {
                                            overflowMenuOpen = false
                                            onDeleteClicked()
                                        },
                                    ),
                                )
                                add(
                                    GlassMenuItemRecord(
                                        id = "merge",
                                        title = mergeLabel,
                                        onClick = {
                                            overflowMenuOpen = false
                                            onMergeClicked()
                                        },
                                    ),
                                )
                                if (onClickCleanTitles != null) {
                                    add(
                                        GlassMenuItemRecord(
                                            id = "clean_titles",
                                            title = cleanTitlesLabel,
                                            onClick = {
                                                overflowMenuOpen = false
                                                onClickCleanTitles()
                                            },
                                        ),
                                    )
                                }
                                if (onClickCollectRecommendations != null) {
                                    add(
                                        GlassMenuItemRecord(
                                            id = "rec_search",
                                            title = recSearchLabel,
                                            onClick = {
                                                overflowMenuOpen = false
                                                onClickCollectRecommendations()
                                            },
                                        ),
                                    )
                                }
                                if (onClickAddToMangaDex != null) {
                                    add(
                                        GlassMenuItemRecord(
                                            id = "add_to_follows",
                                            title = addToFollowsLabel,
                                            onClick = {
                                                overflowMenuOpen = false
                                                onClickAddToMangaDex()
                                            },
                                        ),
                                    )
                                }
                                if (onClickResetInfo != null) {
                                    add(
                                        GlassMenuItemRecord(
                                            id = "reset_info",
                                            title = resetInfoLabel,
                                            onClick = {
                                                overflowMenuOpen = false
                                                onClickResetInfo()
                                            },
                                        ),
                                    )
                                }
                            }
                            GlassMenuAnchor(
                                expanded = overflowMenuOpen,
                                onDismissRequest = { overflowMenuOpen = false },
                                items = items,
                                modifier = Modifier.matchParentSize(),
                            )
                        } else {
                            DropdownMenu(
                                expanded = overflowMenuOpen,
                                onDismissRequest = { overflowMenuOpen = false },
                                offset = BottomBarMenuDpOffset,
                            ) {
                                // KMK -->
                                if (!isTabletUi) {
                                    if (onDownloadClicked != null) {
                                        DropdownMenuItem(
                                            text = { Text(updateLabel) },
                                            onClick = onSelectionUpdateClicked,
                                        )
                                        DropdownMenuSeparator()
                                    }
                                    // KMK <--
                                    DropdownMenuItem(
                                        text = { Text(migrateLabel) },
                                        onClick = onMigrateClicked,
                                    )
                                    DropdownMenuSeparator()
                                }
                                DropdownMenuItem(
                                    text = { Text(deleteLabel) },
                                    destructive = true,
                                    onClick = onDeleteClicked,
                                )
                                DropdownMenuSeparator()
                                // KMK -->
                                DropdownMenuItem(
                                    text = { Text(mergeLabel) },
                                    onClick = onMergeClicked,
                                )
                                if (onClickCleanTitles != null) {
                                    DropdownMenuSeparator()
                                    DropdownMenuItem(
                                        text = { Text(cleanTitlesLabel) },
                                        onClick = onClickCleanTitles,
                                    )
                                }
                                if (onClickCollectRecommendations != null) {
                                    DropdownMenuItem(
                                        text = { Text(recSearchLabel) },
                                        onClick = onClickCollectRecommendations,
                                    )
                                }
                                if (onClickAddToMangaDex != null) {
                                    DropdownMenuItem(
                                        text = { Text(addToFollowsLabel) },
                                        onClick = onClickAddToMangaDex,
                                    )
                                }
                                if (onClickResetInfo != null) {
                                    DropdownMenuItem(
                                        text = { Text(text = resetInfoLabel) },
                                        onClick = onClickResetInfo,
                                    )
                                }
                                // KMK <--
                            }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val BottomBarMenuDpOffset = DpOffset(0.dp, 0.dp)
