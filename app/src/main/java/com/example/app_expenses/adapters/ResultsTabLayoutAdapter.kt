package com.example.app_expenses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app_expenses.HomeFragment
import com.example.app_expenses.fragments.BudgetFragment
import com.example.app_expenses.fragments.TransactionListFragment

class ResultsTabLayoutAdapter(activity: FragmentActivity, private val tabCount: Int): FragmentStateAdapter(activity) {
    override fun getItemCount() = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> HomeFragment()
            else -> HomeFragment()
        }
    }
}