package com.example.app_expenses.repositories

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_expenses.data.UserData
import com.example.app_expenses.enums.SignUpEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthRepository(){
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth
    private val signInLiveData = MutableLiveData<Boolean>()
    private val sendPasswordLiveData = MutableLiveData<Boolean>()
    private val createUserLiveData = MutableLiveData<SignUpEnum>()
    private val currentUserName = MutableLiveData<String>()
    private val signOutLiveData = MutableLiveData<Boolean>()

    fun signInAuthenticate(email: String, password: String){
        CoroutineScope(Dispatchers.IO).launch {
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener  { task ->
                    signInLiveData.postValue(task.isSuccessful)
                }
            } else{
                signInLiveData.postValue(false)
            }
        }
    }

    fun getSignInLiveData(): LiveData<Boolean>{
        return signInLiveData
    }

    fun sendPasswordReset(email: String){
        CoroutineScope(Dispatchers.IO).launch{
            if(!TextUtils.isEmpty(email)){
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        sendPasswordLiveData.postValue(task.isSuccessful)
                    }
            } else{
                sendPasswordLiveData.postValue(false)
            }
        }

    }

    fun getSendPasswordLiveData(): LiveData<Boolean>{
        return sendPasswordLiveData
    }

    fun createUser(firstName: String, lastName: String, email: String, password: String){
        CoroutineScope(Dispatchers.IO).launch {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val newUser = UserData(firstName, lastName, email)
                    firebaseDatabase.child("users").child(FirebaseAuth.getInstance().currentUser?.uid!!).child("profile").setValue(
                        newUser
                    ).addOnCompleteListener { registerUser ->
                        if(registerUser.isSuccessful){
                            createUserLiveData.postValue(SignUpEnum.USER_CREATED)
                        } else{
                            createUserLiveData.postValue(SignUpEnum.USER_NOT_CREATED)
                        }
                    }
                } else{
                   createUserLiveData.postValue(SignUpEnum.EMAIL_TAKEN)
                }
            }
        }
    }

    fun getCreateUserLiveData(): LiveData<SignUpEnum>{
        return createUserLiveData
    }

    fun currentUserExists(): Boolean{
        return auth.currentUser != null
    }
    fun getCurrentUserName(){
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("profile/firstName")
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val name = snapshot.value as String
                            currentUserName.postValue(name)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    fun getCurrentUserNameLiveData(): LiveData<String>{
        return currentUserName
    }

    fun signOut(){
        if(currentUserExists()){
            auth.signOut().also {
                signOutLiveData.postValue(true)
            }
        } else{
            signOutLiveData.postValue(false)
        }
    }

    fun getSignOutLiveData(): LiveData<Boolean>{
        return signOutLiveData
    }
}