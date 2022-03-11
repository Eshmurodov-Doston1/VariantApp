package uz.gxteam.variantapp.error

import uz.gxteam.variantapp.error.errorMain.ErrorApp


data class AppError(
    var error: ErrorApp?=null,
    var code:Int?=null,
    var message:String?=null,
    var internetConnection:Boolean?=null
)