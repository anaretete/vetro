package eu.kanade.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import eu.kanade.presentation.manga.DownloadAction
import expo.modules.glassmenuandroid.records.GlassMenuItemRecord
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.GlassMenuAnchor
import tachiyomi.presentation.core.components.material.LocalLiquidGlassConfig
import tachiyomi.presentation.core.components.material.supportsNativeGlassMenu
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun DownloadDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onDownloadClicked: (DownloadAction) -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = /* KMK --> */ DefaultDropdownMenuOffset, /* KMK <-- */
) {
    val isGlass = LocalLiquidGlassConfig.current.enabled
    val currentOnDownloadClicked by rememberUpdatedState(onDownloadClicked)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val options = persistentListOf(
        DownloadAction.NEXT_1_CHAPTER to pluralStringResource(MR.plurals.download_amount, 1, 1),
        DownloadAction.NEXT_5_CHAPTERS to pluralStringResource(MR.plurals.download_amount, 5, 5),
        DownloadAction.NEXT_10_CHAPTERS to pluralStringResource(MR.plurals.download_amount, 10, 10),
        DownloadAction.NEXT_25_CHAPTERS to pluralStringResource(MR.plurals.download_amount, 25, 25),
        DownloadAction.UNREAD_CHAPTERS to stringResource(MR.strings.download_unread),
        DownloadAction.BOOKMARKED_CHAPTERS to stringResource(MR.strings.download_bookmarked),
    )

    if (isGlass && supportsNativeGlassMenu) {
        val items = remember(options) {
            options.map { (action, label) ->
                GlassMenuItemRecord(
                    id = action.name,
                    title = label,
                    onClick = {
                        currentOnDownloadClicked(action)
                        currentOnDismissRequest()
                    },
                )
            }
        }
        GlassMenuAnchor(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            items = items,
            modifier = modifier,
        )
    } else {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            offset = offset,
            content = {
                options.forEachIndexed { index, (downloadAction, string) ->
                    DropdownMenuItem(
                        text = { Text(text = string) },
                        onClick = {
                            onDownloadClicked(downloadAction)
                            onDismissRequest()
                        },
                    )
                    if (index < options.lastIndex) {
                        DropdownMenuSeparator()
                    }
                }
            },
        )
    }
}
