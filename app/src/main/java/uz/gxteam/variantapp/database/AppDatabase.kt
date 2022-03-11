package uz.gxteam.variantapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gxteam.variantapp.database.dao.ApplicationsDao
import uz.gxteam.variantapp.database.dao.AuthDao
import uz.gxteam.variantapp.database.entity.ApplicationsEntity
import uz.gxteam.variantapp.database.entity.AuthEntity

@Database(entities = [AuthEntity::class,ApplicationsEntity::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun authDao():AuthDao
    abstract fun applicationDao():ApplicationsDao
}