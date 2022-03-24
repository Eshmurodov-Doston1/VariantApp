package uz.gxteam.variantapp.models.chat.messages.resMessage

data class Message(
    val status: Int,
    val admin: Any,
    val messages: List<MessageX>
)