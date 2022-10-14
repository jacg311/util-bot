package utilbot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicMessageCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.types.respondEphemeral
import com.kotlindiscord.kord.extensions.utils.download
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.followup.edit
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Attachment
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.modify.actionRow
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utilbot.util.Util.MCLOGS_BASE_URL
import utilbot.util.Util.client

class LogUpload : Extension() {
    override val name = "log_upload"
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
                val attachments = event.message.attachments
                    .filter { isValidLog(it, listOf(".log", "server.txt", "client.txt")) }

                if (attachments.isEmpty()) return@action

                val reply = event.message.reply { content = "Logs detected, uploading..." }
                val logs = uploadLogFiles(attachments)

                reply.edit {
                    if (logs.isEmpty()) {
                        content = "Failed to upload!"
                        return@edit
                    }

                    content = "Uploaded ${logs.size} logs"

                    actionRow {
                        for (response in logs) {
                            response.second.url?.let {
                                linkButton(it) {
                                    label = response.first
                                }
                            }
                        }
                    }
                }
            }
        }

        publicMessageCommand {
            name = "Upload Logs"

            action {
                val attachments = event.interaction.messages.values.flatMap { message ->
                    message.attachments.filter { isValidLog(it) }
                }

                if (attachments.isEmpty()) {
                    respondEphemeral { content = "No valid logs found" }
                    return@action
                }

                val reply = respond { content = "Found logs, uploading..." }
                val logs = uploadLogFiles(attachments)

                reply.edit {
                    if (logs.isEmpty()) {
                        content = "Failed to upload!"
                        return@edit
                    }

                    content = "Uploaded ${logs.size} logs"

                    actionRow {
                        for (response in logs) {
                            response.second.url?.let {
                                linkButton(it) {
                                    label = response.first
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValidLog(attachment: Attachment, allowedExtensions: List<String>? = null): Boolean {
        return attachment.size < 10_000_000 &&
                attachment.contentType?.contains("text/plain") != false &&
                (allowedExtensions == null || allowedExtensions.any { attachment.filename.endsWith(it) })
    }

    private suspend fun uploadLogFiles(attachments: List<Attachment>): List<Pair<String, LogData>> {
        val responses = attachments.map {
            it.filename to it.download().decodeToString()
        }.map {
            it.first to upload(it.second)
        }

        return responses.filter { it.second.success }
    }

    private suspend fun upload(log: String): LogData {
        return json.decodeFromString(
            client.post("$MCLOGS_BASE_URL/1/log") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("content", log)
                        }
                    )
                )
            }.readBytes().decodeToString()
        )
    }

    @Serializable
    data class LogData(val success: Boolean, val url: String? = null, val error: String? = null)
}
