package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.enums.SignUpEnum
import com.example.app_expenses.repositories.AuthRepository

class AuthViewModel(): ViewModel() {


    private val authRepository = AuthRepository()
    private val signInAuthLiveData: LiveData<Boolean> = authRepository.getSignInLiveData()
    private val passwordSend: LiveData<Boolean> = authRepository.getSendPasswordLiveData()
    private val createUserLiveData: LiveData<SignUpEnum> = authRepository.getCreateUserLiveData()

    fun signInAuth(email: String, password: String){
        authRepository.signInAuthenticate(email, password)
    }

    fun getSignInAuthLiveData(): LiveData<Boolean>{
        return signInAuthLiveData
    }

    fun sendPasswordReset(email: String){
        authRepository.sendPasswordReset(email)
    }

    fun getSendPasswordResetLiveData(): LiveData<Boolean>{
        return passwordSend
    }

    fun createUserAuth(firstName: String, lastName: String, email: String, password: String){
        authRepository.createUser(firstName, lastName, email, password)
    }

    fun getCreateUserLiveData(): LiveData<SignUpEnum>{
        return createUserLiveData
    }

    fun currentUserExists(): Boolean{
        return authRepository.currentUserExists()
    }
}