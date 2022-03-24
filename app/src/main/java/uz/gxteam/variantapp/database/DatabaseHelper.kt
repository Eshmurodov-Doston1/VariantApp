package uz.gxteam.variantapp.database

import androidx.lifecycle.LiveData
import uz.gxteam.variantapp.database.dao.ApplicationsDao
import uz.gxteam.variantapp.database.dao.AuthDao
import uz.gxteam.variantapp.database.dao.DataApplicationDao
import uz.gxteam.variantapp.database.dao.UserInfoDao
import uz.gxteam.variantapp.database.entity.ApplicationsEntity
import uz.gxteam.variantapp.database.entity.DataApplicationEntity
import uz.gxteam.variantapp.database.entity.UserInfoEntity
import uz.gxteam.variantapp.models.login.ResLogin
import javax.inject.Inject

class DatabaseHelper @Inject constructor(
    private val authDao: AuthDao,
    private val getDatabase:AppDatabase,
    private val applicationsDao: ApplicationsDao,
    private val userInfoDao: UserInfoDao,
    private val dataApplicationDao: DataApplicationDao
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

    suspend fun saveUserInfo(userInfoEntity: UserInfoEntity){
        userInfoDao.saveUserInfo(userInfoEntity)
    }

    suspend fun deleteUserInfoTable(){
        userInfoDao.deleteTableUser()
    }

    fun getUserInfo():UserInfoEntity{
        return userInfoDao.getUserInfo()
    }
    fun getUserInfoAll():List<UserInfoEntity>{
        return userInfoDao.getUserInfoAll()
    }
    suspend fun saveDataApplicationEntity(list: List<DataApplicationEntity>){
        dataApplicationDao.saveAuth(list)
    }
    suspend fun deleteDataApplicationEntity(){
        dataApplicationDao.deleteAuthTable()
    }

    fun getAllDataApplicationsEntity():LiveData<List<DataApplicationEntity>>{
        return dataApplicationDao.getAuthEntity()
    }


    fun getAllDataApplicationsEntityList():List<DataApplicationEntity>{
        return dataApplicationDao.getAuthEntityList()
    }

}