package uz.gxteam.variantapp.utils

import androidx.lifecycle.LiveData
import com.squareup.okhttp.ResponseBody
import uz.gxteam.variantapp.database.entity.ApplicationsEntity
import uz.gxteam.variantapp.error.AppError
import uz.gxteam.variantapp.models.application.Application
import uz.gxteam.variantapp.models.chat.messages.resMessage.Message
import uz.gxteam.variantapp.models.getApplications.Applications
import uz.gxteam.variantapp.models.logOut.LogOut
import uz.gxteam.variantapp.models.login.ResLogin
import uz.gxteam.variantapp.models.sendMessage.ResMessageUser

sealed class VariantResourse {
    object Loading:VariantResourse()

    data class SuccessLogin(var resLogin: ResLogin?):VariantResourse()
    data class SuccessLogOut(var logOut: LogOut?):VariantResourse()
    data class SuccessCreateApplication(var application: Application?):VariantResourse()
    data class SuccessApplications(var applications:Applications?):VariantResourse()
    data class SuccessGetAllMessage(var message:Message?):VariantResourse()
    data class SuccessSendChat(var resMessageUser: ResMessageUser?):VariantResourse()

    data class Error(var appError: AppError):VariantResourse()
}