package com.example.data.datasource.ondevice.database.chathistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChatEntity::class], version = 1, exportSchema = false)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null
        fun getInstance(context: Context): ChatDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context,
                    klass = ChatDatabase::class.java,
                    name = "chat_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}