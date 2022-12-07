package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.repositories.TransactionsRepository

class TransactionsViewModel: ViewModel() {
    private val transactionsRepository = TransactionsRepository()
    private val addTransactionLiveData = transactionsRepository.getAddTransactionLiveData()

    fun addTransaction(transactionData: TransactionData){
        transactionsRepository.addTransaction(transactionData)
    }

    fun getAddTransactionLiveData(): LiveData<TransactionData?>{
        return addTransactionLiveData
    }
}