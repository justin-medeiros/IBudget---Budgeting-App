package com.example.app_expenses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.app_expenses.databinding.ActivityLoginBinding
import com.example.app_expenses.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

private lateinit var binding: ActivityLoginBinding
private lateinit var passwordText: TextInputEditText
private lateinit var emailText: TextInputEditText

class LoginActivity(): AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun checkCredentials(){

    }
}
