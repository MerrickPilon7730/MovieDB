package com.example.prog3210_assignment2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog3210_assignment2.databinding.FragmentSearchBinding
import com.example.prog3210_assignment2.model.MovieSearchResult
import com.example.prog3210_assignment2.utils.MovieClickListener
import com.example.prog3210_assignment2.utils.MyAdapter
import com.example.prog3210_assignment2.viewmodel.MovieViewModel

class SearchFragment : Fragment(), MovieClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val movieViewModel: MovieViewModel by activityViewModels()
    private lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MyAdapter(requireContext(), emptyList()).apply {
            setMovieClickListener(this@SearchFragment)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchFragment.adapter
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doSearch(); true
            } else false
        }
        binding.searchButton.setOnClickListener { doSearch() }

        movieViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.updateList(results)
        }
    }

    private fun doSearch() {
        val q = binding.searchEditText.text.toString().trim()
        if (q.isNotEmpty()) movieViewModel.searchForMovie(q)
        else Toast.makeText(requireContext(), "Enter a movie title", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?, pos: Int) {
        val movie: MovieSearchResult? = movieViewModel.searchResults.value?.get(pos)
        if (movie != null && movie.imdbID.isNotEmpty()) {
            MovieDetailsActivity.start(requireContext(), movie.imdbID)
        } else {
            Toast.makeText(requireContext(), "Invalid movie ID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
