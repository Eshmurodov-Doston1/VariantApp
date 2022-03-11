package uz.gxteam.variantapp.models.mainClass

import android.net.Uri

data class Request(
    var name:String,
    var number:String,
    var category:String,
    var colorCategory:String,
    var isChecked:Boolean,
    var date:String,
    var image:String?=null,
    var type:Int?=null,
    var imageMain:Uri?=null
)