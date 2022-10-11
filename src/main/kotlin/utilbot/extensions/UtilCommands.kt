package utilbot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.Color
import dev.kord.rest.builder.message.create.embed
import java.nio.file.Files
import java.util.*
import kotlin.collections.HashMap
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.time.Duration.Companion.milliseconds

class UtilCommands : Extension() {
    override val name = "util"

    val tags = HashMap<String, String>()

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "ping"
            description = "Show the ping of the bot."

            action {
                respond {
                    val ping = this@UtilCommands.kord.gateway.averagePing
                    embed {
                        description = "Avg. Ping: $ping"
                        if (ping != null) {
                            color = when (ping > 100.milliseconds) {
                                true -> Color(255, 0, 0)
                                false -> Color(0, 255, 0)
                            }
                        }
                    }
                }
            }
        }

        publicSlashCommand(::TagArguments) {
            name = "tag"
            description = "Show a tag"

            action {
                respond {
                    content = tags[arguments.tag]
                }
            }
        }
    }

    inner class TagArguments : Arguments() {
        val tag by stringChoice {
            name = "tag"
            description = "The tag to show"

            for (file in Files.list(Path(env("TAG_FOLDER")))) {
                val fileName = file.nameWithoutExtension
                val name = fileName.lowercase(Locale.ROOT).replace(" ", "_")
                choices[fileName] = name
                tags[name] = file.readText()
            }
        }
    }
}
