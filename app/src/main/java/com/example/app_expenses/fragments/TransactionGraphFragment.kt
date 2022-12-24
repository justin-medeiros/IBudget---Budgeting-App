package com.example.app_expenses.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.TransactionsTotalValuesAdapter
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.databinding.FragmentTransactionsGraphBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.TransactionsViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class TransactionGraphFragment: Fragment() {
    private lateinit var fragmentTransactionGraphBinding: FragmentTransactionsGraphBinding
    private var totalTransactionAmount: Float = 0F
    private var transactionTotalValuesAdapter = TransactionsTotalValuesAdapter()
    private var transactionsList: ArrayList<CategoryData> = ArrayList()
    private val mainActivity = MainActivity()
    private lateinit var transactionsViewModel: TransactionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTransactionGraphBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transactions_graph,
            container, false)
        transactionsViewModel = ViewModelProvider(requireActivity())[TransactionsViewModel::class.java]
        transactionsViewModel.getTransactionsTotalAmount()
        transactionsViewModel.getTransactionsTotalAmountLiveData().observe(viewLifecycleOwner){ total ->
            totalTransactionAmount = total
            lifecycleScope.launch {
                delay(150)
                transactionsViewModel.getCategoryTransactionsTotal()
            }
        }

        transactionsViewModel.getCategoryTransactionsTotalLiveData().observe(viewLifecycleOwner){ list ->
            transactionsList = list
            lifecycleScope.launch {
                loadPieChartData()
            }
        }

        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentTransactionGraphBinding.rvTransactionTotals.layoutManager = myLinearLayoutManager
        fragmentTransactionGraphBinding.rvTransactionTotals.adapter = transactionTotalValuesAdapter
        return fragmentTransactionGraphBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentTransactionGraphBinding.containerTransactionsTotal.visibility = View.GONE
        fragmentTransactionGraphBinding.buttonSeeAllTransactionTotal.visibility = View.GONE
        fragmentTransactionGraphBinding.tvNoTransactions.visibility = View.VISIBLE

        fragmentTransactionGraphBinding.buttonSeeAllTransactionTotal.setOnClickListener {
            mainActivity.switchToTab(1)
        }
    }

    private fun loadPieChartData(){
        var pieEntries: ArrayList<PieEntry> = ArrayList()
        var colors: ArrayList<Int> = ArrayList()

        transactionsList.forEach { item ->
            val totalAmount = item.totalAmount?.toFloat()
            if(totalAmount != 0F){
                val category = UtilitiesFunctions.getCategoryEnum(item.categoryName!!)
                colors.add(ContextCompat.getColor(requireContext(), category?.categoryColor!!))
                pieEntries.add(
                    PieEntry(
                        UtilitiesFunctions.calculateTotalPercentage(item.totalAmount!!.toFloat(), totalTransactionAmount),
                    item.categoryName!!)
                )
            }
        }
        transactionsList.sortByDescending{ categoryData -> categoryData.totalAmount?.toFloat() }
        transactionTotalValuesAdapter.replaceAll(transactionsList)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if(pieEntries.size == 0){
            fragmentTransactionGraphBinding.pieChartTransactionGraph.visibility = View.GONE
            params.setMargins(
                UtilitiesFunctions.convertDpToPixel(20F, requireContext()).toInt(),
                UtilitiesFunctions.convertDpToPixel(20F, requireContext()).toInt(),
                UtilitiesFunctions.convertDpToPixel(20F, requireContext()).toInt(),
                0)
            fragmentTransactionGraphBinding.transactionTotalValuesTitle.layoutParams = params
            fragmentTransactionGraphBinding.containerTransactionsTotal.visibility = View.GONE
            fragmentTransactionGraphBinding.tvNoTransactions.visibility = View.VISIBLE

        } else{
            fragmentTransactionGraphBinding.pieChartTransactionGraph.visibility = View.VISIBLE
            params.setMargins(
                UtilitiesFunctions.convertDpToPixel(20F, requireContext()).toInt(),
                UtilitiesFunctions.convertDpToPixel(-100F, requireContext()).toInt(),
                UtilitiesFunctions.convertDpToPixel(20F, requireContext()).toInt(),
                0)
            fragmentTransactionGraphBinding.transactionTotalValuesTitle.layoutParams = params
            fragmentTransactionGraphBinding.buttonSeeAllTransactionTotal.visibility = View.VISIBLE
            fragmentTransactionGraphBinding.containerTransactionsTotal.visibility = View.VISIBLE
            fragmentTransactionGraphBinding.tvNoTransactions.visibility = View.GONE

            val pieDataSet = PieDataSet(pieEntries, "Budget Category")
            pieDataSet.valueTextSize = 12f
            pieDataSet.colors = colors

            val pieData = PieData(pieDataSet) // Grouping data set to chart
            pieData.setValueFormatter(PercentFormatter(fragmentTransactionGraphBinding.pieChartTransactionGraph))
            pieData.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            pieData.setValueTypeface(ResourcesCompat.getFont(requireContext(), R.font.figerona_bold))
            pieData.setDrawValues(true)

            fragmentTransactionGraphBinding.pieChartTransactionGraph.data = pieData
            fragmentTransactionGraphBinding.pieChartTransactionGraph.invalidate()

            setupChart()
        }
    }

    private fun setupChart(){
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setDrawCenterText(true)
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setUsePercentValues(true)
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setEntryLabelTextSize(12f)
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setEntryLabelTypeface(ResourcesCompat.getFont(requireContext(), R.font.figerona_bold))
        fragmentTransactionGraphBinding.pieChartTransactionGraph.centerText = "Transactions This Month by Category"
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setCenterTextSize(24f)
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setCenterTextTypeface(ResourcesCompat.getFont(requireContext(), R.font.figerona_bold))
        fragmentTransactionGraphBinding.pieChartTransactionGraph.setHoleColor(Color.TRANSPARENT)
        fragmentTransactionGraphBinding.pieChartTransactionGraph.extraTopOffset = -100f
        fragmentTransactionGraphBinding.pieChartTransactionGraph.description.isEnabled = false
        fragmentTransactionGraphBinding.pieChartTransactionGraph.legend.isEnabled = false
    }
}