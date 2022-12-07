package com.example.app_expenses.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.utils.UtilitiesFunctions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionsRepository {
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth
    private val addTransactionLiveData = MutableLiveData<TransactionData?>()

    fun addTransaction(transactionData: TransactionData){
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("transactions").push().setValue(transactionData).addOnCompleteListener { transactionAddedSuccessfully ->
                    if(transactionAddedSuccessfully.isSuccessful){
                        addTransactionLiveData?.postValue(transactionData)
                    }
                    else{
                        addTransactionLiveData?.postValue(null)
                    }
                }
        }
    }

    fun getAddTransactionLiveData(): LiveData<TransactionData?> {
        return addTransactionLiveData
    }

}