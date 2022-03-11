package uz.gxteam.variantapp.network.appNetwork

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import uz.gxteam.variantapp.models.application.Application
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.chat.messages.resMessage.Message
import uz.gxteam.variantapp.models.getApplications.Applications
import uz.gxteam.variantapp.models.sendMessage.ResMessageUser
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser

interface AppService {

    @POST("/api/chat/create/application")
    suspend fun createApplication(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<Application>


    @POST("/api/chat/get/applications")
    suspend fun getApplications(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<Applications>


    @POST("/api/chat/join")
    suspend fun getAllMessage(
        @Body reqMessage: ReqMessage,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<Message>


    @POST("/api/chat/send/message")
    suspend fun sendMessageChat(
        @Body sendMessageUser: SendMessageUser,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<ResMessageUser>


}