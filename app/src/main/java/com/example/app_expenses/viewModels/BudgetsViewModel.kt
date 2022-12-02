package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.repositories.BudgetsRepository

class BudgetsViewModel: ViewModel() {
    private val budgetsRepository = BudgetsRepository()
    private val myBudgetsLiveData: LiveData<List<BudgetData>> = budgetsRepository.getMyBudgetsLiveData()
    private val addBudgetsLiveData: LiveData<BudgetData?> = budgetsRepository.getAddBudgetLiveData()
    private val totalBudgetLiveData: LiveData<Float> = budgetsRepository.getTotalBudgetLiveData()

    fun getMyBudgets(category: CategoryEnum){
        budgetsRepository.getMyBudgets(category)
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

    fun removeBudget(budgetName: String, categoryName: String){
        budgetsRepository.removeBudget(budgetName, categoryName)
    }

    fun removeFromTotalBudget(budgetAmount: Float){
        budgetsRepository.removeFromTotalBudget(budgetAmount)
    }

    fun addToTotalBudget(budgetAmount: Float){
        budgetsRepository.addToTotalBudget(budgetAmount)
    }

    fun getTotalBudget(){
        budgetsRepository.getTotalBudget()
    }

    fun getTotalBudgetLiveData(): LiveData<Float>{
        return totalBudgetLiveData
    }
}