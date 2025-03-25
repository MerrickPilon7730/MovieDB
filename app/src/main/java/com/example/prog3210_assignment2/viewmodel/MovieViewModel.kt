package com.example.prog3210_assignment2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prog3210_assignment2.model.MovieModel
import com.example.prog3210_assignment2.model.MovieSearchResult
import com.example.prog3210_assignment2.model.SearchResponse
import com.example.prog3210_assignment2.utils.ApiClient
import com.google.gson.Gson
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

    fun searchForMovie(movieTitle: String) {
        val urlString = "http://www.omdbapi.com/?apikey=$apiKey&s=$movieTitle&type=movie"
        apiClient.fetchMovieData(urlString, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Log or handle error
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

    fun getMovieDetails(imdbID: String) {
        val urlString = "http://www.omdbapi.com/?apikey=$apiKey&i=$imdbID&plot=full"
        apiClient.fetchMovieData(urlString, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error if needed
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val movie = gson.fromJson(json, MovieModel::class.java)
                    _movieData.postValue(movie)
                }
            }
        })
    }
}
