package com.portal.browserbar.di
import android.content.Context
import androidx.room.Room
import com.portal.browserbar.data.local.AppDao
import com.portal.browserbar.data.local.AppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.portal.browserbar")
class DataModule {

    @Single
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "portal_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Single
    fun provideAppDao(database: AppDatabase): AppDao {
        return database.appDao()
    }
}
