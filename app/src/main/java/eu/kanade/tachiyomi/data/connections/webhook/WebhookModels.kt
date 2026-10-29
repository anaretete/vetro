package eu.kanade.tachiyomi.data.connections.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordWebhookPayload(
    val username: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    val content: String? = null,
    val embeds: List<DiscordEmbed>? = null,
)

@Serializable
data class DiscordEmbed(
    val title: String,
    val description: String? = null,
    val color: Int? = 0x0A84FF,
    val timestamp: String? = null,
    val thumbnail: EmbedThumbnail? = null,
    val footer: EmbedFooter? = null,
    val fields: List<EmbedField>? = null,
)

@Serializable
data class EmbedThumbnail(
    val url: String,
)

@Serializable
data class EmbedFooter(
    val text: String,
    @SerialName("icon_url")
    val iconUrl: String? = null,
)

@Serializable
data class EmbedField(
    val name: String,
    val value: String,
    val inline: Boolean = true,
)
