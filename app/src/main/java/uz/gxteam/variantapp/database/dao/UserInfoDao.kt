package uz.gxteam.variantapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.gxteam.variantapp.database.entity.UserInfoEntity

@Dao
interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserInfo(userInfoEntity: UserInfoEntity)

    @Query("DELETE FROM userinfoentity")
    suspend fun deleteTableUser()

    @Query("SELECT*FROM userinfoentity")
    fun getUserInfo():UserInfoEntity

    @Query("SELECT*FROM userinfoentity")
    fun getUserInfoAll():List<UserInfoEntity>

}