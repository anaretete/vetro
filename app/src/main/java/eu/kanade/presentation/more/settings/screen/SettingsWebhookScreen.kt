package eu.kanade.presentation.more.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.domain.connections.service.ConnectionsPreferences
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.tachiyomi.data.connections.ConnectionsManager
import eu.kanade.tachiyomi.data.connections.webhook.WebhookService
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.core.common.util.lang.withUIContext
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.components.material.AlertDialog
import tachiyomi.presentation.core.components.material.TextButton
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

object SettingsWebhookScreen : SearchableSettings {
    @Suppress("unused")
    private fun readResolve(): Any = SettingsWebhookScreen

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = KMR.strings.connections_webhook

    @Composable
    override fun getPreferences(): List<Preference> {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val connectionsPreferences = remember { Injekt.get<ConnectionsPreferences>() }
        val connectionsManager = remember { Injekt.get<ConnectionsManager>() }

        val enableWebhooksPref = connectionsPreferences.enableWebhooks()
        val webhookUrlPref = connectionsPreferences.webhookUrl()
        val botNamePref = connectionsPreferences.webhookBotName()
        val botAvatarPref = connectionsPreferences.webhookBotAvatar()
        val notifyChapterPref = connectionsPreferences.webhookNotifyChapterRead()
        val notifyUpdatePref = connectionsPreferences.webhookNotifyLibraryUpdate()
        val notifyDownloadPref = connectionsPreferences.webhookNotifyDownload()

        val enabled by enableWebhooksPref.collectAsState()
        val webhookUrl by webhookUrlPref.collectAsState()
        val botName by botNamePref.collectAsState()
        val botAvatar by botAvatarPref.collectAsState()

        var showUrlDialog by remember { mutableStateOf(false) }
        var tempUrl by remember(webhookUrl) { mutableStateOf(webhookUrl) }

        var showBotNameDialog by remember { mutableStateOf(false) }
        var tempBotName by remember(botName) { mutableStateOf(botName) }

        var showAvatarDialog by remember { mutableStateOf(false) }
        var tempAvatar by remember(botAvatar) { mutableStateOf(botAvatar) }

        var testingWebhook by remember { mutableStateOf(false) }

        if (showUrlDialog) {
            AlertDialog(
                onDismissRequest = { showUrlDialog = false },
                title = { Text(stringResource(KMR.strings.pref_webhook_url)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempUrl,
                            onValueChange = { tempUrl = it },
                            label = { Text(stringResource(KMR.strings.pref_webhook_url_summary)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            webhookUrlPref.set(tempUrl.trim())
                            showUrlDialog = false
                        },
                    ) {
                        Text(stringResource(MR.strings.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUrlDialog = false }) {
                        Text(stringResource(MR.strings.action_cancel))
                    }
                },
            )
        }

        if (showBotNameDialog) {
            AlertDialog(
                onDismissRequest = { showBotNameDialog = false },
                title = { Text(stringResource(KMR.strings.pref_webhook_bot_name)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempBotName,
                            onValueChange = { tempBotName = it },
                            label = { Text("Bot Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            botNamePref.set(tempBotName.trim())
                            showBotNameDialog = false
                        },
                    ) {
                        Text(stringResource(MR.strings.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBotNameDialog = false }) {
                        Text(stringResource(MR.strings.action_cancel))
                    }
                },
            )
        }

        if (showAvatarDialog) {
            AlertDialog(
                onDismissRequest = { showAvatarDialog = false },
                title = { Text(stringResource(KMR.strings.pref_webhook_bot_avatar)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempAvatar,
                            onValueChange = { tempAvatar = it },
                            label = { Text("https://...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            botAvatarPref.set(tempAvatar.trim())
                            showAvatarDialog = false
                        },
                    ) {
                        Text(stringResource(MR.strings.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAvatarDialog = false }) {
                        Text(stringResource(MR.strings.action_cancel))
                    }
                },
            )
        }

        return listOf(
            Preference.PreferenceGroup(
                title = stringResource(KMR.strings.connections_webhook),
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SwitchPreference(
                        preference = enableWebhooksPref,
                        title = stringResource(KMR.strings.pref_enable_webhooks),
                    ),
                    Preference.PreferenceItem.InfoPreference(
                        stringResource(KMR.strings.pref_webhook_info),
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "Configuration",
                enabled = enabled,
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.TextPreference(
                        title = stringResource(KMR.strings.pref_webhook_url),
                        subtitle = if (webhookUrl.isBlank()) stringResource(KMR.strings.pref_webhook_url_summary) else webhookUrl,
                        onClick = {
                            tempUrl = webhookUrl
                            showUrlDialog = true
                        },
                    ),
                    Preference.PreferenceItem.TextPreference(
                        title = stringResource(KMR.strings.pref_webhook_bot_name),
                        subtitle = botName.ifBlank { "Vetro" },
                        onClick = {
                            tempBotName = botName
                            showBotNameDialog = true
                        },
                    ),
                    Preference.PreferenceItem.TextPreference(
                        title = stringResource(KMR.strings.pref_webhook_bot_avatar),
                        subtitle = if (botAvatar.isBlank()) "Default Icon" else botAvatar,
                        onClick = {
                            tempAvatar = botAvatar
                            showAvatarDialog = true
                        },
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "Notification Events",
                enabled = enabled && webhookUrl.isNotBlank(),
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.SwitchPreference(
                        preference = notifyChapterPref,
                        title = stringResource(KMR.strings.pref_webhook_notify_chapter),
                        subtitle = stringResource(KMR.strings.pref_webhook_notify_chapter_summary),
                    ),
                    Preference.PreferenceItem.SwitchPreference(
                        preference = notifyUpdatePref,
                        title = stringResource(KMR.strings.pref_webhook_notify_update),
                        subtitle = stringResource(KMR.strings.pref_webhook_notify_update_summary),
                    ),
                    Preference.PreferenceItem.SwitchPreference(
                        preference = notifyDownloadPref,
                        title = stringResource(KMR.strings.pref_webhook_notify_download),
                        subtitle = stringResource(KMR.strings.pref_webhook_notify_download_summary),
                    ),
                ),
            ),
            Preference.PreferenceGroup(
                title = "Testing & Actions",
                enabled = enabled && webhookUrl.isNotBlank(),
                preferenceItems = persistentListOf(
                    Preference.PreferenceItem.TextPreference(
                        title = stringResource(KMR.strings.pref_webhook_test),
                        subtitle = if (testingWebhook) "Sending test payload..." else stringResource(KMR.strings.pref_webhook_test_summary),
                        enabled = !testingWebhook,
                        onClick = {
                            testingWebhook = true
                            scope.launchIO {
                                val result = WebhookService.sendTestWebhook()
                                withUIContext {
                                    testingWebhook = false
                                    result.fold(
                                        onSuccess = {
                                            context.toast(KMR.strings.pref_webhook_test_success)
                                        },
                                        onFailure = { err ->
                                            context.toast(err.message ?: "Failed to send test webhook")
                                        },
                                    )
                                }
                            }
                        },
                    ),
                    Preference.PreferenceItem.TextPreference(
                        title = stringResource(MR.strings.action_disconnect),
                        onClick = {
                            connectionsManager.webhook.logout()
                            context.toast(MR.strings.action_disconnect)
                            navigator.pop()
                        },
                    ),
                ),
            ),
        )
    }
}
