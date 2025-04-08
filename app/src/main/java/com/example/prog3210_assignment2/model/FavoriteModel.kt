package com.example.prog3210_assignment2.model

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "imdbId"]
)
data class FavoriteModel(
    val userId: String,
    val imdbId: String,
    var description: String
)