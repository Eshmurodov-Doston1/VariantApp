package uz.gxteam.variantapp.models.getApplications

import java.io.Serializable

data class Data(
    var id:Long,
    val created_at: String,
    val status: Int,
    val token: String,
    var client:String?=null
):Serializable