package utilbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import utilbot.extensions.LogUpload
import utilbot.extensions.UtilCommands
import utilbot.util.Util

suspend fun main() {
    val bot = ExtensibleBot(Util.TOKEN) {
        plugins {
            enabled = false
        }
        extensions {
            add(::UtilCommands)
            //add(::MappingCommands)
            add(::LogUpload)
        }
    }
    bot.start()
}
