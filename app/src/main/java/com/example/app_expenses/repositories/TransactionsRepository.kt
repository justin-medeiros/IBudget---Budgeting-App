package com.example.app_expenses.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.data.TransactionData
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
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class TransactionsRepository {
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth
    private val addTransactionLiveData = MutableLiveData<TransactionData?>()
    private val allTransactionsLiveData = MutableLiveData<MutableCollection<TransactionData>>()
    private val transactionsTotalAmountLiveData = MutableLiveData<Float>()
    private val totalCategoryTransactionLiveData = MutableLiveData<ArrayList<CategoryData>>()
    private val latestTransactionsListLiveData = MutableLiveData<List<TransactionData>>()

    fun getMyTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val myTransactionsList: TreeMap<Long, TransactionData> = TreeMap()
            firebaseDatabase
                .child("users").child(auth.currentUser?.uid!!).child("transactions")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (dataSnapshot1 in dataSnapshot.children) {
                                val transactionData = dataSnapshot1.getValue(TransactionData::class.java)
                                myTransactionsList[transactionData!!.timeStamp] = transactionData!!
                            }
                        }
                        allTransactionsLiveData.postValue(myTransactionsList.descendingMap().values)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    fun getMyTransactionsLiveData(): LiveData<MutableCollection<TransactionData>>{
        return allTransactionsLiveData
    }

    fun addTransaction(transactionData: TransactionData){
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("transactions").push()
                .setValue(transactionData).addOnCompleteListener { transactionAddedSuccessfully ->
                    if(transactionAddedSuccessfully.isSuccessful){
                        addTransactionLiveData?.postValue(transactionData)
                        getLatestTransactions()
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

    fun removeTransactions(listOfTransactionData: MutableCollection<TransactionData>) {
        for(transactionData in listOfTransactionData){
            CoroutineScope(Dispatchers.IO).launch {
                firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("transactions")
                    .orderByChild("timeStamp").equalTo(transactionData.timeStamp.toDouble())
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for(data in snapshot.children){
                                data.ref.removeValue().addOnSuccessListener {
                                    getLatestTransactions()
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }
    }

    fun addAllTransactions(listOfTransactionData: MutableCollection<TransactionData>) {
        for(transactionData in listOfTransactionData){
            CoroutineScope(Dispatchers.IO).launch {
                firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("transactions").push()
                    .setValue(transactionData).addOnCompleteListener { transactionAddedSuccessfully ->
                        if(transactionAddedSuccessfully.isSuccessful){
                            getLatestTransactions()
                            Log.e("SUCCESS", "Added successfully.")
                        }
                        else{
                            Log.e("ERROR", "Could not add to database.")
                        }
                    }
            }
        }
    }

    fun subtractFromTransactionsTotalAmount(transactionDataAmount: Float, transactionMonth: String){
        CoroutineScope(Dispatchers.IO).launch {
            subtractTransactionsTotal(transactionDataAmount, transactionMonth)
        }
    }

    fun addToTransactionsTotalAmount(transactionDataAmount: Float, transactionMonth: String){
        CoroutineScope(Dispatchers.IO).launch {
            addTransactionsTotal(transactionDataAmount, transactionMonth)
        }
    }

    fun addToCategoryTransactionsTotal(categoryName: String, transactionDataAmount: Float){
        CoroutineScope(Dispatchers.IO).launch {
            addSubtractCategoryTransactionsTotal(categoryName, transactionDataAmount, true)
        }
    }

    fun subtractFromCategoryTransactionsTotal(categoryName: String, transactionDataAmount: Float){
        CoroutineScope(Dispatchers.IO).launch {
            addSubtractCategoryTransactionsTotal(categoryName, transactionDataAmount, false)
        }
    }

    private fun addTransactionsTotal(transactionDataAmount: Float, transactionMonth: String) {
        val transactionsTotalBudget =
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("transactions_total")
                .child(transactionMonth)
        transactionsTotalBudget.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newTotalBudget: Float
                val oldTotalBudget = snapshot.value as String?
                newTotalBudget = if (oldTotalBudget == null) {
                    transactionDataAmount
                } else {
                    oldTotalBudget!!.toFloat() + transactionDataAmount
                }
                transactionsTotalBudget.setValue(newTotalBudget.toString())
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun subtractTransactionsTotal(transactionDataAmount: Float, transactionMonth: String){
        val transactionsTotalBudget =
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("transactions_total")
                .child(transactionMonth)
        transactionsTotalBudget.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newTotalBudget: Float
                val oldTotalBudget = snapshot.value as String?
                newTotalBudget = if (oldTotalBudget == null) {
                    0f
                } else {
                    oldTotalBudget!!.toFloat() - transactionDataAmount
                }
                transactionsTotalBudget.setValue(newTotalBudget.toString())
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addSubtractCategoryTransactionsTotal(categoryName: String, transactionAmount: Float, isAdding: Boolean) {
        val categoryTotalBudget =
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("categories")
                .child(categoryName).child("category_transactions_total")
        categoryTotalBudget.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newTotalBudget: Float
                val oldTotalBudget = snapshot.value as String?
                if (oldTotalBudget == null) {
                    newTotalBudget = transactionAmount
                } else {
                    newTotalBudget = oldTotalBudget!!.toFloat() - transactionAmount
                    if (isAdding) {
                        newTotalBudget = oldTotalBudget!!.toFloat() + transactionAmount
                    }
                }
                categoryTotalBudget.setValue(newTotalBudget.toString())
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getTransactionsTotalAmount(){
        CoroutineScope(Dispatchers.IO).launch {
            val currentMonth = UtilitiesFunctions.timestampToMonthYear(System.currentTimeMillis())
            firebaseDatabase
                .child("users").child(auth.currentUser?.uid!!).child("transactions_total")
                .child(currentMonth)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val transactionsTotal = dataSnapshot.getValue(String::class.java)
                            transactionsTotalAmountLiveData.postValue(transactionsTotal!!.toFloat())
                        } else{
                            transactionsTotalAmountLiveData.postValue(0f)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    fun getTransactionsTotalAmountLiveData(): LiveData<Float>{
        return transactionsTotalAmountLiveData
    }

    fun getCategoryTransactionsTotal() {
        val newCategoryTransactionList: ArrayList<CategoryData> = ArrayList()
        val budgetCategoryDefaultList = UtilitiesFunctions.createCategoriesBudgets()
        CoroutineScope(Dispatchers.IO).launch {
            for (category in budgetCategoryDefaultList) {
                val currentBudget = firebaseDatabase.child("users").child(auth.currentUser?.uid!!)
                    .child("categories")
                    .child(category.categoryName!!).child("category_transactions_total")
                currentBudget.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val categoryPosition =
                            UtilitiesFunctions.getCategoryBudgetsPosition(category.categoryName)
                        if (dataSnapshot.value != null) {
                            val newCategoryTransactionTotal = dataSnapshot.value as String?
                            if(newCategoryTransactionTotal?.toFloat() != 0F){
                                newCategoryTransactionList.add(CategoryData(category.categoryName, newCategoryTransactionTotal))
                            }
                        }
                        if (categoryPosition == budgetCategoryDefaultList.size - 1) {
                            totalCategoryTransactionLiveData.postValue(newCategoryTransactionList)
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }

    fun getCategoryTransactionsTotalLiveData(): LiveData<ArrayList<CategoryData>>{
        return totalCategoryTransactionLiveData
    }

    fun getLatestTransactions(){
        CoroutineScope(Dispatchers.IO).launch {
            val latestTransactionsMap: TreeMap<Long, TransactionData> = TreeMap()
            val currentMonth = UtilitiesFunctions.timestampToMonthYear(System.currentTimeMillis())
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!)
                .child("transactions")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        for (dataSnapshot1 in dataSnapshot.children) {
                            val transactionName = dataSnapshot1.child("transactionName").value as String
                            val transactionAmount =
                                dataSnapshot1.child("transactionAmount").value as String
                            val transactionCategory =
                                dataSnapshot1.child("categoryName").value as String
                            val transactionTimeStamp = dataSnapshot1.child("timeStamp").value as Long
                            val transactionData = TransactionData(transactionTimeStamp, transactionCategory, transactionName, transactionAmount)
                            if(currentMonth == UtilitiesFunctions.timestampToMonthYear(transactionTimeStamp)){
                                latestTransactionsMap[transactionTimeStamp] = transactionData
                            }
                        }
                        latestTransactionsListLiveData.postValue(getFiveLatestTransactions(latestTransactionsMap))
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    fun getLatestTransactionsLiveData(): LiveData<List<TransactionData>>{
        return latestTransactionsListLiveData
    }

    private fun getFiveLatestTransactions(map: TreeMap<Long, TransactionData>): List<TransactionData>{
        var tempList = arrayListOf<TransactionData>()
        var i = 0
        for(item in map.descendingMap().values){
            if(i < 5){
                tempList.add(item)
                i++
            } else{
                break
            }
        }
        return tempList
    }

}