package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.TransactionListAdapter
import com.example.app_expenses.databinding.FragmentTransactionListBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.TransactionsViewModel
import com.google.android.material.snackbar.Snackbar

class TransactionListFragment: Fragment() {
    private lateinit var fragmentTransactionListBinding: FragmentTransactionListBinding
    private lateinit var myTransactionsListAdapter: TransactionListAdapter
    private lateinit var transactionViewModel: TransactionsViewModel
    private var selectTvCLicked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentTransactionListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_list,
            container, false)
        myTransactionsListAdapter = TransactionListAdapter(requireActivity())
        transactionViewModel = ViewModelProvider(requireActivity())[TransactionsViewModel::class.java]
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

        transactionViewModel.getAddTransactionLiveData().observe(viewLifecycleOwner){ transactionData ->
            myTransactionsListAdapter.addItem(transactionData!!, 0)
        }

        transactionViewModel.deleteButtonClicked.observe(viewLifecycleOwner){ clicked ->
            if(clicked && transactionViewModel.amountOfItemsSelected.value ?: 0 > 0){
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.text = "Select"
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                selectTvCLicked = false
                transactionViewModel.selectButtonClicked.postValue(false)
            }
        }

        fragmentTransactionListBinding.tvSelectToDeleteTransactionList.setOnClickListener {
            if(!selectTvCLicked){
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.text = "Cancel All"
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.backgroundTintList =
                    ColorStateList.valueOf(Color.TRANSPARENT)
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.background_tertiary))
            } else{
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.text = "Select"
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            selectTvCLicked = !selectTvCLicked
            transactionViewModel.selectButtonClicked.postValue(selectTvCLicked)
        }
    }
}