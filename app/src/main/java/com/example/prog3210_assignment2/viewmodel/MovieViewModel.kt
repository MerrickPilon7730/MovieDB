package com.example.prog3210_assignment2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prog3210_assignment2.model.MovieModel

class MovieViewModel : ViewModel(){

    private val _movieData = MutableLiveData<MovieModel>()
    val movieData: LiveData<MovieModel> get() = _movieData

    val movieModel = MovieModel()

    fun setMovieData(movie: MovieModel){
        _movieData.value = movie
    }

    fun searchForMovie(movieTitle: String) {
        val urlString = "http://www.omdbapi.com/?apikey=7c3e78d1&s=$movieTitle&type=movie"
    }
}


