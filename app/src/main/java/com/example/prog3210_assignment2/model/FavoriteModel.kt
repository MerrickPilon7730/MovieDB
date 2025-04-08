package com.example.prog3210_assignment2.model

import androidx.room.Entity

// This is the data class for the favorite movies table in the database
// It contains the userId, imdbId, and description of the movie
@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "imdbId"]
)
data class FavoriteModel(
    val userId: String,
    val imdbId: String,
    var description: String
)