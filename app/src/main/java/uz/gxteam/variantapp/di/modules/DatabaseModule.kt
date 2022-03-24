package uz.gxteam.variantapp.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import uz.gxteam.variantapp.database.AppDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context):AppDatabase{
        return Room.databaseBuilder(context,AppDatabase::class.java,"variant.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthDao(appDatabase: AppDatabase) = appDatabase.authDao()

    @Provides
    @Singleton
    fun provideApplicationDao(appDatabase: AppDatabase) = appDatabase.applicationDao()

    @Provides
    @Singleton
    fun provideUserInfoDao(appDatabase: AppDatabase) = appDatabase.userInfoDao()

    @Provides
    @Singleton
    fun provideDataApplicationDao(appDatabase: AppDatabase) = appDatabase.dataApplicationDao()

}