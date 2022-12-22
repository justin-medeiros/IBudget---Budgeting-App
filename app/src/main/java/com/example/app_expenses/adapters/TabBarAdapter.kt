package com.example.app_expenses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app_expenses.fragments.BudgetFragment
import com.example.app_expenses.fragments.HomeFragment
import com.example.app_expenses.fragments.ResultsFragment
import com.example.app_expenses.fragments.TransactionListFragment

class TabBarAdapter(activity: FragmentActivity, private val tabCount: Int): FragmentStateAdapter(activity) {
    override fun getItemCount() = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> HomeFragment()
            1-> TransactionListFragment()
            2 -> ResultsFragment()
            else -> BudgetFragment()
        }
    }
}