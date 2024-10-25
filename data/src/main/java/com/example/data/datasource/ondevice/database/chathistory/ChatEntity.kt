package com.example.data.datasource.ondevice.database.chathistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_table")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val message: String,
    @ColumnInfo(name = "image_name")
    val imageName: String,
    @ColumnInfo(name = "image_path_name")
    val imagePathName: String,
    @ColumnInfo
    val sender: String
)