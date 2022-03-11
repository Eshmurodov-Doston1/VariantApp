package uz.gxteam.variantapp.models.login

import java.lang.Error

data class ResLogin(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val token_type: String,
)