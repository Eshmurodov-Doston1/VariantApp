package uz.gxteam.variantapp.models.chat.messages.resMessage

data class MessageX(
    val chat_application_id: Int,
    val created_at: String,
    val `file`: Any,
    val id: Int,
    val message: String,
    val status: Int,
    val updated_at: String,
    val user_id: Int
)