package com.doodleart.paintcolor.drawart.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.doodleart.paintcolor.drawart.model.MyFileModel
import com.doodleart.paintcolor.drawart.roomdb.dao.MyFileDao

@Database(entities = [MyFileModel::class], version = 1)
abstract class DBHelper : RoomDatabase() {
    abstract fun fileDao(): MyFileDao

    companion object {
        @Volatile
        private var INSTANCE: DBHelper? = null

        fun getDatabase(context: Context): DBHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DBHelper::class.java,
                    "my_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}