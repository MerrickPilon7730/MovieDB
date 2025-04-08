package com.example.prog3210_assignment2.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prog3210_assignment2.databinding.SignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    // SIgn In Activity is responsible for user login.
    // It validates user input, signs in the user with Firebase Authentication,
    private lateinit var binding: SignInBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth.signOut()

        binding.signInBtn.setOnClickListener {
            val email = binding.userEmail.text.toString().trim()
            val password = binding.userPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                }

                password.isEmpty() -> {
                    Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { goToMain() }
                        .addOnFailureListener {
                            Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        // Set up the register button to navigate to the RegisterActivity
        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
