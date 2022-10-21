package utilbot.util

import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake

class Config {
    val logUploadAllowedIds: Set<Snowflake>
        get() {
            TODO()
        }

    val logFileExtensions = env("LOG_EXTENSIONS").replace(" ", "").split(",")
}
