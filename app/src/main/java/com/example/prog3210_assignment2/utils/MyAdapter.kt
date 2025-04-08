package com.example.prog3210_assignment2.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prog3210_assignment2.R
import com.example.prog3210_assignment2.model.MovieSearchResult
import com.example.prog3210_assignment2.view.MyViewHolder

class MyAdapter(
    private val context: Context,
    private var items: List<MovieSearchResult>
) : RecyclerView.Adapter<MyViewHolder>() {

    var clickListener: MovieClickListener? = null

    fun setMovieClickListener(listener: MovieClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(itemView, clickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = items[position]
        holder.title.text = movie.Title
        holder.description.text = "Year: ${movie.Year}"
        Glide.with(context).load(movie.Poster).into(holder.imageView)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<MovieSearchResult>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getItems(): List<MovieSearchResult> = items
}
