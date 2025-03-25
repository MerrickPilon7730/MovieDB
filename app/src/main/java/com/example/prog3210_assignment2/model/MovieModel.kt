package com.example.prog3210_assignment2.model

data class MovieModel(
    val Title: String = "",
    val Year: String = "",
    val Rated: String? = null,
    val Released: String? = null,
    val Runtime: String? = null,
    val Genre: String? = null,
    val Director: String? = null,
    val Writer: String? = null,
    val Actors: String? = null,
    val Plot: String? = null,
    val Language: String? = null,
    val Country: String? = null,
    val Awards: String? = null,
    val Poster: String? = null,
    val Ratings: List<Rating>? = emptyList(),
    val Metascore: String? = null,
    val imdbRating: String? = null,
    val imdbVotes: String? = null,
    val imdbID: String = "",
    val Type: String = "",
    val DVD: String? = null,
    val BoxOffice: String? = null,
    val Production: String? = null,
    val Website: String? = null,
    val Response: String? = null
)

data class Rating(
    val Source: String,
    val Value: String
)

data class MovieSearchResult(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)

data class SearchResponse(
    val Search: List<MovieSearchResult>,
    val totalResults: String,
    val Response: String
)
