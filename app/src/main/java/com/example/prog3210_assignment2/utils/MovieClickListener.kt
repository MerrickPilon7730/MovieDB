package com.example.prog3210_assignment2.utils

import android.view.View

// Interface to handle click events on movie items in a RecyclerView
// This interface defines a single method onClick that takes a View and an Int as parameters
interface MovieClickListener {
    fun onClick(v: View?, pos: Int)
}
