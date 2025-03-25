package com.example.prog3210_assignment2.view

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prog3210_assignment2.R
import com.example.prog3210_assignment2.utils.MovieClickListener

class MyViewHolder(itemView: View, private val clickListener: MovieClickListener?) :
    RecyclerView.ViewHolder(itemView) {

    val imageView: ImageView = itemView.findViewById(R.id.imageview)
    val title: TextView = itemView.findViewById(R.id.title_txt)
    val description: TextView = itemView.findViewById(R.id.description_text)

    init {
        itemView.setOnClickListener {
            Log.d("tag", "onViewHolder Click")
            clickListener?.onClick(itemView, adapterPosition)
        }
    }
}
