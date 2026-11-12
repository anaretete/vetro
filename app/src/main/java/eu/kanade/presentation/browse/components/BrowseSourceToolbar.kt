package eu.kanade.presentation.browse.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.components.AppBarTitle
import eu.kanade.presentation.components.DropdownMenu
import eu.kanade.presentation.components.DropdownMenuSeparator
import eu.kanade.presentation.components.RadioMenuItem
import eu.kanade.presentation.components.SearchToolbar
import eu.kanade.tachiyomi.source.Source
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.domain.library.model.LibraryDisplayMode
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.components.material.GlassMenuAnchor
import tachiyomi.presentation.core.components.material.IconButton
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.supportsNativeGlassMenu
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.source.local.LocalSource

@Composable
fun BrowseSourceToolbar(
    searchQuery: String?,
    onSearchQueryChange: (String?) -> Unit,
    source: Source?,
    displayMode: LibraryDisplayMode?,
    onDisplayModeChange: (LibraryDisplayMode) -> Unit,
    navigateUp: () -> Unit,
    onWebViewClick: () -> Unit,
    onHelpClick: () -> Unit,
    // KMK -->
    onToggleIncognito: () -> Unit,
    onSettingsClick: (() -> Unit)?,
    // KMK <--
    onSearch: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    // KMK -->
    toggleSelectionMode: () -> Unit,
    isRunning: Boolean,
    // KMK <--
) {
    // Avoid capturing unstable source in actions lambda
    val title = source?.name
    val isLocalSource = source is LocalSource

    var selectingDisplayMode by remember { mutableStateOf(false) }

    SearchToolbar(
        navigateUp = navigateUp,
        titleContent = { AppBarTitle(title) },
        searchQuery = searchQuery,
        onChangeSearchQuery = onSearchQueryChange,
        onSearch = onSearch,
        onClickCloseSearch = navigateUp,
        actions = {
            if (displayMode != null) {
                val isGlass = LocalLiquidGlassConfig.current.enabled
                val comfortableLabel = stringResource(MR.strings.action_display_comfortable_grid)
                val panoramaLabel = stringResource(KMR.strings.action_display_comfortable_grid_panorama)
                val compactLabel = stringResource(MR.strings.action_display_grid)
                val listLabel = stringResource(MR.strings.action_display_list)
                Box {
                    if (isGlass && supportsNativeGlassMenu) {
                        val items = remember(displayMode, comfortableLabel, panoramaLabel, compactLabel, listLabel) {
                            listOf(
                                GlassMenuItemRecord(
                                    id = "comfortable_grid",
                                    title = comfortableLabel,
                                    checked = if (displayMode == LibraryDisplayMode.ComfortableGrid) true else null,
                                    onClick = {
                                        selectingDisplayMode = false
                                        onDisplayModeChange(LibraryDisplayMode.ComfortableGrid)
                                    },
                                ),
                                GlassMenuItemRecord(
                                    id = "panorama_grid",
                                    title = panoramaLabel,
                                    checked = if (displayMode == LibraryDisplayMode.ComfortableGridPanorama) true else null,
                                    onClick = {
                                        selectingDisplayMode = false
                                        onDisplayModeChange(LibraryDisplayMode.ComfortableGridPanorama)
                                    },
                                ),
                                GlassMenuItemRecord(
                                    id = "compact_grid",
                                    title = compactLabel,
                                    checked = if (displayMode == LibraryDisplayMode.CompactGrid) true else null,
                                    onClick = {
                                        selectingDisplayMode = false
                                        onDisplayModeChange(LibraryDisplayMode.CompactGrid)
                                    },
                                ),
                                GlassMenuItemRecord(
                                    id = "list",
                                    title = listLabel,
                                    checked = if (displayMode == LibraryDisplayMode.List) true else null,
                                    onClick = {
                                        selectingDisplayMode = false
                                        onDisplayModeChange(LibraryDisplayMode.List)
                                    },
                                ),
                            )
                        }
                        GlassMenuAnchor(
                            expanded = selectingDisplayMode,
                            onDismissRequest = { selectingDisplayMode = false },
                            items = items,
                            modifier = Modifier.matchParentSize(),
                        )
                    } else {
                        DropdownMenu(
                            expanded = selectingDisplayMode,
                            onDismissRequest = { selectingDisplayMode = false },
                        ) {
                            RadioMenuItem(
                                text = { Text(text = comfortableLabel) },
                                isChecked = displayMode == LibraryDisplayMode.ComfortableGrid,
                            ) {
                                selectingDisplayMode = false
                                onDisplayModeChange(LibraryDisplayMode.ComfortableGrid)
                            }
                            DropdownMenuSeparator()
                            // KMK -->
                            RadioMenuItem(
                                text = { Text(text = panoramaLabel) },
                                isChecked = displayMode == LibraryDisplayMode.ComfortableGridPanorama,
                            ) {
                                selectingDisplayMode = false
                                onDisplayModeChange(LibraryDisplayMode.ComfortableGridPanorama)
                            }
                            DropdownMenuSeparator()
                            // KMK <--
                            RadioMenuItem(
                                text = { Text(text = compactLabel) },
                                isChecked = displayMode == LibraryDisplayMode.CompactGrid,
                            ) {
                                selectingDisplayMode = false
                                onDisplayModeChange(LibraryDisplayMode.CompactGrid)
                            }
                            DropdownMenuSeparator()
                            RadioMenuItem(
                                text = { Text(text = listLabel) },
                                isChecked = displayMode == LibraryDisplayMode.List,
                            ) {
                                selectingDisplayMode = false
                                onDisplayModeChange(LibraryDisplayMode.List)
                            }
                        }
                    }
                    IconButton(onClick = { selectingDisplayMode = true }) {
                        Icon(
                            imageVector = if (displayMode == LibraryDisplayMode.List) {
                                Icons.AutoMirrored.Filled.ViewList
                            } else {
                                Icons.Filled.ViewModule
                            },
                            contentDescription = stringResource(MR.strings.action_display_mode),
                        )
                    }
                }
            }

            AppBarActions(
                actions = persistentListOf<AppBar.AppBarAction>().builder()
                    .apply {
                        // KMK -->
                        add(bulkSelectionButton(isRunning, toggleSelectionMode))
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(MR.strings.pref_incognito_mode),
                                onClick = onToggleIncognito,
                            ),
                        )
                        // KMK <--
                        if (isLocalSource) {
                            add(
                                AppBar.OverflowAction(
                                    title = stringResource(MR.strings.label_help),
                                    onClick = onHelpClick,
                                ),
                            )
                        } else {
                            add(
                                AppBar.OverflowAction(
                                    title = stringResource(MR.strings.action_web_view),
                                    onClick = onWebViewClick,
                                ),
                            )
                        }
                        // SY <--
                        // KMK -->
                        onSettingsClick?.let {
                            // KMK <--
                            add(
                                AppBar.OverflowAction(
                                    title = stringResource(MR.strings.action_settings),
                                    onClick = onSettingsClick,
                                ),
                            )
                        }
                    }
                    .build(),
            )
        },
        scrollBehavior = scrollBehavior,
    )
}
