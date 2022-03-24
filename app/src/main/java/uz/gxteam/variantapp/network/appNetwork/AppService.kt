package uz.gxteam.variantapp.network.appNetwork

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.gxteam.variantapp.models.application.Application
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.chat.messages.resMessage.Message
import uz.gxteam.variantapp.models.getApplications.Applications
import uz.gxteam.variantapp.models.oneApplication.OneApplication
import uz.gxteam.variantapp.models.oneApplication.sendToken.SendToken
import uz.gxteam.variantapp.models.sendMessage.ResMessageUser
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser
import uz.gxteam.variantapp.models.upload.UploadData
import uz.gxteam.variantapp.models.webSocket.ResSocket
import uz.gxteam.variantapp.models.webSocket.chatAppStatus.ChatAppStatus
import uz.gxteam.variantapp.models.webSocket.sendSocket.SendSocketData


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

    @POST("api/broadcasting/auth")
    suspend fun authBroadCasting(
        @Body sendSocketData: SendSocketData,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"):Response<ResSocket>

    @POST("api/broadcasting/auth")
    suspend fun authBroadCastingAppStatus(
        @Body sendSocketData: SendSocketData,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"):Response<ChatAppStatus>


    @GET("/api/user/detail")
    suspend fun getUserData(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<uz.gxteam.variantapp.models.userData.UserData>


    @POST("api/chat/finished/applications")
    suspend fun getAllApplicationsSuccess(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<Applications>


    @POST("/api/chat/application")
    suspend fun getApplication(
        @Body sendToken: SendToken,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<OneApplication>

}