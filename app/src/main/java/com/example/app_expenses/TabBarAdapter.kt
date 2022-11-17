package com.example.app_expenses

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabBarAdapter(activity: FragmentActivity, private val tabCount: Int): FragmentStateAdapter(activity) {
    override fun getItemCount() = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> HomeFragment()
            1-> Add_Budget()
            2 -> HomeFragment()
            3-> HomeFragment()
            else -> HomeFragment()
        }
    }
}