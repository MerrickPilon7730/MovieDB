package com.example.prog3210_assignment2.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog3210_assignment2.databinding.ActivityMainBinding
import com.example.prog3210_assignment2.model.MovieSearchResult
import com.example.prog3210_assignment2.utils.MovieClickListener
import com.example.prog3210_assignment2.utils.MyAdapter
import com.example.prog3210_assignment2.viewmodel.MovieViewModel

class MainActivity : AppCompatActivity(), MovieClickListener {

    private lateinit var binding: ActivityMainBinding
    private val movieViewModel: MovieViewModel by viewModels()
    private lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize adapter with an empty list and set the click listener
        myAdapter = MyAdapter(this, listOf())
        myAdapter.setMovieClickListener(this)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = myAdapter

        // Setup the search button click listener
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                movieViewModel.searchForMovie(query)
            } else {
                Toast.makeText(this, "Enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the search results and update the adapter
        movieViewModel.searchResults.observe(this) { results ->
            myAdapter.updateList(results)
        }
    }

    // MovieClickListener callback: when an item is clicked, start details activity.
    override fun onClick(v: View?, pos: Int) {
        val movie: MovieSearchResult? = movieViewModel.searchResults.value?.get(pos)
        movie?.let {
            // Make sure imdbID is valid
            if (it.imdbID.isNotEmpty()) {
                MovieDetailsActivity.start(this, it.imdbID)
            } else {
                Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
