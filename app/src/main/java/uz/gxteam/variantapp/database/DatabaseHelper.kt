package uz.gxteam.variantapp.database

import androidx.lifecycle.LiveData
import uz.gxteam.variantapp.database.dao.ApplicationsDao
import uz.gxteam.variantapp.database.dao.AuthDao
import uz.gxteam.variantapp.database.entity.ApplicationsEntity
import uz.gxteam.variantapp.models.login.ResLogin
import javax.inject.Inject

class DatabaseHelper @Inject constructor(
    private val authDao: AuthDao,
    private val getDatabase:AppDatabase,
    private val applicationsDao: ApplicationsDao
) {
    suspend fun clearDatabase(){
        getDatabase.clearAllTables()
    }

    suspend fun saveApplications(applicationsEntity: ApplicationsEntity){
        applicationsDao.saveApplications(applicationsEntity)
    }

    suspend fun deleteApplicationsTable(){
        applicationsDao.deleteTableApplications()
    }
    fun getAllApplications():LiveData<ApplicationsEntity>{
        return applicationsDao.getAllApplications()
    }
    suspend fun getAllApplicationsList():List<ApplicationsEntity>{
        return applicationsDao.getAllApplicationsList()
    }
}