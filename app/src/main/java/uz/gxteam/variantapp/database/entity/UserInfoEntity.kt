package uz.gxteam.variantapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfoEntity (
    val birth_date: String,
    val branch_id: Long,
    val created_at: String,
    val document_id: String,
    val email: String,
    @PrimaryKey
    val id: Int,
    val name: String,
    val partner_id: Int,
    val passport_serial: String,
    val patronym: String,
    val period:String,
    val phone: String,
    val photo: String,
    val pinfl: String,
    val remember_token: String,
    val role_id: Int,
    val status: String,
    val surname: String,
    val two_factor_enabled: Int,
    val type: String,
    val updated_at: String
        )