package com.example.prog3210_assignment2.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding

    // Use the built-in delegate directly.
    private val movieViewModel: MovieViewModel by viewModels()

    companion object {
        private const val EXTRA_IMDB_ID = "extra_imdb_id"
        private const val EXTRA_FROM_FAVORITES = "extra_from_favorites"

        fun start(context: Context, imdbID: String, fromFavorites: Boolean = false) {
            Intent(context, MovieDetailsActivity::class.java).apply {
                putExtra(EXTRA_IMDB_ID, imdbID)
                putExtra(EXTRA_FROM_FAVORITES, fromFavorites)
                context.startActivity(this)
            }
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

        // Adjust UI for favorites mode.
        if (openedFromFavorites) {
            binding.favoriteActionButton.text = "Remove from Favorites"
            binding.updateDescriptionButton.visibility = android.view.View.VISIBLE
            // Make description non-editable initially.
            binding.moviePlot.isFocusable = false
            binding.moviePlot.isClickable = false
        } else {
            binding.updateDescriptionButton.visibility = android.view.View.GONE
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
            binding.moviePlot.setText(movie.Plot ?: "N/A")
            binding.movieAwards.text = "Awards: ${movie.Awards ?: "N/A"}"
            binding.movieBoxOffice.text = "Box Office: ${movie.BoxOffice ?: "N/A"}"

            Glide.with(this)
                .load(movie.Poster)
                .into(binding.moviePoster)

            // If opened from favorites, override the description with the one from Firestore.
            if (openedFromFavorites) {
                Firebase.auth.currentUser?.uid?.let { uid ->
                    Firebase.firestore.collection("users")
                        .document(uid)
                        .collection("favorites")
                        .document(imdbID)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val updatedDesc = document.getString("description")
                                if (!updatedDesc.isNullOrEmpty()) {
                                    binding.moviePlot.setText(updatedDesc)
                                }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error fetching updated description", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }


        // If in favorites mode, fetch updated description from Firestore.
        if (openedFromFavorites) {
            Firebase.auth.currentUser?.uid?.let { uid ->
                Firebase.firestore.collection("users")
                    .document(uid)
                    .collection("favorites")
                    .document(imdbID)
                    .get()
                    .addOnSuccessListener { document ->
                        document?.takeIf { it.exists() }?.getString("description")
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { binding.moviePlot.setText(it) }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Error fetching updated description",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        binding.backButton.setOnClickListener { finish() }

        // Update description functionality for favorites mode.
        if (openedFromFavorites) {
            binding.updateDescriptionButton.setOnClickListener {
                val currentDesc = binding.moviePlot.text.toString()
                val input = EditText(this).apply {
                    setText(currentDesc)
                    setImeActionLabel("Save", EditorInfo.IME_ACTION_DONE)
                    imeOptions = EditorInfo.IME_ACTION_DONE
                }
                AlertDialog.Builder(this)
                    .setTitle("Update Description")
                    .setView(input)
                    .setPositiveButton("Update") { _, _ ->
                        val newDescription = input.text.toString().trim()
                        val uid = Firebase.auth.currentUser?.uid
                        if (uid == null) {
                            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        lifecycleScope.launch {
                            try {
                                Firebase.firestore
                                    .collection("users")
                                    .document(uid)
                                    .collection("favorites")
                                    .document(imdbID)
                                    .update("description", newDescription)
                                    .await()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@MovieDetailsActivity,
                                        "Description updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.moviePlot.setText(newDescription)
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@MovieDetailsActivity,
                                        "Error updating description: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        // Favorite action button: add or remove.
        binding.favoriteActionButton.setOnClickListener {
            val uid = Firebase.auth.currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!openedFromFavorites) {
                // Add to favorites.
                val description = binding.moviePlot.text.toString().trim()
                lifecycleScope.launch {
                    try {
                        Firebase.firestore
                            .collection("users")
                            .document(uid)
                            .collection("favorites")
                            .document(imdbID)
                            .set(mapOf("description" to description))
                            .await()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MovieDetailsActivity,
                                "Added to favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MovieDetailsActivity,
                                "Error saving favorite: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                // Remove from favorites.
                AlertDialog.Builder(this)
                    .setTitle("Remove from Favorites")
                    .setMessage("Are you sure you want to remove this movie from favorites?")
                    .setPositiveButton("Yes") { _, _ ->
                        lifecycleScope.launch {
                            try {
                                Firebase.firestore
                                    .collection("users")
                                    .document(uid)
                                    .collection("favorites")
                                    .document(imdbID)
                                    .delete()
                                    .await()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@MovieDetailsActivity,
                                        "Removed from favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                finish()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
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
