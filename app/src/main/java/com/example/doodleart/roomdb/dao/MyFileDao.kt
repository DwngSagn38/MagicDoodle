package com.example.doodleart.roomdb.dao

import androidx.room.*
import com.example.doodleart.model.MyFileModel

@Dao
interface MyFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: MyFileModel)

    @Update
    suspend fun updateFile(file: MyFileModel)

    @Delete
    suspend fun deleteFile(file: MyFileModel)

    @Query("SELECT * FROM my_files ORDER BY createdAt DESC")
    suspend fun getAllFiles(): List<MyFileModel>

    @Query("SELECT * FROM my_files WHERE id = :id")
    suspend fun getFileById(id: Int): MyFileModel?
}
