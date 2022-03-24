package uz.gxteam.variantapp.models.webSocket.socketMessage

data class Data(
    val app_id: Int,
    val token: String,
    val message: String,
    var link:String?=null,
    val type: String,
    var ext:String


    /*
    * {"app_id":29,
    * "token":"YaEbFjRRRbJnmgfyJqnZEESr",
    * "message":"\u0424\u0430\u0439\u043b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d!",
    * "link":"storage\/chat\/EgV\/EgV1647580013TqDPvJbnop6234136db4c6c.png",
    * "type":1,
    * "ext":"png"}*/

)