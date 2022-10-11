package utilbot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.download
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.behavior.edit
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.modify.actionRow
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utilbot.Util.MCLOGS_BASE_URL
import utilbot.Util.client

class LogUpload : Extension() {
    override val name = "log_upload"

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
                //if (event.message.channelId !in Util.CONFIG.logUploadAllowedIds) {
                //    return@action
                //}

                for (attachment in event.message.attachments) {
                    if (attachment.size > 10_000_000 && attachment.contentType?.contains("text/plain") == false) {
                        return@action
                    }
                    val message = event.message.respond { content = "Uploading ${attachment.filename}" }
                    val text = attachment.download().decodeToString()

                    val response = client.post("$MCLOGS_BASE_URL/1/log") {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            FormDataContent(
                                Parameters.build {
                                    append("content", text)
                                }
                            )
                        )
                    }.readBytes().decodeToString()

                    val json = Json { ignoreUnknownKeys = true }
                    val log = json.decodeFromString<LogData>(response)

                    message.edit {
                        content = if (log.success) "${attachment.filename} uploaded" else "Failed to upload!"

                        if (log.error != null) content += " ${log.error}"

                        if (log.url != null) {
                            actionRow {
                                linkButton(log.url) {
                                    label = "Click to view"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class LogData(val success: Boolean, val url: String? = null, val error: String? = null)
}
