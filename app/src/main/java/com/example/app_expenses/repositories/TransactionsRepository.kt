package com.example.app_expenses.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions
import com.google.firebase.auth.FirebaseAuth
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

class TransactionsRepository {
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth
    private val addTransactionLiveData = MutableLiveData<TransactionData?>()
    private val allTransactionLiveData = MutableLiveData<List<TransactionData>>()

    fun getMyTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val myTransactionsList: MutableList<TransactionData> = mutableListOf()
            // Want to order by timestamp so that the transactions are in the correct order every launch (most recent to oldest)
            firebaseDatabase
                .child("users").child(auth.currentUser?.uid!!).child("transactions")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (dataSnapshot1 in dataSnapshot.children) {
                                val transactionData = dataSnapshot1.getValue(TransactionData::class.java)
                                myTransactionsList.add(0, transactionData!!)
                            }
                        }
                        allTransactionLiveData.postValue(myTransactionsList)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    fun getMyTransactionsLiveData(): LiveData<List<TransactionData>>{
        return allTransactionLiveData
    }

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

//    fun removeTransaction(budgetName: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("categories")
//                .child(categoryName).child("budgets").orderByChild("budgetName").equalTo(budgetName)
//                .addListenerForSingleValueEvent(object : ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for(data in snapshot.children){
//                            data.ref.removeValue()
//                        }
//                    }
//                    override fun onCancelled(error: DatabaseError) {}
//                })
//        }
//    }

//    fun subtractFromCategoryBudget(budgetCategoryData: BudgetCategoryData){
//        CoroutineScope(Dispatchers.IO).launch {
//            addSubtractCategoryBudget(budgetCategoryData, false)
//        }
//    }
//
//    fun addToCategoryBudget(budgetCategoryData: BudgetCategoryData){
//        CoroutineScope(Dispatchers.IO).launch {
//            addSubtractCategoryBudget(budgetCategoryData, true)
//        }
//    }

    fun getAddTransactionLiveData(): LiveData<TransactionData?> {
        return addTransactionLiveData
    }

}