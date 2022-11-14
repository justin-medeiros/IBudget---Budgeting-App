package com.example.app_expenses

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.app_expenses.databinding.ActivitySplashScreenBinding
import com.example.app_expenses.fragments.LoginFragment


class SplashScreenActivity: AppCompatActivity() {

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
            callLoginFragment()
        }, 5000)
    }

    private fun callLoginFragment(){
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutSplashScreen, LoginFragment(), LoginFragment:: class.java.simpleName)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {} // disallow back pressing
}