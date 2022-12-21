package com.example.app_expenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.app_expenses.data.BudgetCategoryData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.repositories.CategoryBudgetsRepository
import java.util.*

class CategoryBudgetsViewModel: ViewModel() {
    private val categoryBudgetsListRepository = CategoryBudgetsRepository()
    private val categoryTotalBudgetLiveData: LiveData<BudgetCategoryData> = categoryBudgetsListRepository.getCategoryTotalBudgetLiveData()
    private val latestBudgetsLiveData: LiveData<List<BudgetData>> = categoryBudgetsListRepository.getLatestBudgetsLiveData()
    private val categoryBudgetsLiveData:  LiveData<TreeMap<Int, BudgetCategoryData>> = categoryBudgetsListRepository.getCategoryBudgetsLiveData()
    private val budgetListLiveData: LiveData<List<String>> = categoryBudgetsListRepository.getBudgetListLiveData()

    fun getCategoryBudgets(){
        categoryBudgetsListRepository.getCategoryBudgets()
    }

    fun getCategoryBudgetsLiveData(): LiveData<TreeMap<Int, BudgetCategoryData>> {
        return categoryBudgetsLiveData
    }

    fun getLatestBudgets(){
        categoryBudgetsListRepository.getLatestCategoryBudgets()
    }

    fun getLatestBudgetsLiveData(): LiveData<List<BudgetData>>{
        return latestBudgetsLiveData
    }

    fun validateBudgetNames(categoryName: String){
        categoryBudgetsListRepository.validateUniqueBudgetName(categoryName)
    }

    fun getBudgetsListLiveData(): LiveData<List<String>>{
        return budgetListLiveData
    }

    fun subtractFromCategoryTotalBudget(budgetCategoryData: BudgetCategoryData){
        categoryBudgetsListRepository.subtractFromCategoryBudget(budgetCategoryData)
    }

    fun addToCategoryTotalBudget(budgetCategoryData: BudgetCategoryData){
        categoryBudgetsListRepository.addToCategoryBudget(budgetCategoryData)
    }

    fun getCategoryTotalBudget(categoryName: String){
        categoryBudgetsListRepository.getCategoryTotalBudget(categoryName)
    }

    fun getCategoryTotalBudgetLiveData(): LiveData<BudgetCategoryData>{
        return categoryTotalBudgetLiveData
    }

}