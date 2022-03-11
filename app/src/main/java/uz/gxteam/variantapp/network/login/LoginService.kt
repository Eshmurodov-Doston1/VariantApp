package uz.gxteam.variantapp.network.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import uz.gxteam.variantapp.models.logOut.LogOut
import uz.gxteam.variantapp.models.login.ReqLogin
import uz.gxteam.variantapp.models.login.ResLogin

interface LoginService {
    @POST("/api/login")
    suspend fun login(@Body reqLogin: ReqLogin):Response<ResLogin>

    @POST("/api/logout")
    suspend fun logOut(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<LogOut>


}