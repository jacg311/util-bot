package utilbot.util

import blue.endless.jankson.Jankson
import com.kotlindiscord.kord.extensions.utils.env
import io.ktor.client.*
import mu.KotlinLogging

object Util {
    val TOKEN = env("TOKEN")
    val LOGGER = KotlinLogging.logger("util-bot")

    val CONFIG = Config()

    val client = HttpClient()

    val JANKSON: Jankson = Jankson.builder().build()
}
