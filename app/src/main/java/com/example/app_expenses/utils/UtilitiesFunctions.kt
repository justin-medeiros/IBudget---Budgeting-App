package com.example.app_expenses.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object UtilitiesFunctions{
    fun replaceFragment(activity: FragmentActivity, fragment: Fragment, id: Int){
        val backStackName = fragment.javaClass.name
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(id, fragment)
        transaction?.addToBackStack(backStackName)
        transaction?.commit()
    }
}