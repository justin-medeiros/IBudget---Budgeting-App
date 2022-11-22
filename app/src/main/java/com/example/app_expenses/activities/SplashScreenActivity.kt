package com.example.app_expenses.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.app_expenses.R
import com.example.app_expenses.databinding.ActivitySplashScreenBinding
import com.example.app_expenses.fragments.LoginFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth

class SplashScreenActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        auth = Firebase.auth
        finishSplashScreen(binding)
    }

    private fun finishSplashScreen(binding: ActivitySplashScreenBinding){
        Handler(Looper.getMainLooper()).postDelayed({
            binding.nameTv.visibility = View.GONE
            binding.nameTwoTv.visibility = View.GONE
            binding.imageSplashScreen.visibility = View.GONE
            if(auth.currentUser != null){
                callMainActivity()
            }else{
                callLoginFragment()
            }
        }, 5000)
    }

    private fun callLoginFragment(){
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutSplashScreen, LoginFragment(), LoginFragment:: class.java.simpleName)
            .addToBackStack(null)
            .commit()
    }

    private fun callMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {}
}