package utilbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import utilbot.extensions.LogUpload
import utilbot.extensions.PingCommand
import utilbot.extensions.TagCommand
import utilbot.extensions.VersionCommand
import utilbot.util.Util

suspend fun main() {
    val bot = ExtensibleBot(Util.TOKEN) {
        plugins {
            enabled = false
        }
        extensions {
            add(::VersionCommand)
            add(::TagCommand)
            add(::PingCommand)
            //add(::MappingCommand)
            add(::LogUpload)
        }
    }
    bot.start()
}
