package eu.kanade.tachiyomi.data.connections.webhook

import eu.kanade.domain.connections.service.ConnectionsPreferences
import eu.kanade.tachiyomi.network.NetworkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object WebhookService {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun getTimestamp(): String = isoDateFormat.format(Date())

    private fun getPreferences(): ConnectionsPreferences = Injekt.get()

    private fun getClient() = Injekt.get<NetworkHelper>().client

    suspend fun sendPayload(payload: DiscordWebhookPayload): Result<Unit> = withContext(Dispatchers.IO) {
        val prefs = getPreferences()
        if (!prefs.enableWebhooks().get()) {
            return@withContext Result.failure(IllegalStateException("Webhooks are disabled in settings"))
        }
        val url = prefs.webhookUrl().get().trim()
        if (url.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Webhook URL is empty"))
        }

        try {
            val bodyString = json.encodeToString(payload)
            val requestBody = bodyString.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = getClient().newCall(request).execute()
            if (response.isSuccessful || response.code in 200..299) {
                Result.success(Unit)
            } else {
                val errorMsg = "HTTP ${response.code}: ${response.message}"
                Timber.e("Webhook send failed: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error sending webhook")
            Result.failure(e)
        }
    }

    suspend fun sendTestWebhook(): Result<Unit> {
        val prefs = getPreferences()
        val botName = prefs.webhookBotName().get().ifBlank { "Vetro" }
        val botAvatar = prefs.webhookBotAvatar().get().ifBlank { null }

        val payload = DiscordWebhookPayload(
            username = botName,
            avatarUrl = botAvatar,
            embeds = listOf(
                DiscordEmbed(
                    title = "⚡ Vetro Webhook Connection Test",
                    description = "Your Vetro Webhook integration has been successfully configured and connected!",
                    color = 0x30D158,
                    timestamp = getTimestamp(),
                    footer = EmbedFooter(text = "Vetro • Liquid Glass Reader"),
                    fields = listOf(
                        EmbedField(name = "Status", value = "Connected", inline = true),
                        EmbedField(name = "Client", value = "Android Native", inline = true),
                    ),
                ),
            ),
        )
        return sendPayload(payload)
    }

    suspend fun notifyChapterRead(
        mangaTitle: String,
        chapterName: String,
        coverUrl: String? = null,
    ): Result<Unit> {
        val prefs = getPreferences()
        if (!prefs.enableWebhooks().get() || !prefs.webhookNotifyChapterRead().get()) {
            return Result.success(Unit)
        }

        val botName = prefs.webhookBotName().get().ifBlank { "Vetro" }
        val botAvatar = prefs.webhookBotAvatar().get().ifBlank { null }

        val payload = DiscordWebhookPayload(
            username = botName,
            avatarUrl = botAvatar,
            embeds = listOf(
                DiscordEmbed(
                    title = "📖 Finished Reading Chapter",
                    description = "**$mangaTitle**\n$chapterName",
                    color = 0x0A84FF,
                    timestamp = getTimestamp(),
                    thumbnail = coverUrl?.let { EmbedThumbnail(it) },
                    footer = EmbedFooter(text = "Vetro Reader"),
                    fields = listOf(
                        EmbedField(name = "Manga", value = mangaTitle, inline = true),
                        EmbedField(name = "Chapter", value = chapterName, inline = true),
                    ),
                ),
            ),
        )
        return sendPayload(payload)
    }

    suspend fun notifyLibraryUpdate(
        updatedCount: Int,
        sampleTitles: List<String>,
    ): Result<Unit> {
        val prefs = getPreferences()
        if (!prefs.enableWebhooks().get() || !prefs.webhookNotifyLibraryUpdate().get() || updatedCount <= 0) {
            return Result.success(Unit)
        }

        val botName = prefs.webhookBotName().get().ifBlank { "Vetro" }
        val botAvatar = prefs.webhookBotAvatar().get().ifBlank { null }

        val desc = if (sampleTitles.isNotEmpty()) {
            sampleTitles.take(5).joinToString("\n") { "• $it" } + if (sampleTitles.size > 5) "\n• ...and more" else ""
        } else {
            "New chapters downloaded or detected in library."
        }

        val payload = DiscordWebhookPayload(
            username = botName,
            avatarUrl = botAvatar,
            embeds = listOf(
                DiscordEmbed(
                    title = "🔔 Library Update ($updatedCount new)",
                    description = desc,
                    color = 0xFF9F0A,
                    timestamp = getTimestamp(),
                    footer = EmbedFooter(text = "Vetro Library Sync"),
                ),
            ),
        )
        return sendPayload(payload)
    }

    suspend fun notifyDownloadComplete(
        mangaTitle: String,
        chapterName: String,
    ): Result<Unit> {
        val prefs = getPreferences()
        if (!prefs.enableWebhooks().get() || !prefs.webhookNotifyDownload().get()) {
            return Result.success(Unit)
        }

        val botName = prefs.webhookBotName().get().ifBlank { "Vetro" }
        val botAvatar = prefs.webhookBotAvatar().get().ifBlank { null }

        val payload = DiscordWebhookPayload(
            username = botName,
            avatarUrl = botAvatar,
            embeds = listOf(
                DiscordEmbed(
                    title = "📥 Download Complete",
                    description = "**$mangaTitle** — $chapterName is now available offline.",
                    color = 0xBF5AF2,
                    timestamp = getTimestamp(),
                    footer = EmbedFooter(text = "Vetro Downloader"),
                ),
            ),
        )
        return sendPayload(payload)
    }
}
