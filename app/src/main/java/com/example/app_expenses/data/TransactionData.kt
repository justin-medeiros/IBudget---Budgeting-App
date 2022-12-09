package com.example.app_expenses.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TransactionData(val timeStamp: Long = 0, val categoryName: String? = null, val transactionName: String? = null, val transactionAmount: String? = null)