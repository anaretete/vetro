package eu.kanade.tachiyomi.data.connections.webhook

import android.graphics.Color
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.connections.ConnectionsService
import tachiyomi.i18n.kmk.KMR

class Webhook(id: Long) : ConnectionsService(id) {

    override fun nameStrRes() = KMR.strings.connections_webhook

    override fun getLogo() = R.drawable.ic_webhook_24dp

    override fun getLogoColor() = Color.rgb(10, 132, 255)

    override fun logout() {
        super.logout()
        connectionsPreferences.enableWebhooks().set(false)
        connectionsPreferences.webhookUrl().set("")
    }

    override suspend fun login(username: String, password: String) {
        // Configured directly via Webhook URL
    }

    override val isLogged: Boolean
        get() = connectionsPreferences.enableWebhooks().get() &&
            connectionsPreferences.webhookUrl().get().isNotBlank()

    override fun getToken(): String {
        return connectionsPreferences.webhookUrl().get()
    }
}
