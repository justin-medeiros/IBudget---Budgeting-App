package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.data.MyBudgetData
import com.example.app_expenses.repositories.BudgetsRepository

class BudgetsViewModel: ViewModel() {
    private val budgetsRepository = BudgetsRepository()
    private val myBudgetsLiveData: LiveData<List<MyBudgetData>> = budgetsRepository.getMyBudgetsLiveData()
    private val addBudgetsLiveData: LiveData<BudgetData?> = budgetsRepository.getAddBudgetLiveData()
    private val totalBudgetLiveData: LiveData<Float> = budgetsRepository.getTotalBudgetLiveData()

    fun getMyBudgets(){
        budgetsRepository.getMyBudgets()
    }

    fun getMyBudgetsLiveData(): LiveData<List<MyBudgetData>>{
        return myBudgetsLiveData
    }

    fun addBudget(newBudget: BudgetData){
        budgetsRepository.addBudget(newBudget)
    }

    fun getAddBudgetLiveData(): LiveData<BudgetData?>{
        return addBudgetsLiveData
    }

    fun removeBudget(budgetName: String){
        budgetsRepository.removeBudget(budgetName)
    }

    fun addToTotalBudget(){
        budgetsRepository.addToTotalBudget()
    }

    fun removeFromTotalBudget(){
        budgetsRepository.removeFromTotalBudget()
    }

    fun getTotalBudget(){
        budgetsRepository.getTotalBudget()
    }

    fun getTotalBudgetLiveData(): LiveData<Float>{
        return totalBudgetLiveData
    }
}