package com.example.prog3210_assignment2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog3210_assignment2.databinding.FragmentFavouriteBinding
import com.example.prog3210_assignment2.model.MovieModel
import com.example.prog3210_assignment2.model.MovieSearchResult
import com.example.prog3210_assignment2.utils.MyAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyAdapter

    private val apiKey = "97d44d3e"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MyAdapter(requireContext(), emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
        }

        binding.loadingIndicator.visibility = View.VISIBLE

        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            binding.loadingIndicator.visibility = View.GONE
            return
        }

        Firebase.firestore
            .collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { snapshot ->
                val imdbIds = snapshot.documents.map { it.id }

                val handler = CoroutineExceptionHandler { _, exception ->
                    exception.printStackTrace()
                    binding.loadingIndicator.visibility = View.GONE
                }

                viewLifecycleOwner.lifecycleScope.launch(handler) {
                    val favoriteMovies = mutableListOf<MovieSearchResult>()
                    for (imdbId in imdbIds) {
                        val movieSearchResult = fetchMovieAsSearchResult(imdbId)
                        movieSearchResult?.let { favoriteMovies.add(it) }
                    }
                    withContext(Dispatchers.Main) {
                        adapter.updateList(favoriteMovies)
                        binding.loadingIndicator.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                binding.loadingIndicator.visibility = View.GONE
            }
    }

    private suspend fun fetchMovieAsSearchResult(imdbId: String): MovieSearchResult? {
        return withContext(Dispatchers.IO) {
            val url = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbId"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { json ->
                        val movieDetail = Gson().fromJson(json, MovieModel::class.java)
                        MovieSearchResult(
                            Title = movieDetail.Title,
                            Year = movieDetail.Year,
                            imdbID = movieDetail.imdbID,
                            Type = movieDetail.Type,
                            Poster = movieDetail.Poster ?: ""
                        )
                    }
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
