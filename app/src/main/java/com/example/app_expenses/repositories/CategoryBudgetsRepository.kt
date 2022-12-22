package com.example.app_expenses.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.utils.UtilitiesFunctions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.utilities.Tree
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class CategoryBudgetsRepository {
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth
    private val categoryTotalBudgetLiveData = MutableLiveData<CategoryData>()
    private val latestBudgetsLiveData = MutableLiveData<List<BudgetData>>()
    private val budgetCategoryLiveData = MutableLiveData<TreeMap<Int, CategoryData>>()
    private val budgetListLiveData = MutableLiveData<List<String>>()

    fun getCategoryBudgets(){
        // Using a tree map to pass the correct position of each category in descending order to display in the budgets page
        val newBudgetCategoryList: TreeMap<Int, CategoryData> = TreeMap()
        val budgetCategoryDefaultList = UtilitiesFunctions.createCategoriesBudgets()
        CoroutineScope(Dispatchers.IO).launch {
            for (category in budgetCategoryDefaultList) {
                val currentBudget = firebaseDatabase.child("users").child(auth.currentUser?.uid!!)
                    .child("categories")
                    .child(category.categoryName!!).child("total_budget")
                currentBudget.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val categoryPosition = UtilitiesFunctions.getCategoryBudgetsPosition(category.categoryName)
                        if (dataSnapshot.value != null) {
                            val newTotalBudget = dataSnapshot.value as String?
                            newBudgetCategoryList[categoryPosition] =
                                CategoryData(category.categoryName, newTotalBudget)

                        } else {
                            currentBudget.setValue("0.0")
                            newBudgetCategoryList[categoryPosition] =
                                category
                        }
                        if (categoryPosition == budgetCategoryDefaultList.size-1) {
                            budgetCategoryLiveData.postValue(newBudgetCategoryList)
                        }

                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }

    fun getCategoryBudgetsLiveData(): LiveData<TreeMap<Int, CategoryData>>{
        return budgetCategoryLiveData
    }

    fun getLatestCategoryBudgets(){
        CoroutineScope(Dispatchers.IO).launch {
            // Using a tree map to pass the correct position of each category in descending order to display in the budgets page
            val latestBudgetsList: TreeMap<Int, BudgetData> = TreeMap()
            val budgetCategoryDefaultList = UtilitiesFunctions.createCategoriesBudgets()
            for (category in budgetCategoryDefaultList) {
                val currentBudget = firebaseDatabase.child("users").child(auth.currentUser?.uid!!)
                    .child("categories")
                    .child(category.categoryName!!).child("budgets")
                currentBudget.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (dataSnapshot1 in dataSnapshot.children) {
                                val budgetName = dataSnapshot1.child("budgetName").value as String
                                val budgetAmount =
                                    dataSnapshot1.child("budgetAmount").value as String
                                val budgetCategory =
                                    dataSnapshot1.child("categoryName").value as String
                                val budgetTimeStamp = dataSnapshot1.child("timeStamp").value as Long
                                val budgetData = BudgetData(budgetTimeStamp, budgetCategory, budgetName, budgetAmount)
                                latestBudgetsList[budgetTimeStamp.toInt()] = budgetData
                            }
                        }
                        if(category.categoryName == "Personal Spending"){
                            latestBudgetsLiveData.postValue(getFiveLatestBudgets(latestBudgetsList))
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }

    fun getLatestBudgetsLiveData(): LiveData<List<BudgetData>>{
        return latestBudgetsLiveData
    }

    private fun getFiveLatestBudgets(map: TreeMap<Int, BudgetData>): List<BudgetData>{
        var tempList = arrayListOf<BudgetData>()
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

    fun subtractFromCategoryBudget(CategoryData: CategoryData){
        CoroutineScope(Dispatchers.IO).launch {
            addSubtractCategoryBudget(CategoryData, false)
        }
    }

    fun addToCategoryBudget(CategoryData: CategoryData){
        CoroutineScope(Dispatchers.IO).launch {
            addSubtractCategoryBudget(CategoryData, true)
        }
    }

    fun validateUniqueBudgetName(categoryName: String){
        val categoryNameList = mutableListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("categories")
                .child(categoryName).child("budgets").addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(data in snapshot.children){
                            val budgetName = data.child("budgetName").value as String
                            categoryNameList.add(budgetName.lowercase())
                        }
                        budgetListLiveData.postValue(categoryNameList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    fun getBudgetListLiveData(): LiveData<List<String>>{
        return budgetListLiveData
    }


    private fun addSubtractCategoryBudget(CategoryData: CategoryData, isAdding: Boolean) {
            val categoryTotalBudget =
                firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("categories")
                    .child(CategoryData.categoryName!!).child("total_budget")
            categoryTotalBudget.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var newTotalBudget: Float
                    val oldTotalBudget = snapshot.value as String?
                    if (oldTotalBudget == null) {
                        newTotalBudget = CategoryData.totalAmount!!.toFloat()
                    } else {
                        newTotalBudget = oldTotalBudget!!.toFloat() - CategoryData.totalAmount!!.toFloat()
                        if (isAdding) {
                            newTotalBudget = oldTotalBudget!!.toFloat() + CategoryData.totalAmount!!.toFloat()
                        }
                    }
                    categoryTotalBudget.setValue(newTotalBudget.toString())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

    fun getCategoryTotalBudget(categoryName: String){
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabase
                .child("users").child(auth.currentUser?.uid!!).child("categories").child(categoryName).child("total_budget")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val totalBudget = dataSnapshot.getValue(String::class.java)
                            categoryTotalBudgetLiveData.postValue(CategoryData(categoryName, totalBudget))
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    fun getCategoryTotalBudgetLiveData(): LiveData<CategoryData>{
        return categoryTotalBudgetLiveData
    }

}