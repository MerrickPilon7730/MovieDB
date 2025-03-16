package com.example.prog3210_assignment2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prog3210_assignment2.model.MovieModel

class MovieViewModel : ViewModel(){

    private val movieData = MutableLiveData<MovieModel>();
    val movieModel = MovieModel();

}


