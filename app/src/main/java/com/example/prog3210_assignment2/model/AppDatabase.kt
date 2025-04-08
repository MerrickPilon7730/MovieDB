package com.example.prog3210_assignment2.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.prog3210_assignment2.viewmodel.FavoriteDBOperation

@Database(entities = [FavoriteModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDBOperation(): FavoriteDBOperation

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(ctx: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build().also { INSTANCE = it }
            }
    }
}
