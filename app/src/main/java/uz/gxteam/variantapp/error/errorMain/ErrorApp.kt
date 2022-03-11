package uz.gxteam.variantapp.error.errorMain

import com.google.gson.annotations.SerializedName
import uz.gxteam.variantapp.error.errorMain.arrayError.Error

data class ErrorApp(
    val errors: Errors?=null,
    var errors1:List<Errors>?=null,
    var status:Int?=null
)