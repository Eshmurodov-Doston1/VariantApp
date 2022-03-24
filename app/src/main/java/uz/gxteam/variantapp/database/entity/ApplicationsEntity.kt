package uz.gxteam.variantapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ApplicationsEntity(
    @PrimaryKey(autoGenerate =true)
    var id:Long = 0,
    val current_page: Int,
    val `data`: String,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val links:String,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: String,
    val to: Int,
    val total: Int
)