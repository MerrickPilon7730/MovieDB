package com.example.prog3210_assignment2.viewmodel

import androidx.room.*
import com.example.prog3210_assignment2.model.FavoriteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDBOperation {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(fav: FavoriteModel)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavorites(userId: String): Flow<List<FavoriteModel>>

    @Update
    suspend fun update(fav: FavoriteModel)
}