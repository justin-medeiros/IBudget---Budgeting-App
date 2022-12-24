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
import com.example.app_expenses.adapters.LatestTransactionAdapter
import com.example.app_expenses.databinding.FragmentHomeBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel
import com.example.app_expenses.viewModels.BudgetsViewModel
import com.example.app_expenses.viewModels.CategoryBudgetsViewModel
import com.example.app_expenses.viewModels.TransactionsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class HomeFragment: Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var signOutAlertDialog: AlertDialog
    private lateinit var latestTransactionAdapter: LatestTransactionAdapter
    private lateinit var transactionsViewModel: TransactionsViewModel
    private val mainActivity = MainActivity()
    private val authViewModel: AuthViewModel by viewModels()
    private val budgetsViewModel: BudgetsViewModel by viewModels()

    private var totalBalance = -1f
    private var totalBudget = -1f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
            container, false)
        transactionsViewModel = ViewModelProvider(requireActivity())[TransactionsViewModel::class.java]
        signOutAlertDialog = createSignOutDialog()
        fragmentHomeBinding.currentDate = UtilitiesFunctions.timestampToDate(System.currentTimeMillis())
        latestTransactionAdapter = LatestTransactionAdapter()
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
            }
        }

        lifecycleScope.launch {
            transactionsViewModel.getLatestTransactionsList()
            transactionsViewModel.getLatestTransactionsLiveData().observe(viewLifecycleOwner){ latestTransactions ->
                latestTransactionAdapter.replaceAll(latestTransactions)
            }
        }

        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentHomeBinding.rvRecentTransactionsHome.layoutManager = myLinearLayoutManager
        fragmentHomeBinding.rvRecentTransactionsHome.adapter = latestTransactionAdapter

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
            val spentPercentage = (totalBalance / totalBudget) * 100
            fragmentHomeBinding.spentTextHome.text =  "You have spent %.1f%% of your monthly budget".format(spentPercentage)
            if(spentPercentage >= 100){
                fragmentHomeBinding.progressBarHome.setProgressCompat(100, true)
            } else{
                fragmentHomeBinding.progressBarHome.setProgressCompat(spentPercentage.toInt(), true)
            }

        }
    }



}