package com.example.prog3210_assignment2.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.prog3210_assignment2.databinding.ActivityMovieDetailsBinding
import com.example.prog3210_assignment2.viewmodel.MovieViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val movieViewModel: MovieViewModel by viewModels()

    companion object {
        private const val EXTRA_IMDB_ID = "extra_imdb_id"
        private const val EXTRA_FROM_FAVORITES = "extra_from_favorites"

        fun start(context: Context, imdbID: String, fromFavorites: Boolean = false) {
            val intent = Intent(context, MovieDetailsActivity::class.java)
            intent.putExtra(EXTRA_IMDB_ID, imdbID)
            intent.putExtra(EXTRA_FROM_FAVORITES, fromFavorites)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imdbID = intent.getStringExtra(EXTRA_IMDB_ID)
        val openedFromFavorites = intent.getBooleanExtra(EXTRA_FROM_FAVORITES, false)

        if (imdbID.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid movie id", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            movieViewModel.getMovieDetails(imdbID)
        }

        // If we opened this from Favorites, rename the button and change its behavior.
        if (openedFromFavorites) {
            binding.addToFavoritesButton.text = "Remove from Favorites"
        }

        // Observe and populate movie details
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

        // Single button that can either ADD or REMOVE depending on `openedFromFavorites`
        binding.addToFavoritesButton.setOnClickListener {
            val uid = Firebase.auth.currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!openedFromFavorites) {
                // 1) Normal behavior: add to favorites
                val description = binding.moviePlot.text.toString().trim()
                lifecycleScope.launch {
                    try {
                        Firebase.firestore
                            .collection("users")
                            .document(uid)
                            .collection("favorites")
                            .document(imdbID!!)
                            .set(mapOf("description" to description))
                            .await()

                        runOnUiThread {
                            Toast.makeText(
                                this@MovieDetailsActivity,
                                "Added to favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MovieDetailsActivity,
                                "Error saving favorite: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                // 2) Behavior if opened FROM favorites: remove from favorites
                AlertDialog.Builder(this)
                    .setTitle("Remove from Favorites")
                    .setMessage("Are you sure you want to remove this movie from favorites?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Actually remove from Firestore
                        lifecycleScope.launch {
                            try {
                                Firebase.firestore
                                    .collection("users")
                                    .document(uid)
                                    .collection("favorites")
                                    .document(imdbID!!)
                                    .delete()
                                    .await()

                                runOnUiThread {
                                    Toast.makeText(
                                        this@MovieDetailsActivity,
                                        "Removed from favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // Close this activity so user returns to favorites list
                                finish()
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@MovieDetailsActivity,
                                        "Error removing favorite: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }
}
