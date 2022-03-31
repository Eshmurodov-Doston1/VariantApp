package uz.gxteam.variantapp.utils

import androidx.lifecycle.LiveData
import uz.gxteam.variantapp.database.entity.DataApplicationEntity
import uz.gxteam.variantapp.error.AppError
import uz.gxteam.variantapp.models.application.Application
import uz.gxteam.variantapp.models.cancelApplication.ResCancelApp
import uz.gxteam.variantapp.models.chat.messages.resMessage.Message
import uz.gxteam.variantapp.models.getApplications.Applications
import uz.gxteam.variantapp.models.logOut.LogOut
import uz.gxteam.variantapp.models.login.ResLogin
import uz.gxteam.variantapp.models.oneApplication.OneApplication
import uz.gxteam.variantapp.models.sendMessage.ResMessageUser
import uz.gxteam.variantapp.models.upload.UploadData
import uz.gxteam.variantapp.models.userData.UserData
import uz.gxteam.variantapp.models.webSocket.ResSocket
import uz.gxteam.variantapp.models.webSocket.chatAppStatus.ChatAppStatus

sealed class VariantResourse {
    object Loading:VariantResourse()

    data class SuccessLogin(var resLogin: ResLogin?):VariantResourse()
    data class SuccessLogOut(var logOut: LogOut?):VariantResourse()
    data class SuccessCreateApplication(var application: Application?):VariantResourse()
    data class SuccessApplications(var applications: Applications?):VariantResourse()
    data class ApplicationsSuccess(var applications: Applications?):VariantResourse()
    data class SuccessGetApplication(var oneApplication: OneApplication?):VariantResourse()
    data class SuccessGetAllMessage(var message: Message?):VariantResourse()
    data class SuccessCancelApplication(var resCancelApp: ResCancelApp?):VariantResourse()
    data class SuccessSendChat(var resMessageUser: ResMessageUser?):VariantResourse()
    data class SuccessBroadCastingAuth(var resSocket: ResSocket?):VariantResourse()
    data class SuccessBroadCastingAuthApp(var chatAppStatus: ChatAppStatus?):VariantResourse()
    data class SuccessUserData(var userData: UserData?):VariantResourse()
    data class SuccessUpload(var uploadData: UploadData?):VariantResourse()

    data class Error(var appError: AppError):VariantResourse()
}