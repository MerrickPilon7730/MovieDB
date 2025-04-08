package com.example.prog3210_assignment2.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.prog3210_assignment2.databinding.ActivityMovieDetailsBinding
import com.example.prog3210_assignment2.viewmodel.MovieViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val movieViewModel: MovieViewModel by viewModels()

    companion object {
        private const val EXTRA_IMDB_ID = "extra_imdb_id"
        fun start(context: Context, imdbID: String) {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra(EXTRA_IMDB_ID, imdbID)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imdbID = intent.getStringExtra(EXTRA_IMDB_ID)
        if (imdbID.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid movie id", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            movieViewModel.getMovieDetails(imdbID)
        }

        movieViewModel.movieData.observe(this) { movie ->
            binding.movieTitle.text = movie.Title
            binding.movieYear.text = movie.Year
            binding.movieRated.text = "Rated: ${movie.Rated ?: "N/A"}"
            binding.movieReleased.text = "Released: ${movie.Released ?: "N/A"}"
            binding.movieRuntime.text = "Runtime: ${movie.Runtime ?: "N/A"}"
            binding.movieGenre.text = "Genre: ${movie.Genre ?: "N/A"}"
            binding.movieDirector.text = "Director: ${movie.Director ?: "N/A"}"
            binding.movieWriter.text = "Writer: ${movie.Writer ?: "N/A"}"
            binding.movieActors.text = "Actors: ${movie.Actors ?: "N/A"}"
            binding.moviePlot.text = "Plot: ${movie.Plot ?: "N/A"}"
            binding.movieAwards.text = "Awards: ${movie.Awards ?: "N/A"}"
            binding.movieBoxOffice.text = "Box Office: ${movie.BoxOffice ?: "N/A"}"

            Glide.with(this)
                .load(movie.Poster)
                .into(binding.moviePoster)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addToFavoritesButton.setOnClickListener {
            val uid = Firebase.auth.currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val imdbId     = intent.getStringExtra(EXTRA_IMDB_ID)!!
            val description = binding.moviePlot.text.toString().trim()

            // write to Firestore
            lifecycleScope.launch {
                try {
                    Firebase
                        .firestore
                        .collection("users")
                        .document(uid)
                        .collection("favorites")
                        .document(imdbId)
                        .set(mapOf("description" to description))
                        .await()

                    runOnUiThread {
                        Toast.makeText(this@MovieDetailsActivity,
                            "Added to favorites", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MovieDetailsActivity,
                            "Error saving favorite: ${e.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
