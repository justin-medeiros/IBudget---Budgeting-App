package com.example.app_expenses.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object UtilitiesFunctions{
    fun replaceFragment(activity: FragmentActivity, fragment: Fragment, id: Int, addToBackStack: Boolean){
        var backStackName = fragment.javaClass.name
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(id, fragment)
        if(!addToBackStack){
            backStackName = null
        }
        transaction?.addToBackStack(backStackName)
        transaction?.commit()
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}