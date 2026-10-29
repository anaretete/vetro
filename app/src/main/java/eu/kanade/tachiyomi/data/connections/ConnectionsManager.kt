// AM (CONNECTIONS) -->
package eu.kanade.tachiyomi.data.connections

import eu.kanade.tachiyomi.data.connections.discord.Discord
import eu.kanade.tachiyomi.data.connections.webhook.Webhook

class ConnectionsManager {

    companion object {
        const val DISCORD = 201L
        const val WEBHOOK = 202L
    }

    val discord = Discord(DISCORD)
    val webhook = Webhook(WEBHOOK)

    private val services = listOf(discord, webhook)

    fun getService(id: Long) = services.find { it.id == id }
}
// <-- AM (CONNECTIONS)
