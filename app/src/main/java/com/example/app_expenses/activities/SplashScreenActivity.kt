package com.example.app_expenses.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.app_expenses.R
import com.example.app_expenses.databinding.ActivitySplashScreenBinding
import com.example.app_expenses.fragments.LoginFragment
import com.example.app_expenses.viewModels.AuthViewModel


class SplashScreenActivity: AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        finishSplashScreen(binding)
    }

    private fun finishSplashScreen(binding: ActivitySplashScreenBinding){
        Handler(Looper.getMainLooper()).postDelayed({
            binding.nameTv.visibility = View.GONE
            binding.nameTwoTv.visibility = View.GONE
            binding.imageSplashScreen.visibility = View.GONE
            if(authViewModel.currentUserExists()){
                callMainActivity()
            } else{
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