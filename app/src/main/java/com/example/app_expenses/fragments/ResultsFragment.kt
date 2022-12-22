package com.example.app_expenses.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.app_expenses.R
import com.example.app_expenses.adapters.ResultsTabLayoutAdapter
import com.example.app_expenses.databinding.FragmentResultsBinding
import com.google.android.material.tabs.TabLayout

class ResultsFragment: Fragment() {
    private lateinit var fragmentResultsBinding: FragmentResultsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentResultsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_results,
            container, false)
        setupTabLayout()
        return fragmentResultsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupTabLayout(){
        val resultsAdapter = ResultsTabLayoutAdapter(requireActivity(), fragmentResultsBinding.resultsTabLayout.tabCount)
        fragmentResultsBinding.resultsViewPager.adapter = resultsAdapter
        // Set current page to Transactions and then set back to Budgets to make sure both tab fragments views are created before user goes to the Results page
        fragmentResultsBinding.resultsViewPager.currentItem = 1
        fragmentResultsBinding.resultsViewPager.currentItem = 0
        fragmentResultsBinding.resultsViewPager.isUserInputEnabled = false
        fragmentResultsBinding.resultsTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                fragmentResultsBinding.resultsViewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        fragmentResultsBinding.resultsViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                fragmentResultsBinding.resultsTabLayout.selectTab(fragmentResultsBinding.resultsTabLayout.getTabAt(position))
            }
        })
    }
}