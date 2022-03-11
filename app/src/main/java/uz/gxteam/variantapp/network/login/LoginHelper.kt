package uz.gxteam.variantapp.network.login

import kotlinx.coroutines.flow.flow
import uz.gxteam.variantapp.models.login.ReqLogin
import javax.inject.Inject

class LoginHelper @Inject constructor(
    private val loginService: LoginService
    ) {
    suspend fun loginApp(reqLogin: ReqLogin) = flow { emit(loginService.login(reqLogin)) }

    suspend fun logOut(token:String) = flow { emit(loginService.logOut(token)) }
}