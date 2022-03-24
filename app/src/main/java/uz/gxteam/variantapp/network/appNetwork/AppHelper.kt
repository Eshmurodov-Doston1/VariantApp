package uz.gxteam.variantapp.network.appNetwork

import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.oneApplication.sendToken.SendToken
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser
import uz.gxteam.variantapp.models.webSocket.sendSocket.SendSocketData
import javax.inject.Inject

class AppHelper @Inject constructor(
    private val appService: AppService
) {
    suspend fun createApplication(token:String) = flow { emit( appService.createApplication(token)) }

    suspend fun getApplications(token:String) = flow { emit( appService.getApplications(token)) }

    suspend fun getAllMessage(reqMessage: ReqMessage,token:String) = flow { emit( appService.getAllMessage(reqMessage,token)) }

    suspend fun sendMessageChat(reqMessageUser: SendMessageUser,token:String) = flow { emit( appService.sendMessageChat(reqMessageUser,token)) }

    suspend fun authBroadCasting(sendSocketData: SendSocketData,token:String) = flow { emit( appService.authBroadCasting(sendSocketData,token)) }

    suspend fun authBroadCastingAppStatus(sendSocketData: SendSocketData,token:String) = flow { emit( appService.authBroadCastingAppStatus(sendSocketData,token)) }

    suspend fun getAllApplicationsSuccess(token:String) = flow { emit( appService.getAllApplicationsSuccess(token)) }

    suspend fun getOneApplication(sendToken: SendToken,token:String) = flow { emit(appService.getApplication(sendToken,token)) }

    suspend fun getUserData(token:String) = flow { emit( appService.getUserData(token)) }
}