package com.example.app_expenses.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth

object PrefsHelper {
    private val PREF_NAME = auth.currentUser.toString()
    private const val DEF_STR = ""

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun writeString(key: String, value: String){
        with(sharedPreferences.edit()){
            this.putString(key, value)
            apply()
        }
    }

    fun readString(key: String) : String? = sharedPreferences.getString(key, DEF_STR)

}