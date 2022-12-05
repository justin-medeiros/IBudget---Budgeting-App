package com.example.app_expenses.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app_expenses.data.BudgetCategoryData
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
import java.util.*

class CategoryBudgetsRepository {
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val auth: FirebaseAuth = Firebase.auth
    private val categoryTotalBudgetLiveData = MutableLiveData<BudgetCategoryData>()
    private val budgetCategoryLiveData = MutableLiveData<TreeMap<Int, BudgetCategoryData>>()
    private val budgetListLiveData = MutableLiveData<List<String>>()

    fun getCategoryBudgets(){
        // Using a tree map to pass the correct position of each category in descending order to display in the budgets page
        val newBudgetCategoryList: TreeMap<Int, BudgetCategoryData> = TreeMap()
        val budgetCategoryDefaultList = UtilitiesFunctions.createCategoriesBudgets()
        CoroutineScope(Dispatchers.Main).launch {
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
                                BudgetCategoryData(category.categoryName, newTotalBudget)

                        } else {
                            currentBudget.setValue("0")
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

    fun getCategoryBudgetsLiveData(): LiveData<TreeMap<Int, BudgetCategoryData>>{
        return budgetCategoryLiveData
    }

    fun subtractFromCategoryBudget(budgetCategoryData: BudgetCategoryData){
        CoroutineScope(Dispatchers.Main).launch {
            addSubtractCategoryBudget(budgetCategoryData, false)
        }
    }

    fun addToCategoryBudget(budgetCategoryData: BudgetCategoryData){
        CoroutineScope(Dispatchers.Main).launch {
            addSubtractCategoryBudget(budgetCategoryData, true)
        }
    }

    fun validateUniqueBudgetName(categoryName: String){
        val categoryNameList = mutableListOf<String>()
        CoroutineScope(Dispatchers.Main).launch {
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


    private fun addSubtractCategoryBudget(budgetCategoryData: BudgetCategoryData, isAdding: Boolean) {
            val categoryTotalBudget =
                firebaseDatabase.child("users").child(auth.currentUser?.uid!!).child("categories")
                    .child(budgetCategoryData.categoryName!!).child("total_budget")
            categoryTotalBudget.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var newTotalBudget: Float
                    val oldTotalBudget = snapshot.value as String?
                    if (oldTotalBudget == null) {
                        newTotalBudget = budgetCategoryData.budgetAmount!!.toFloat()
                    } else {
                        newTotalBudget = oldTotalBudget!!.toFloat() - budgetCategoryData.budgetAmount!!.toFloat()
                        if (isAdding) {
                            newTotalBudget = oldTotalBudget!!.toFloat() + budgetCategoryData.budgetAmount!!.toFloat()
                        }
                    }
                    categoryTotalBudget.setValue(newTotalBudget.toString())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

    fun getCategoryTotalBudget(categoryName: String){
        CoroutineScope(Dispatchers.Main).launch {
            firebaseDatabase
                .child("users").child(auth.currentUser?.uid!!).child("categories").child(categoryName).child("total_budget")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val totalBudget = dataSnapshot.getValue(String::class.java)
                            categoryTotalBudgetLiveData.postValue(BudgetCategoryData(categoryName, totalBudget))
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    fun getCategoryTotalBudgetLiveData(): LiveData<BudgetCategoryData>{
        return categoryTotalBudgetLiveData
    }
}