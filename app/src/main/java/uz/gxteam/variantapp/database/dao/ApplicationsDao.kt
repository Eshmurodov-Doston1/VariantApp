package uz.gxteam.variantapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.gxteam.variantapp.database.entity.ApplicationsEntity

@Dao
interface ApplicationsDao {
    @Insert
    suspend fun saveApplications(applications: ApplicationsEntity)

    @Query("DELETE FROM applicationsentity")
    suspend fun deleteTableApplications()

    @Query("SELECT*FROM applicationsentity")
    fun getAllApplications():LiveData<ApplicationsEntity>

    @Query("SELECT*FROM applicationsentity")
    fun getAllApplicationsList():List<ApplicationsEntity>

}