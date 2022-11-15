package com.example.app_expenses.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserData(val firstName: String? = null, val lastName: String? = null, val email: String?, val password: String?){}

