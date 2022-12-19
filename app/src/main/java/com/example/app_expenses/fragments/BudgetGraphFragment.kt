package com.example.app_expenses.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.app_expenses.R
import com.example.app_expenses.data.BudgetCategoryData
import com.example.app_expenses.databinding.FragmentBudgetGraphBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.BudgetsViewModel
import com.example.app_expenses.viewModels.CategoryBudgetsViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class BudgetGraphFragment: Fragment() {
    private lateinit var fragmentBudgetGraphBinding: FragmentBudgetGraphBinding
    private var totalCategoryBudget: Float = 0F
    var budgetMap: TreeMap<Int, BudgetCategoryData> = TreeMap()
    private val categoryBudgetsViewModel: CategoryBudgetsViewModel by viewModels()
    private val budgetsViewModel: BudgetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBudgetGraphBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_budget_graph,
            container, false)

        budgetsViewModel.getTotalBudget()
        budgetsViewModel.getTotalBudgetLiveData().observe(viewLifecycleOwner){ total ->
            totalCategoryBudget = total
            categoryBudgetsViewModel.getCategoryBudgets()
        }

        categoryBudgetsViewModel.getCategoryBudgetsLiveData().observe(viewLifecycleOwner){ map ->
            budgetMap = map
            fragmentBudgetGraphBinding.pieChartBudgetGraph.notifyDataSetChanged()
            fragmentBudgetGraphBinding.pieChartBudgetGraph.invalidate()
            lifecycleScope.launch {
                loadPieChartData()
                setupChart()
            }
        }

        return fragmentBudgetGraphBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadPieChartData(){
        var pieEntries: ArrayList<PieEntry> = ArrayList()
        var colors: ArrayList<Int> = ArrayList()

        budgetMap.forEach { item ->
            if(item.value.budgetAmount?.toFloat() != 0F){
                val category = UtilitiesFunctions.getCategoryEnum(item.value.categoryName!!)
                colors.add(ContextCompat.getColor(requireContext(), category?.categoryColor!!))
                pieEntries.add(PieEntry(UtilitiesFunctions.calculateTotalPercentage(item.value.budgetAmount!!.toFloat(), totalCategoryBudget),
                    item.value.categoryName!!))
            }

        }

        val pieDataSet = PieDataSet(pieEntries, "Budget Category")
        pieDataSet.valueTextSize = 12f
        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet) // Grouping data set to chart
        pieData.setValueFormatter(PercentFormatter(fragmentBudgetGraphBinding.pieChartBudgetGraph))
        pieData.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        pieData.setValueTypeface(ResourcesCompat.getFont(requireContext(), R.font.figerona_bold))
        pieData.setDrawValues(true)

        fragmentBudgetGraphBinding.pieChartBudgetGraph.data = pieData
    }

    private fun setupChart(){
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setDrawCenterText(true)
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setUsePercentValues(true)
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setEntryLabelTextSize(12f)
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setEntryLabelTypeface(ResourcesCompat.getFont(requireContext(), R.font.figerona_bold))
        fragmentBudgetGraphBinding.pieChartBudgetGraph.centerText = "Budgets by Category"
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setCenterTextSize(24f)
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setCenterTextTypeface(ResourcesCompat.getFont(requireContext(), R.font.figerona_bold))
        fragmentBudgetGraphBinding.pieChartBudgetGraph.setHoleColor(Color.TRANSPARENT)
        fragmentBudgetGraphBinding.pieChartBudgetGraph.description.isEnabled = false
    }
}