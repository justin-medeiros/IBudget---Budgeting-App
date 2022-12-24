package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.repositories.TransactionsRepository
import java.sql.Timestamp

class TransactionsViewModel: ViewModel() {
    private val transactionsRepository = TransactionsRepository()
    private val addTransactionLiveData = transactionsRepository.getAddTransactionLiveData()
    private val allTransactionsLiveData = transactionsRepository.getMyTransactionsLiveData()
    private val getTransactionTotalLiveData = transactionsRepository.getTransactionsTotalAmountLiveData()
    private val getCategoryTransactionTotalLiveData = transactionsRepository.getCategoryTransactionsTotalLiveData()
    private val getLatestTransactionsLiveData = transactionsRepository.getLatestTransactionsLiveData()

    val selectButtonClicked = MutableLiveData<Boolean>()
    val amountOfItemsSelected = MutableLiveData<Int>()
    val deleteButtonClicked = MutableLiveData<Boolean>()
    val numberOfTransactions = MutableLiveData<Int>()

    fun getMyTransactions(){
        transactionsRepository.getMyTransactions()
    }

    fun getMyTransactionsLiveData(): LiveData<List<TransactionData>>{
        return allTransactionsLiveData
    }

    fun addTransaction(transactionData: TransactionData){
        transactionsRepository.addTransaction(transactionData)
    }

    fun getAddTransactionLiveData(): LiveData<TransactionData?>{
        return addTransactionLiveData
    }

    fun removeTransactions(listOfTransactions: MutableCollection<TransactionData>){
        transactionsRepository.removeTransactions(listOfTransactions)
    }

    fun addAllTransactions(listOfTransactions: MutableCollection<TransactionData>){
        transactionsRepository.addAllTransactions(listOfTransactions)
    }

    fun addToTransactionsTotal(transactionAmount: Float, transactionMonth: String){
        transactionsRepository.addToTransactionsTotalAmount(transactionAmount, transactionMonth)
    }

    fun subtractFromTransactionsTotal(transactionAmount: Float, transactionMonth: String){
        transactionsRepository.subtractFromTransactionsTotalAmount(transactionAmount, transactionMonth)
    }

    fun addToCategoryTransactionsTotal(categoryName: String, transactionDataAmount: Float){
        transactionsRepository.addToCategoryTransactionsTotal(categoryName, transactionDataAmount)
    }

    fun subtractFromCategoryTransactionsTotal(categoryName: String, transactionDataAmount: Float){
        transactionsRepository.subtractFromCategoryTransactionsTotal(categoryName, transactionDataAmount)
    }

    fun getTransactionsTotalAmount(){
        transactionsRepository.getTransactionsTotalAmount()
    }

    fun getTransactionsTotalAmountLiveData(): LiveData<Float>{
        return getTransactionTotalLiveData
    }

    fun getCategoryTransactionsTotal(){
        transactionsRepository.getCategoryTransactionsTotal()
    }

    fun getCategoryTransactionsTotalLiveData(): LiveData<ArrayList<CategoryData>>{
        return getCategoryTransactionTotalLiveData
    }

    fun getLatestTransactionsList(){
        transactionsRepository.getLatestTransactions()
    }

    fun getLatestTransactionsLiveData(): LiveData<List<TransactionData>>{
        return getLatestTransactionsLiveData
    }
}