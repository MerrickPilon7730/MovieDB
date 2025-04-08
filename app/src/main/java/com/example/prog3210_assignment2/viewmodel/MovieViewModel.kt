package com.example.prog3210_assignment2.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prog3210_assignment2.model.MovieModel
import com.example.prog3210_assignment2.model.MovieSearchResult
import com.example.prog3210_assignment2.model.SearchResponse
import com.example.prog3210_assignment2.utils.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MovieViewModel : ViewModel() {

    private val _movieData = MutableLiveData<MovieModel>()
    val movieData: LiveData<MovieModel> get() = _movieData

    private val _searchResults = MutableLiveData<List<MovieSearchResult>>()
    val searchResults: LiveData<List<MovieSearchResult>> get() = _searchResults

    private val apiClient = ApiClient()
    private val gson = Gson()
    private val apiKey = "97d44d3e"

    // Seach for movies 
    fun searchForMovie(movieTitle: String) {
        val encodedTitle = java.net.URLEncoder.encode(movieTitle, "UTF-8")
        val urlString = "https://www.omdbapi.com/?apikey=$apiKey&s=$encodedTitle&type=movie"
        apiClient.fetchMovieData(urlString, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val searchResponse = gson.fromJson(json, SearchResponse::class.java)
                    if (searchResponse.Response == "True") {
                        _searchResults.postValue(searchResponse.Search)
                    } else {
                        _searchResults.postValue(emptyList())
                    }
                }
            }
        })
    }

    // get movie deatils for particular movie 
    fun getMovieDetails(imdbID: String) {
        val urlString = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID&plot=full"
        apiClient.fetchMovieData(urlString, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    Log.d("MovieDetails", json)
                    try {
                        val movie = gson.fromJson(json, MovieModel::class.java)
                        _movieData.postValue(movie)
                    } catch (e: Exception) {
                        Log.e("MovieDetails", "Parsing error", e)
                    }
                }
            }
        })
    }

    // fect movies as search result using the imdb id
    // using suspend function to fetch movie details in a coroutine
    // This function is called from the UI thread and will suspend until the result is available.
    suspend fun fetchMovieAsSearchResult(imdbId: String): MovieSearchResult? =
        withContext(Dispatchers.IO) {
            val url = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbId"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()?.let { json ->
                            val movieDetail = gson.fromJson(json, MovieModel::class.java)
                            return@withContext MovieSearchResult(
                                Title = movieDetail.Title,
                                Year = movieDetail.Year,
                                imdbID = movieDetail.imdbID,
                                Type = movieDetail.Type,
                                Poster = movieDetail.Poster ?: ""
                            )
                        }
                    }
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
