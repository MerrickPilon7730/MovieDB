package com.example.prog3210_assignment2.utils

import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request

// Api Client class to handle network requests
// This class uses OkHttp to make network calls to fetch movie data from a given URL
class ApiClient {
    private val client = OkHttpClient()

    fun fetchMovieData(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(callback)
    }
}
