package utilbot.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utilbot.util.Constants
import utilbot.util.Util

class VersionCommand : Extension() {
    override val name = "version"

    override suspend fun setup() {
        ephemeralSlashCommand(::Args) {
            name = this@VersionCommand.name
            description = "Get Current Fabric versions for a given Minecraft version"

            val json = Json {
                ignoreUnknownKeys = true
            }

            action {
                val yarnResponse =
                    Util.client.get("${Constants.FABRIC_META_URL}/versions/yarn/${arguments.version}?limit=1")
                        .bodyAsText().removePrefix("[").removeSuffix("]").ifBlank { "{}" }
                val yarnVersion: YarnVersion = json.decodeFromString(yarnResponse)

                val loaderResponse =
                    Util.client.get("${Constants.FABRIC_META_URL}/versions/loader/${arguments.version}?limit=1")
                        .bodyAsText().removePrefix("[").removeSuffix("]").ifBlank { "{}" }
                val loaderVersion: LoaderWrapper = json.decodeFromString(loaderResponse)

                respond {
                    content = """
                        ```
                        minecraft_version=${yarnVersion.gameVersion ?: "unknown"}
                        yarn_mappings=${yarnVersion.version ?: "unknown"}
                        loader_version=${loaderVersion.loader?.version ?: "unknown"}
                        ```
                        """.trimIndent()
                }
            }
        }
    }

    inner class Args : Arguments() {
        val version by string {
            name = this@VersionCommand.name
            description = "Get the latest fabric versions for the given Game version"
        }
    }

    class YarnVersion {
        var gameVersion: String? = null
        var version: String? = null
    }

    class LoaderWrapper {
        val loader: LoaderVersion? = null
    }

    class LoaderVersion {
        val version: String? = null
    }
}
