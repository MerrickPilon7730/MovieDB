package com.example.prog3210_assignment2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog3210_assignment2.databinding.FragmentFavouriteBinding
import com.example.prog3210_assignment2.model.MovieSearchResult
import com.example.prog3210_assignment2.utils.MovieClickListener
import com.example.prog3210_assignment2.utils.MyAdapter
import com.example.prog3210_assignment2.viewmodel.MovieViewModel
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

class FavoritesFragment : Fragment(), MovieClickListener {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    // Add the activityViewModels delegate to share the same instance.
    private val movieViewModel: MovieViewModel by activityViewModels()

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
        adapter = MyAdapter(requireContext(), emptyList()).apply {
            setMovieClickListener(this@FavoritesFragment)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.loadingIndicator.visibility = View.VISIBLE
        loadFavoriteMovies()
    }

    override fun onResume() {
        super.onResume()
        binding.loadingIndicator.visibility = View.VISIBLE
        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            binding.loadingIndicator.visibility = View.GONE
            return
        }

        Firebase.firestore.collection("users")
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
                        movieViewModel.fetchMovieAsSearchResult(imdbId)
                            ?.let { favoriteMovies.add(it) }
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

    override fun onClick(v: View?, pos: Int) {
        val currentList = adapter.getItems()
        if (pos in currentList.indices) {
            val movie = currentList[pos]
            if (movie.imdbID.isNotEmpty()) {
                MovieDetailsActivity.start(requireContext(), movie.imdbID, fromFavorites = true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
