package uz.gxteam.variantapp.models.chat.messages.resMessage

import java.io.Serializable

data class MessageX(
    val chat_application_id: Int?=null,
    val created_at: String?=null,
    val id: Int?=null,
    val message: String?=null,
    val photo: Photo?=null,
    val photo_log_id: Any?=null,
    val status: Int?=null,
    val type: Int?=null,
    val updated_at: String?=null,
    var user_id: Int?=null
):Serializable