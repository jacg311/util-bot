package utilbot.extensions

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.Color
import dev.kord.common.entity.Permission
import dev.kord.rest.builder.message.create.embed
import utilbot.util.Util
import java.nio.file.Files
import java.util.*
import kotlin.collections.set
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

class TagCommand : Extension() {
    override val name = "tag"

    private val tags = HashMap<String, EmbedData>()

    override suspend fun setup() {
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

                        for (field in embedData.fields) {
                            field {
                                inline = field.inline
                                name = field.name
                                value = field.value
                            }
                        }
                    }
                }
            }
        }

        ephemeralSlashCommand {
            name = "reload"
            description = "Reload the bot commands"
            requirePermission(Permission.Administrator)
            check {
                hasPermission(Permission.Administrator)
            }

            action {
                updateTags()
                respond {
                    content = "Tags synced."
                }
            }
        }
    }

    inner class TagArguments : Arguments() {
        val tag by string {
            name = "tag"
            description = "The tag to show"

            updateTags()
        }
    }

    fun updateTags() {
        for (file in Files.list(Path(env("TAG_FOLDER")))) {
            val name = file.nameWithoutExtension.lowercase(Locale.ROOT).replace(" ", "_")
            tags[name] = Util.JANKSON.fromJsonCarefully(file.readText(), EmbedData::class.java)
        }
    }

    class EmbedData {
        val title: String? = null
        val description: String? = null
        val image: String? = null
        val thumbnail: String? = null
        val color: Int = 0x202225
        val fields: List<EmbedField> = mutableListOf()
    }

    class EmbedField {
        val inline = false
        val name = "\u200E" // Zero Width String
        val value = "\u200E"
    }
}
