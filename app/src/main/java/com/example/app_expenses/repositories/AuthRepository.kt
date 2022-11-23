package com.example.app_expenses.repositories

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.example.app_expenses.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AuthRepository(){
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth

    fun signInAuthenticate(email: String, password: String): Boolean{
        var signInSuccess = false
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener  { task ->
                if (task.isSuccessful) {
                    signInSuccess = true
                }
            }
        }
        return signInSuccess
    }

    fun createUser(firstName: String, lastName: String, email: String, password: String): Int{
        var isSuccessful = 0
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val newUser = UserData(firstName, lastName, email)
                firebaseDatabase.child("users").child(FirebaseAuth.getInstance().currentUser?.uid!!).child("profile").setValue(
                    newUser
                ).addOnCompleteListener { registerUser ->
                    isSuccessful = if(registerUser.isSuccessful){
                        2
                    } else{
                        1
                    }
                }
            }
        }
        return isSuccessful
    }

    fun currentUserExists(): Boolean{
        return auth.currentUser != null
    }
}