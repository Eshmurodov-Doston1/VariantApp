package uz.gxteam.variantapp.error

import uz.gxteam.variantapp.error.errorMain.Errors

data class ErrorJoin (
    var errors1:List<Errors>?=null,
    var status:Int?=null
    )