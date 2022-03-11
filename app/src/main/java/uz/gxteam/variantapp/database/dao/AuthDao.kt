package uz.gxteam.variantapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.gxteam.variantapp.database.entity.AuthEntity

@Dao
interface AuthDao {
    @Insert
    suspend fun saveAuth(authEntity:AuthEntity)

    @Query("DELETE FROM authentity")
    suspend fun deleteAuthTable()

    @Query("SELECT*FROM authentity")
    suspend fun getAuthEntity():AuthEntity
}