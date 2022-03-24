package uz.gxteam.variantapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.gxteam.variantapp.models.getApplications.StatusName
@Entity
data class DataApplicationEntity(
    val client: String,
    val created_at: String,
    @PrimaryKey
    val id: Int,
    val status: Int,
    val status_name: String,
    val token: String
)
