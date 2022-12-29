package com.example.app_expenses.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.BudgetGoalAdapter
import com.example.app_expenses.adapters.LatestTransactionsAdapter
import com.example.app_expenses.data.BudgetGoalData
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.databinding.FragmentHomeBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel
import com.example.app_expenses.viewModels.BudgetsViewModel
import com.example.app_expenses.viewModels.CategoryBudgetsViewModel
import com.example.app_expenses.viewModels.TransactionsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment: Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var signOutAlertDialog: AlertDialog
    private lateinit var latestTransactionAdapter: LatestTransactionsAdapter
    private lateinit var budgetGoalAdapter: BudgetGoalAdapter
    private lateinit var transactionsViewModel: TransactionsViewModel
    private val mainActivity = MainActivity()
    private val authViewModel: AuthViewModel by viewModels()
    private val budgetsViewModel: BudgetsViewModel by viewModels()
    private val categoryBudgetsViewModel: CategoryBudgetsViewModel by viewModels()

    private var totalBalance = -1f
    private var totalBudget = -1f
    private var totalCategoryBudgets: TreeMap<Int, CategoryData> = TreeMap()
    private var totalCategoryTransactions: ArrayList<CategoryData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
            container, false)
        transactionsViewModel = ViewModelProvider(requireActivity())[TransactionsViewModel::class.java]
        signOutAlertDialog = createSignOutDialog()
        fragmentHomeBinding.currentDate = UtilitiesFunctions.timestampToDate(System.currentTimeMillis())
        latestTransactionAdapter = LatestTransactionsAdapter()
        budgetGoalAdapter = BudgetGoalAdapter()

        lifecycleScope.launch {
            authViewModel.getCurrentUserName()
            authViewModel.getCurrentUserNameLiveData().observe(viewLifecycleOwner){ name ->
                fragmentHomeBinding.name = "Welcome, $name"
            }
        }

        lifecycleScope.launch {
            transactionsViewModel.getTransactionsTotalAmount()
            transactionsViewModel.getTransactionsTotalAmountLiveData().observe(viewLifecycleOwner){ totalTransaction ->
                fragmentHomeBinding.tvBalanceAmountHome.text = "$%.2f".format(totalTransaction)
                totalBalance = totalTransaction
                showPercentageSpent()
            }
        }

        lifecycleScope.launch {
            budgetsViewModel.getTotalBudget()
            budgetsViewModel.getTotalBudgetLiveData().observe(viewLifecycleOwner){ totalBudg ->
                fragmentHomeBinding.totalBudgetHome.text = "Total Budget: $%.2f".format(totalBudg)
                totalBudget = totalBudg
                showPercentageSpent()
                categoryBudgetsViewModel.getCategoryBudgets()
            }
        }

        lifecycleScope.launch {
            transactionsViewModel.getLatestTransactionsList()
            transactionsViewModel.getLatestTransactionsLiveData().observe(viewLifecycleOwner){ latestTransactions ->
                latestTransactionAdapter.replaceAll(latestTransactions)
            }
        }

        lifecycleScope.launch{
            categoryBudgetsViewModel.getCategoryBudgetsLiveData().observe(viewLifecycleOwner){ categoryBudgetsMap ->
                totalCategoryBudgets = categoryBudgetsMap
                transactionsViewModel.getCategoryTransactionsTotal()
            }
        }

        lifecycleScope.launch{
            transactionsViewModel.getCategoryTransactionsTotalLiveData().observe(viewLifecycleOwner){ categoryTransactionTotalList ->
                totalCategoryTransactions = categoryTransactionTotalList
                showBudgetGoalItems()
            }
        }

        val recentTransactionsLinearLayout = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val budgetGoalLinearLayout = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentHomeBinding.rvBudgetGoalItems.layoutManager = budgetGoalLinearLayout
        fragmentHomeBinding.rvRecentTransactionsHome.layoutManager = recentTransactionsLinearLayout
        fragmentHomeBinding.rvRecentTransactionsHome.adapter = latestTransactionAdapter
        fragmentHomeBinding.rvBudgetGoalItems.adapter = budgetGoalAdapter

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHomeBinding.signOutButton.setOnClickListener {
            signOutAlertDialog.show()
        }

        fragmentHomeBinding.seeAllRecentTransactionsHome.setOnClickListener {
            mainActivity.switchToTab(1)
        }

        authViewModel.signOutLiveData().observe(viewLifecycleOwner){ signedOut ->
            if(signedOut){
                mainActivity.hideTabBarVisibility()
                signOutAlertDialog.dismiss()
                UtilitiesFunctions.createSimpleSnackbar(fragmentHomeBinding.root, "Signed Out Successfully",
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!, Snackbar.LENGTH_LONG, requireContext(), true).show()
                UtilitiesFunctions.replaceFragment(requireActivity(), LoginFragment(), R.id.relativeLayoutMainActivity, false)
            }
        }
    }

    private fun createSignOutDialog(): AlertDialog{
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)

        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.custom_signout_alert_dialog, null)
        dialogBuilder.setView(dialogView)

        val cancelButton = dialogView.findViewById<Button>(R.id.btnSignOutCancel)
        val confirmButton = dialogView.findViewById<Button>(R.id.btnSignOutConfirm)
        val alertDialog: AlertDialog = dialogBuilder.create()

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        confirmButton.setOnClickListener {
            authViewModel.signOut()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        return alertDialog
    }

    private fun showPercentageSpent(){
        if(totalBalance != -1f && totalBudget != -1f){
            val spentPercentage = UtilitiesFunctions.calculateTotalPercentage(totalBalance, totalBudget) * 100
            if(spentPercentage > 100){
                fragmentHomeBinding.progressBarHome.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
                fragmentHomeBinding.spentTextHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
                fragmentHomeBinding.tvBalanceAmountHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
                fragmentHomeBinding.progressBarHome.setProgressCompat(100, true)
            } else{
                fragmentHomeBinding.progressBarHome.setIndicatorColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
                fragmentHomeBinding.spentTextHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                fragmentHomeBinding.tvBalanceAmountHome.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                fragmentHomeBinding.progressBarHome.setProgressCompat(spentPercentage.toInt(), true)
            }
            fragmentHomeBinding.spentTextHome.text =  "You have spent ${spentPercentage.toInt()}% of your monthly budget"

        }
    }

    private fun showBudgetGoalItems(){
        if(totalCategoryTransactions.isNotEmpty() && totalCategoryBudgets.isNotEmpty()){
            totalCategoryTransactions.sortBy { item -> UtilitiesFunctions.getCategoryBudgetsPosition(item.categoryName!!) }
            val listOfBudgetGoalItems: MutableList<BudgetGoalData> = mutableListOf()
            for(i  in 0..5){
                val categoryBudget = totalCategoryBudgets.getValue(i)
                val categoryTransaction = totalCategoryTransactions[i]
                val budgetGoalItem = BudgetGoalData(categoryBudget.categoryName, categoryBudget?.totalAmount!!.toFloat(),
                    categoryTransaction?.totalAmount!!.toFloat())
                listOfBudgetGoalItems.add(budgetGoalItem)
            }
            budgetGoalAdapter.replaceAll(listOfBudgetGoalItems)
        }
    }
}