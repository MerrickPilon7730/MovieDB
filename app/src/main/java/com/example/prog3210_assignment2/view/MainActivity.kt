package com.example.prog3210_assignment2.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.prog3210_assignment2.R
import com.example.prog3210_assignment2.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the ViewPager2 with a FragmentStateAdapter
        // The ViewPager2 will hold two fragments: SearchFragment and FavoritesFragment
        // The FragmentStateAdapter will manage the lifecycle of the fragments
        // and ensure that only the currently visible fragment is kept in memory
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(pos: Int): Fragment =
                if (pos == 0) SearchFragment() else FavoritesFragment()
        }

        // Set up the ViewPager2 to work with the BottomNavigationView
        // When the user swipes between fragments, the BottomNavigationView will update to reflect the current fragment
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.bottomNav.menu.getItem(position).isChecked = true
                }
            }
        )

        // Set up the BottomNavigationView to work with the ViewPager2
        // When the user selects an item in the BottomNavigationView, the ViewPager2 will update to show the corresponding fragment
        // The BottomNavigationView will have two items: nav_search and nav_favourites
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> binding.viewPager.currentItem = 0
                R.id.nav_favourites -> binding.viewPager.currentItem = 1
            }
            true
        }

        // Sign out button functionality
        // When the sign out button is clicked, an AlertDialog is shown to confirm the action
        binding.signOutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes") { _, _ ->
                    Firebase.auth.signOut()
                    Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}
