package com.example.app_expenses.data

import com.example.app_expenses.enums.CategoryEnum

data class MyBudgetData(val category: CategoryEnum? = null, val budgetName: String? = null, val budgetAmount: String? = null)

