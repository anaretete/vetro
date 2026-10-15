package eu.kanade.presentation.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.components.material.CupertinoActionSheetCard
import tachiyomi.presentation.core.components.material.CupertinoAlertDialog
import tachiyomi.presentation.core.components.material.CupertinoCancelPill
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun ReaderPageActionsDialog(
    onDismissRequest: () -> Unit,
    onSetAsCover: (useExtraPage: Boolean) -> Unit,
    onShare: (copyToClipboard: Boolean, useExtraPage: Boolean) -> Unit,
    onSave: (useExtraPage: Boolean) -> Unit,
    onShareCombined: (copyToClipboard: Boolean) -> Unit,
    onSaveCombined: () -> Unit,
    hasExtraPage: Boolean,
) {
    var showSetCoverDialog by remember { mutableStateOf(false) }
    var useExtraPage by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest,
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CupertinoActionSheetCard(
                    modifier = if (hasExtraPage) Modifier.verticalScroll(rememberScrollState()) else Modifier,
                ) {
                    CupertinoActionItem(
                        title = stringResource(
                            if (hasExtraPage) SYMR.strings.action_set_first_page_cover else MR.strings.set_as_cover,
                        ),
                        icon = Icons.Outlined.Photo,
                        onClick = {
                            useExtraPage = false
                            showSetCoverDialog = true
                        },
                    )

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    )

                    CupertinoActionItem(
                        title = stringResource(
                            if (hasExtraPage) KMR.strings.action_copy_to_clipboard_first_page else MR.strings.action_copy_to_clipboard,
                        ),
                        icon = Icons.Outlined.ContentCopy,
                        onClick = {
                            onShare(true, false)
                            onDismissRequest()
                        },
                    )

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    )

                    CupertinoActionItem(
                        title = stringResource(
                            if (hasExtraPage) SYMR.strings.action_share_first_page else MR.strings.action_share,
                        ),
                        icon = Icons.Outlined.Share,
                        onClick = {
                            onShare(false, false)
                            onDismissRequest()
                        },
                    )

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    )

                    CupertinoActionItem(
                        title = stringResource(
                            if (hasExtraPage) SYMR.strings.action_save_first_page else MR.strings.action_save,
                        ),
                        icon = Icons.Outlined.Save,
                        onClick = {
                            onSave(false)
                            onDismissRequest()
                        },
                    )

                    if (hasExtraPage) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        )

                        CupertinoActionItem(
                            title = stringResource(SYMR.strings.action_set_second_page_cover),
                            icon = Icons.Outlined.Photo,
                            onClick = {
                                useExtraPage = true
                                showSetCoverDialog = true
                            },
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        )

                        CupertinoActionItem(
                            title = stringResource(KMR.strings.action_copy_to_clipboard_second_page),
                            icon = Icons.Outlined.ContentCopy,
                            onClick = {
                                onShare(true, true)
                                onDismissRequest()
                            },
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        )

                        CupertinoActionItem(
                            title = stringResource(SYMR.strings.action_share_second_page),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onShare(false, true)
                                onDismissRequest()
                            },
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        )

                        CupertinoActionItem(
                            title = stringResource(SYMR.strings.action_save_second_page),
                            icon = Icons.Outlined.Save,
                            onClick = {
                                onSave(true)
                                onDismissRequest()
                            },
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        )

                        CupertinoActionItem(
                            title = stringResource(KMR.strings.action_copy_to_clipboard_combined_page),
                            icon = Icons.Outlined.ContentCopy,
                            onClick = {
                                onShareCombined(true)
                                onDismissRequest()
                            },
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        )

                        CupertinoActionItem(
                            title = stringResource(SYMR.strings.action_share_combined_page),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onShareCombined(false)
                                onDismissRequest()
                            },
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        )

                        CupertinoActionItem(
                            title = stringResource(SYMR.strings.action_save_combined_page),
                            icon = Icons.Outlined.Save,
                            onClick = {
                                onSaveCombined()
                                onDismissRequest()
                            },
                        )
                    }
                }

                CupertinoCancelPill(
                    onClick = onDismissRequest,
                    text = stringResource(MR.strings.action_cancel),
                )
            }
        }
    }

    if (showSetCoverDialog) {
        SetCoverDialog(
            onConfirm = {
                onSetAsCover(useExtraPage)
                showSetCoverDialog = false
                useExtraPage = false
            },
            onDismiss = { showSetCoverDialog = false },
        )
    }
}

@Composable
private fun CupertinoActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = if (isDestructive) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurface,
            ),
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) Color(0xFFFF3B30) else Color(0xFF0A84FF),
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun SetCoverDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    CupertinoAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(MR.strings.set_as_cover))
        },
        text = {
            Text(stringResource(MR.strings.confirm_set_image_as_cover))
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onConfirm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(MR.strings.action_ok),
                    color = Color(0xFF0A84FF),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(MR.strings.action_cancel),
                    color = Color(0xFF0A84FF),
                    fontWeight = FontWeight.Normal,
                )
            }
        },
    )
}
