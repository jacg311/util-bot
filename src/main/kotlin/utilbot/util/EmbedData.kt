package utilbot.util

import dev.kord.common.Color
import kotlinx.serialization.Serializable

@Serializable
data class EmbedData(
    val title: String? = null,
    val description: String,
    val image: String? = null,
    val thumbnail: String? = null,
    val color: Color? = null,
)
