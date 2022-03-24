package uz.gxteam.variantapp.models.getApplications

import java.io.Serializable

data class DataApplication(
    val client: Any?=null,
    val created_at: String?=null,
    val id: Int?=null,
    val status: Int?=null,
    val status_name: StatusName,
    val token: String
):Serializable