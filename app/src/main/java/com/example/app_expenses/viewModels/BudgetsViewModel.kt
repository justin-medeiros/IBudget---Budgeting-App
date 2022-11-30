package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.repositories.BudgetsRepository

class BudgetsViewModel: ViewModel() {
    private val budgetsRepository = BudgetsRepository()
    private val myBudgetsLiveData: LiveData<List<BudgetData>> = budgetsRepository.getMyBudgetsLiveData()
    private val addBudgetsLiveData: LiveData<BudgetData?> = budgetsRepository.getAddBudgetLiveData()
    private val totalBudgetLiveData: LiveData<Float> = budgetsRepository.getTotalBudgetLiveData()

    fun getMyBudgets(){
        budgetsRepository.getMyBudgets()
    }

    fun getMyBudgetsLiveData(): LiveData<List<BudgetData>>{
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

    fun addToTotalBudget(budgetAmount: Float){
        budgetsRepository.addToTotalBudget(budgetAmount)
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