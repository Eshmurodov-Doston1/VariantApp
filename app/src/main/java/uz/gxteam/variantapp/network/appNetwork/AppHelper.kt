package uz.gxteam.variantapp.network.appNetwork

import kotlinx.coroutines.flow.flow
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser
import javax.inject.Inject

class AppHelper @Inject constructor(
    private val appService: AppService
) {
    suspend fun createApplication(token:String) = flow { emit( appService.createApplication(token)) }

    suspend fun getApplications(token:String) = flow { emit( appService.getApplications(token)) }

    suspend fun getAllMessage(reqMessage: ReqMessage,token:String) = flow { emit( appService.getAllMessage(reqMessage,token)) }

    suspend fun sendMessageChat(reqMessageUser: SendMessageUser,token:String) = flow { emit( appService.sendMessageChat(reqMessageUser,token)) }
}