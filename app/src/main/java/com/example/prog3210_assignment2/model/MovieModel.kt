package com.example.prog3210_assignment2.model

data class MovieModel(
    val title: String = "",
    val year: String = "",
    val rated: String? = null,
    val released: String? = null,
    val runtime: String? = null,
    val genre: String? = null,
    val director: String? = null,
    val writer: String? = null,
    val actors: String? = null,
    val plot: String? = null,
    val language: String? = null,
    val country: String? = null,
    val awards: String? = null,
    val poster: String? = null,
    val ratings: List<Rating>? = emptyList(),
    val metascore: String? = null,
    val imdbRating: String? = null,
    val imdbVotes: String? = null,
    val imdbID: String = "",
    val type: String = "",
    val boxOffice: String? = null
)

data class Rating(
    val source: String,
    val value: String
)


/*
movie search
    {
      "Title": "Pirates of the Caribbean: The Curse of the Black Pearl",
      "Year": "2003",
      "imdbID": "tt0325980",
      "Type": "movie",
      "Poster": "https://m.media-amazon.com/images/M/MV5BNDhlMzEyNzItMTA5Mi00YWRhLThlNTktYTQyMTA0MDIyNDEyXkEyXkFqcGc@._V1_SX300.jpg"
    },


Specific movie
    {
  "Title": "Pirates of the Caribbean: The Curse of the Black Pearl",
  "Year": "2003",
  "Rated": "PG-13",
  "Released": "09 Jul 2003",
  "Runtime": "143 min",
  "Genre": "Action, Adventure, Fantasy",
  "Director": "Gore Verbinski",
  "Writer": "Ted Elliott, Terry Rossio, Stuart Beattie",
  "Actors": "Johnny Depp, Geoffrey Rush, Orlando Bloom",
  "Plot": "Blacksmith Will Turner teams up with eccentric pirate \"Captain\" Jack Sparrow to save Elizabeth Swann, the governor's daughter and his love, from Jack's former pirate allies, who are now undead.",
  "Language": "English",
  "Country": "United States",
  "Awards": "Nominated for 5 Oscars. 38 wins & 104 nominations total",
  "Poster": "https://m.media-amazon.com/images/M/MV5BNDhlMzEyNzItMTA5Mi00YWRhLThlNTktYTQyMTA0MDIyNDEyXkEyXkFqcGc@._V1_SX300.jpg",
  "Ratings": [
    {
      "Source": "Internet Movie Database",
      "Value": "8.1/10"
    },
    {
      "Source": "Rotten Tomatoes",
      "Value": "79%"
    },
    {
      "Source": "Metacritic",
      "Value": "63/100"
    }
  ],
  "Metascore": "63",
  "imdbRating": "8.1",
  "imdbVotes": "1,250,137",
  "imdbID": "tt0325980",
  "Type": "movie",
  "DVD": "N/A",
  "BoxOffice": "$305,413,918",
  "Production": "N/A",
  "Website": "N/A",
  "Response": "True"
}
 */