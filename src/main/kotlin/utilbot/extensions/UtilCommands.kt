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
import utilbot.util.Util.JANKSON
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.time.Duration.Companion.milliseconds

class UtilCommands : Extension() {
    override val name = "util"

    val tags = HashMap<String, EmbedData>()

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
                    embed {
                        val embedData = tags[arguments.tag]

                        if (embedData == null) {
                            description = "Couldnt find the tag ${arguments.tag}"
                            return@action
                        }

                        title = embedData.title
                        description = embedData.description
                        image = embedData.image
                        color = Color(embedData.color)

                        if (embedData.thumbnail != null) {
                            thumbnail {
                                url = embedData.thumbnail
                            }
                        }
                    }
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
                val e = JANKSON.fromJsonCarefully(file.readText(), EmbedData::class.java)
                tags[name] = e
            }
        }
    }

    class EmbedData {
        val title: String? = null
        val description: String? = null
        val image: String? = null
        val thumbnail: String? = null
        val color: Int = 0x202225
    }
}
