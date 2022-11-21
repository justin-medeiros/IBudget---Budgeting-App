package com.example.app_expenses.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Category(val categoryName: String? = null, val color: Int? = null, val icon: Int? = null)
