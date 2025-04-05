package com.example.prog3210_assignment2.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.prog3210_assignment2.R
import com.example.prog3210_assignment2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) ViewPager2 adapter
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(pos: Int): Fragment =
                if (pos == 0) SearchFragment() else FavoritesFragment()
        }

        // 2) swipe → bottomNav
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNav.menu.getItem(position).isChecked = true
                }
            }
        )

        // 3) bottomNav → swipe
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search     -> binding.viewPager.currentItem = 0
                R.id.nav_favourites -> binding.viewPager.currentItem = 1
            }
            true
        }
    }
}
