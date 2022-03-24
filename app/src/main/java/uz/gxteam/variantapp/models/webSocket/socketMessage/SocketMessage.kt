package uz.gxteam.variantapp.models.webSocket.socketMessage

data class SocketMessage(
    val channel: String?=null,
    val data: Data?=null,
    val event: String?=null
)