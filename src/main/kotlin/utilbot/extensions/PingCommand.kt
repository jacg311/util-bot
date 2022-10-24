package utilbot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.Color
import dev.kord.rest.builder.message.create.embed
import kotlin.time.Duration.Companion.milliseconds

class PingCommand : Extension() {
    override val name = "ping"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "ping"
            description = "Show the ping of the bot."

            action {
                respond {
                    val ping = this@PingCommand.kord.gateway.averagePing
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
    }
}
