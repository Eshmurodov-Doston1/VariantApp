package uz.gxteam.variantapp.models.oneApplication

import java.io.Serializable

data class OneApplication(
    val client: Client,
    val created_at: String,
    val id: Int,
    val status: Int,
    val status_name: StatusName,
    val token: String
):Serializable