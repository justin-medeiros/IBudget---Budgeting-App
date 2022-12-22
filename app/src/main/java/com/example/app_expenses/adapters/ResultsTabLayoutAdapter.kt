package com.example.app_expenses.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app_expenses.fragments.BudgetGraphFragment
import com.example.app_expenses.fragments.TransactionGraphFragment

class ResultsTabLayoutAdapter(activity: FragmentActivity, private val tabCount: Int): FragmentStateAdapter(activity) {
    override fun getItemCount() = tabCount

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> BudgetGraphFragment()
            else -> TransactionGraphFragment()
        }
    }
}