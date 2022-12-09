package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.repositories.TransactionsRepository

class TransactionsViewModel: ViewModel() {
    private val transactionsRepository = TransactionsRepository()
    private val addTransactionLiveData = transactionsRepository.getAddTransactionLiveData()
    private val allTransactionLiveData = transactionsRepository.getMyTransactionsLiveData()

    fun getMyTransactions(){
        transactionsRepository.getMyTransactions()
    }

    fun getMyTransactionsLiveData(): LiveData<List<TransactionData>>{
        return allTransactionLiveData
    }

    fun addTransaction(transactionData: TransactionData){
        transactionsRepository.addTransaction(transactionData)
    }

    fun getAddTransactionLiveData(): LiveData<TransactionData?>{
        return addTransactionLiveData
    }
}