package uz.gxteam.variantapp.viewModels.appViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.gxteam.variantapp.database.DatabaseHelper
import uz.gxteam.variantapp.database.entity.ApplicationsEntity
import uz.gxteam.variantapp.error.AppError
import uz.gxteam.variantapp.error.errorMain.ErrorApp
import uz.gxteam.variantapp.iterceptor.MySharedPreference
import uz.gxteam.variantapp.models.chat.messages.SendMessage
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser
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
                        var applicationsEntity = ApplicationsEntity(
                            current_page = applications?.current_page?:0,
                            data = applications?.data.toString(),
                            first_page_url = applications?.first_page_url.toString(),
                            from = applications?.from?:0,
                            last_page = applications?.last_page?:0,
                            last_page_url = applications?.last_page_url.toString(),
                            links = applications?.links.toString(),
                            next_page_url = applications?.next_page_url.toString(),
                            path = applications?.path.toString(),
                            per_page = applications?.per_page?:0,
                            prev_page_url = applications?.prev_page_url.toString(),
                            to = applications?.to?:0,
                            total = applications?.total?:0)
                        if (databaseHelper.getAllApplicationsList().isEmpty()){
                            databaseHelper.saveApplications(applicationsEntity)
                        }else{
                            databaseHelper.deleteApplicationsTable()
                            databaseHelper.saveApplications(applicationsEntity)
                        }
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



}