package uz.gxteam.variantapp.viewModels.appViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.internal.notify
import uz.gxteam.variantapp.database.DatabaseHelper
import uz.gxteam.variantapp.database.entity.ApplicationsEntity
import uz.gxteam.variantapp.database.entity.DataApplicationEntity
import uz.gxteam.variantapp.database.entity.UserInfoEntity
import uz.gxteam.variantapp.error.AppError
import uz.gxteam.variantapp.error.ErrorJoin
import uz.gxteam.variantapp.error.errorMain.ErrorApp
import uz.gxteam.variantapp.iterceptor.MySharedPreference
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.oneApplication.sendToken.SendToken
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser
import uz.gxteam.variantapp.models.webSocket.sendSocket.SendSocketData
import uz.gxteam.variantapp.network.appNetwork.AppHelper
import uz.gxteam.variantapp.utils.NetworkHelper
import uz.gxteam.variantapp.utils.VariantResourse
import javax.inject.Inject

class AppViewModel @Inject constructor(
    private val databaseHelper: DatabaseHelper,
    private val networkHelper: NetworkHelper,
    private val appHelper: AppHelper,
    private val mySharedPreference: MySharedPreference
):ViewModel() {
    fun createApplication():StateFlow<VariantResourse>{
        var createApplication = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.createApplication("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                    if (it.isSuccessful){
                        mySharedPreference.token_socet = it.body()?.token
                        createApplication.emit(VariantResourse.SuccessCreateApplication(it.body()))
                    }else{
                        var gson = Gson()
                        val throwable = Throwable(it.errorBody()?.string())
                        val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                        createApplication.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                    }
                }
            }else{
                createApplication.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return createApplication
    }

    fun getShared():MySharedPreference{
        return mySharedPreference
    }


    fun sendMessageServer(sendMessage: SendMessageUser):StateFlow<VariantResourse>{
        var messageRes = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.sendMessageChat(sendMessage,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                    if (it.isSuccessful){
                        messageRes.emit(VariantResourse.SuccessSendChat(it.body()))

                    }else{
                        var gson = Gson()
                        val throwable = Throwable(it.errorBody()?.string())
                        val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                        messageRes.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                    }
                }
            }else{
                messageRes.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return messageRes
    }


    fun getApplications():StateFlow<VariantResourse>{
        var applicationsApp = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.getApplications("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                    if (it.isSuccessful){
                        val applications = it.body()
                        applicationsApp.emit(VariantResourse.SuccessApplications(it.body()))

                    }else{
                        var gson = Gson()
                        val throwable = Throwable(it.errorBody()?.string())
                        val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                        applicationsApp.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                    }
                }
            }else{
                applicationsApp.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return applicationsApp
    }
    fun getAllMessage(reqMessage: ReqMessage):StateFlow<VariantResourse>{
        var getAllMessage = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.getAllMessage(reqMessage,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    .collect {
                        if (it.isSuccessful){
                            getAllMessage.emit(VariantResourse.SuccessGetAllMessage(it.body()))
                        }else{
                            var gson = Gson()
                            val throwable = Throwable(it.errorBody()?.string())
                            val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                            getAllMessage.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                        }
                    }
            }else{
                getAllMessage.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return getAllMessage
    }



    fun broadCatAuth(sendSocketData: SendSocketData):StateFlow<VariantResourse>{
        var broadCastAuthState = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.authBroadCasting(sendSocketData,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                    if (it.isSuccessful){
                        broadCastAuthState.emit(VariantResourse.SuccessBroadCastingAuth(it.body()))
                    }else{
                        var gson = Gson()
                        val throwable = Throwable(it.errorBody()?.string())
                        val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                        broadCastAuthState.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                    }
                }
            }else{
                broadCastAuthState.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return broadCastAuthState
    }






    fun broadCatAuthApp(sendSocketData: SendSocketData):StateFlow<VariantResourse>{
        var broadCastAuthState = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.authBroadCastingAppStatus(sendSocketData,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                    if (it.isSuccessful){
                        broadCastAuthState.emit(VariantResourse.SuccessBroadCastingAuthApp(it.body()))
                    }else{
                        var gson = Gson()
                        val throwable = Throwable(it.errorBody()?.string())
                        val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                        broadCastAuthState.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                    }
                }
            }else{
                broadCastAuthState.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return broadCastAuthState
    }






    fun getUserData():StateFlow<VariantResourse>{
        var userData = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
             appHelper.getUserData("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                 if (it.isSuccessful){
                     userData.emit(VariantResourse.SuccessUserData(it.body()))
                     var userInfo = UserInfoEntity(
                         it.body()?.birth_date.toString(),
                         it.body()?.branch_id?:0,
                         (it.body()?.created_at?:0).toString(),
                         it.body()?.document_id.toString(),
                         it.body()?.email.toString(),
                         it.body()?.id?:0,
                         it.body()?.name.toString(),
                         it.body()?.partner_id?:0,
                         it.body()?.passport_serial.toString(),
                         it.body()?.patronym.toString(),
                         it.body()?.period.toString(),
                         it.body()?.phone.toString(),
                         it.body()?.photo.toString(),
                         it.body()?.pinfl.toString(),
                         it.body()?.remember_token.toString(),
                         it.body()?.role_id?:0,
                         it.body()?.status.toString(),
                         it.body()?.surname.toString(),
                         it.body()?.two_factor_enabled?:0,
                         it.body()?.type.toString(),
                         (it.body()?.updated_at?:0).toString()
                     )
                     databaseHelper.saveUserInfo(userInfo)
                 }else{
                     var gson = Gson()
                     val throwable = Throwable(it.errorBody()?.string())
                     val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                     userData.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                 }
             }
            }else{
                userData.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return userData
    }

    fun getDatabase():DatabaseHelper{
        return databaseHelper
    }


    fun getAllApplicationsSuccess():StateFlow<VariantResourse>{
        var successsApplications = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.getAllApplicationsSuccess("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    .collect {
                       if (it.isSuccessful){
                           successsApplications.emit(VariantResourse.ApplicationsSuccess(it.body()))
                       }else{
                           var gson = Gson()
                           val throwable = Throwable(it.errorBody()?.string())
                           val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                           successsApplications.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                       }
                    }
            }else{
                successsApplications.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return successsApplications
    }

    fun getOneApplication(sendToken: SendToken):StateFlow<VariantResourse>{
        var application = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                appHelper.getOneApplication(sendToken,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                    if (it.isSuccessful){
                        application.emit(VariantResourse.SuccessGetApplication(it.body()))
                    }else{
                        var gson = Gson()
                        val throwable = Throwable(it.errorBody()?.string())
                        val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                        application.emit(VariantResourse.Error(AppError(error,it.code(), it.message(),true)))
                    }
                }
            }else{
                application.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return application
    }
}