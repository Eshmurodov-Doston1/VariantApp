package uz.gxteam.variantapp.viewModels.loginViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.gxteam.variantapp.database.DatabaseHelper
import uz.gxteam.variantapp.error.AppError
import uz.gxteam.variantapp.error.errorMain.ErrorApp
import uz.gxteam.variantapp.iterceptor.MySharedPreference
import uz.gxteam.variantapp.models.login.ReqLogin
import uz.gxteam.variantapp.network.login.LoginHelper
import uz.gxteam.variantapp.utils.NetworkHelper
import uz.gxteam.variantapp.utils.VariantResourse
import javax.inject.Inject


class LoginViewModel @Inject constructor(
    private val loginHelper:LoginHelper,
    private val networkHelper: NetworkHelper,
    private val mySharedPreference: MySharedPreference,
    private val databaseHelper: DatabaseHelper):ViewModel() {
   fun login(reqLogin: ReqLogin):StateFlow<VariantResourse>{
       Log.e("Req", reqLogin.toString() )
       var login = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
       viewModelScope.launch {
           if (networkHelper.isNetworkConnected()){
               loginHelper.loginApp(reqLogin).collect {
                   if (it.isSuccessful){
                       mySharedPreference.accessToken = it.body()?.access_token
                       mySharedPreference.refreshToken = it.body()?.refresh_token
                       mySharedPreference.tokenType = it.body()?.token_type
                       login.emit(VariantResourse.SuccessLogin(it.body()))

                   }else{
                       var gson = Gson()
                       val throwable = Throwable(it.errorBody()?.string())
                       val error = gson.fromJson(throwable.message, ErrorApp::class.java)
                       login.emit(
                          VariantResourse.Error(AppError(error,it.code(), it.message(),true))
                       )
                   }
               }
           }else{
               login.emit(VariantResourse.Error(AppError(internetConnection = false)))
           }
       }
       return login
   }

    fun getShared():MySharedPreference{
        return mySharedPreference
    }


    fun logOut():StateFlow<VariantResourse>{
        var logOut = MutableStateFlow<VariantResourse>(VariantResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                Log.e("Tokens", "${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                loginHelper.logOut("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}").collect {
                    if (it.isSuccessful){
                        logOut.emit(VariantResourse.SuccessLogOut(it.body()))
                        databaseHelper.clearDatabase()
                        mySharedPreference.clear()
                    }else{
                        val error = Gson().fromJson(it.errorBody()?.string().toString(), ErrorApp::class.java)
                        logOut.emit(VariantResourse.Error(AppError(error = error,it.code(),it.message(),true)))
                    }
                }
            }else{
                logOut.emit(VariantResourse.Error(AppError(internetConnection = false)))
            }
        }
        return logOut
    }
}