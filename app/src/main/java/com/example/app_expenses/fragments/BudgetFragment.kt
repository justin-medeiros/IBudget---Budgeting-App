package com.example.app_expenses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.BudgetsAdapter
import com.example.app_expenses.databinding.FragmentBudgetBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.BudgetsViewModel
import androidx.recyclerview.widget.*
import com.example.app_expenses.viewModels.CategoryBudgetsViewModel


class BudgetFragment: Fragment() {
    private lateinit var fragmentBudgetBinding: FragmentBudgetBinding
    private var mainActivity: MainActivity = MainActivity()
    var myBudgetsCategoryAdapter = BudgetsAdapter()
    private val myBudgetsViewModel: BudgetsViewModel by viewModels()
    private val categoryBudgetsViewModel: CategoryBudgetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentBudgetBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_budget,
            container, false)
        categoryBudgetsViewModel.getCategoryBudgets()
        myBudgetsViewModel.getTotalBudget()
        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentBudgetBinding.rvBudget.layoutManager = myLinearLayoutManager
        fragmentBudgetBinding.rvBudget.adapter = myBudgetsCategoryAdapter
        return fragmentBudgetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        categoryBudgetsViewModel.getCategoryBudgetsLiveData().observe(viewLifecycleOwner){ categoryBudgetsList ->
            myBudgetsCategoryAdapter.addAll(categoryBudgetsList.values)
        }

        myBudgetsViewModel.getTotalBudgetLiveData().observe(viewLifecycleOwner){ totalBudget ->
            fragmentBudgetBinding.tvBudgetAmount.text = "$%.2f".format(totalBudget)
        }

        fragmentBudgetBinding.addNewBudgetButton.setOnClickListener {
            fragmentBudgetBinding.rvBudget.itemAnimator = null
            UtilitiesFunctions.replaceFragment(requireActivity(), AddBudgetFragment(myBudgetsCategoryAdapter), R.id.relativeLayoutMainActivity, true)
            mainActivity.hideTabBarVisibility()
        }
    }
}