package com.example.app_expenses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.adapters.MyBudgetsListAdapter
import com.example.app_expenses.adapters.TransactionListAdapter
import com.example.app_expenses.databinding.FragmentTransactionListBinding
import com.example.app_expenses.viewModels.TransactionsViewModel

class TransactionListFragment: Fragment() {
    private lateinit var fragmentTransactionListBinding: FragmentTransactionListBinding
    private val transactionViewModel: TransactionsViewModel by viewModels()
    private var myTransactionsListAdapter = TransactionListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentTransactionListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_list,
            container, false)

        transactionViewModel.getMyTransactions()

        val categoriesArray = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categoriesArray)

        fragmentTransactionListBinding.tvAutoCompleteTransactionList.setAdapter(arrayAdapter)
        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentTransactionListBinding.rvTransactionList.layoutManager = myLinearLayoutManager
        fragmentTransactionListBinding.rvTransactionList.adapter = myTransactionsListAdapter
        return fragmentTransactionListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel.getMyTransactionsLiveData().observe(viewLifecycleOwner){ transactionsList ->

            myTransactionsListAdapter.addAllItems(transactionsList)
        }
    }
}