package com.example.prog3210_assignment2.model

// This is the data class for the movie details and search results
// It contains all the fields returned by the OMDB API for a movie
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

// This is the data class for the ratings of the movie
// It contains the source and value of the rating
data class Rating(
    val Source: String,
    val Value: String
)

// This is the data class for the search results of the movie
// It contains the title, year, imdbID, type, and poster of the movie
data class MovieSearchResult(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String
)

// This is the data class for the search response of the movie
// It contains the list of search results, total results, and response status
data class SearchResponse(
    val Search: List<MovieSearchResult>,
    val totalResults: String,
    val Response: String
)
