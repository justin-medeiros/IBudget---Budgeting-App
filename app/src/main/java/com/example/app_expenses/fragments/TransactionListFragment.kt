package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.adapters.TransactionListAdapter
import com.example.app_expenses.databinding.FragmentTransactionListBinding
import com.example.app_expenses.viewModels.TransactionsViewModel

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

        transactionViewModel.getMyTransactionsLiveData().observe(viewLifecycleOwner){ transactionsList ->
            if(transactionsList.isEmpty()){
                fragmentTransactionListBinding.rvTransactionList.visibility = View.GONE
                fragmentTransactionListBinding.tvRvNoTransactions.visibility = View.VISIBLE
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.isEnabled = false
            } else{
                myTransactionsListAdapter.addAllItems(transactionsList)
            }
        }

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

        transactionViewModel.getAddTransactionLiveData().observe(viewLifecycleOwner){ transactionData ->
            myTransactionsListAdapter.addItem(transactionData!!, 0)
            fragmentTransactionListBinding.rvTransactionList.visibility = View.VISIBLE
            fragmentTransactionListBinding.tvRvNoTransactions.visibility = View.GONE
            fragmentTransactionListBinding.tvSelectToDeleteTransactionList.isEnabled = true
        }

        transactionViewModel.numberOfTransactions.observe(viewLifecycleOwner){ numberOfTransactions ->
            if(numberOfTransactions > 0){
                fragmentTransactionListBinding.rvTransactionList.visibility = View.VISIBLE
                fragmentTransactionListBinding.tvRvNoTransactions.visibility = View.GONE
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.isEnabled = true
            } else{
                fragmentTransactionListBinding.rvTransactionList.visibility = View.GONE
                fragmentTransactionListBinding.tvRvNoTransactions.visibility = View.VISIBLE
                fragmentTransactionListBinding.tvSelectToDeleteTransactionList.isEnabled = false
            }
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