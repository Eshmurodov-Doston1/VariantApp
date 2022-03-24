package uz.gxteam.variantapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.gxteam.variantapp.database.entity.AuthEntity
import uz.gxteam.variantapp.database.entity.DataApplicationEntity

@Dao
interface DataApplicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAuth(dataApplicationEntity:List<DataApplicationEntity> )

    @Query("DELETE FROM dataApplicationEntity")
    suspend fun deleteAuthTable()

    @Query("SELECT*FROM dataapplicationentity")
    fun getAuthEntity(): LiveData<List<DataApplicationEntity>>

    @Query("SELECT*FROM dataapplicationentity")
    fun getAuthEntityList(): List<DataApplicationEntity>
}