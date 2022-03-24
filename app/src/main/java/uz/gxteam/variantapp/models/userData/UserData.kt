package uz.gxteam.variantapp.models.userData

data class UserData(
    val birth_date: String,
    val branch_id: Long,
    val created_at: Any,
    val document_id: Any,
    val email: Any,
    val id: Int,
    val name: String,
    val partner_id: Int,
    val passport_serial: String,
    val patronym: String,
    val period: List<Int>,
    val phone: String,
    val photo: Any,
    val pinfl: String,
    val remember_token: Any,
    val role_id: Int,
    val status: String,
    val surname: String,
    val two_factor_enabled: Int,
    val type: String,
    val updated_at: Any
)