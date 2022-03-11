package uz.gxteam.variantapp.models.chat.messages.resMessage

data class Message(
    val admin: List<Admin>,
    val messages: List<MessageX>,
    val status: Int
)