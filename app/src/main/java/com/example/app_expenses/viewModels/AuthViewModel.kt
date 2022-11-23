package com.example.app_expenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_expenses.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(): ViewModel() {

    private val authRepository = AuthRepository()

    fun signInAuth(email: String, password: String): Boolean{
        var isSuccess = false
        viewModelScope.launch(Dispatchers.IO) {
            isSuccess = authRepository.signInAuthenticate(email, password)
        }
        return isSuccess
    }

    fun createUserAuth(firstName: String, lastName: String, email: String, password: String): Int{
        var isSuccess = 0
        viewModelScope.launch(Dispatchers.IO) {
            isSuccess = authRepository.createUser(firstName, lastName, email, password)
        }
        return isSuccess
    }

    fun currentUserExists(): Boolean{
        return authRepository.currentUserExists()
    }
}