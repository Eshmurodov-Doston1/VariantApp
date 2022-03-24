package uz.gxteam.variantapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gxteam.variantapp.database.dao.ApplicationsDao
import uz.gxteam.variantapp.database.dao.AuthDao
import uz.gxteam.variantapp.database.dao.DataApplicationDao
import uz.gxteam.variantapp.database.dao.UserInfoDao
import uz.gxteam.variantapp.database.entity.ApplicationsEntity
import uz.gxteam.variantapp.database.entity.AuthEntity
import uz.gxteam.variantapp.database.entity.DataApplicationEntity
import uz.gxteam.variantapp.database.entity.UserInfoEntity

@Database(entities = [AuthEntity::class,ApplicationsEntity::class,UserInfoEntity::class,DataApplicationEntity::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun authDao():AuthDao
    abstract fun applicationDao():ApplicationsDao
    abstract fun userInfoDao():UserInfoDao
    abstract fun dataApplicationDao():DataApplicationDao
}