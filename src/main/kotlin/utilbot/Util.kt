package utilbot

import com.kotlindiscord.kord.extensions.utils.env
import io.ktor.client.HttpClient
import mu.KotlinLogging

object Util {
    val TOKEN = env("TOKEN")
    val LOGGER = KotlinLogging.logger("util-bot")

    val CONFIG = Config()

    val client = HttpClient()
    const val LINKIE_BASE_URL = "linkieapi.shedaniel.me"
    const val MCLOGS_BASE_URL = "https://api.mclo.gs"
}
