package utilbot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.defaultingStringChoice
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.waitForMessage
import dev.kord.core.entity.Message

class MappingCommands : Extension() {
    override val name = "mapping"

    override suspend fun setup() {
        publicSlashCommand(::MapLogArgs) {
            name = "maplog"
            description = "Remaps a game log with the specified mapping."

            action {
                respond {
                    content = "Send a file to remap within 30 seconds"
                }
                val message: Message? = waitForMessage(30_000) {
                    message.author == user.asUser() && message.attachments.isNotEmpty()
                }

                if (message == null) {
                    respond {
                        content = "No messages with attachments sent. Aborting."
                    }
                    return@action
                }

                for (attachment in message.attachments) {
                    println(attachment.filename)
                }
            }
        }
    }

    inner class MapLogArgs : Arguments() {
        val mapping by stringChoice {
            name = "mapping"
            description = "The Mapping to use"

            choices["yarn"] = "yarn"
        }

        val version by defaultingStringChoice {
            name = "version"
            description = "The version to use"
            defaultValue = ""
        }
    }
}
