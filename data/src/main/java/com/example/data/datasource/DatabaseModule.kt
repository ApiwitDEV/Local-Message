package com.example.data.datasource

import android.content.Context
import androidx.room.Room
import com.example.data.datasource.ondevice.database.chathistory.ChatDao
import com.example.data.datasource.ondevice.database.chathistory.ChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideChatDao(database: ChatDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): ChatDatabase {
        return Room.databaseBuilder(
            appContext,
            ChatDatabase::class.java,
            "chat_database"
        ).build()
    }

}