package com.example.app_expenses.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth

object PrefsHelper {
    private val PREF_NAME = auth.currentUser.toString()
    private const val DEF_STR = ""
    private const val DEF_FLOAT = 0.00F

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

    fun writeFloat(key: String, value: Float){
        with(sharedPreferences.edit()){
            this.putFloat(key, value)
            apply()
        }
    }

    fun deleteAll(){
        sharedPreferences.edit().clear().commit()
    }

    fun readString(key: String) : String? = sharedPreferences.getString(key, DEF_STR)

    fun readFloat(key: String): Float? = sharedPreferences.getFloat(key, DEF_FLOAT)

    fun onBudgetChangeListener(textView: TextView){
        val listener =
            OnSharedPreferenceChangeListener { sharedPreferences, key ->
                val newBudget = sharedPreferences.getFloat(key, DEF_FLOAT)
                textView.text = "$%.2f".format(newBudget)
            }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
}