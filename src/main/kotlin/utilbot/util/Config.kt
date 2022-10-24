package utilbot.util

import com.kotlindiscord.kord.extensions.utils.env

class Config {
    val logFileExtensions = env("LOG_EXTENSIONS").replace(" ", "").split(",")
}
