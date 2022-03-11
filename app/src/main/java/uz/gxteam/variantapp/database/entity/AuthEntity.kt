package uz.gxteam.variantapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AuthEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Long,
    var number:String,
    var password:String
)