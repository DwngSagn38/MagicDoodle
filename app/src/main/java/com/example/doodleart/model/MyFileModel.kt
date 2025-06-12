package com.example.doodleart.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_files")
data class MyFileModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val type: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)