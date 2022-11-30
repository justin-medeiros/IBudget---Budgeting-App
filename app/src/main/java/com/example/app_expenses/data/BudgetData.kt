package com.example.app_expenses.data


import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BudgetData(val timeStamp: Long = 0, val categoryName: String? = null, val budgetName: String? = null, val budgetAmount: String? = null)