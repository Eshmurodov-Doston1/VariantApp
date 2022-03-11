package uz.gxteam.variantapp.error.errorMain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Errors(
    @SerializedName("field_name")
    @Expose
    val field_name: String,
    @SerializedName("message")
    @Expose
    val message: String
)