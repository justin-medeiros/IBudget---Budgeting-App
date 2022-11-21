package com.example.app_expenses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.MainActivity
import com.example.app_expenses.R
import com.example.app_expenses.adapters.CategoryAdapter
import com.example.app_expenses.data.Category
import com.example.app_expenses.databinding.FragmentAddBudgetBinding
import com.example.app_expenses.enums.CategoryEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private lateinit var auth: FirebaseAuth
private lateinit var fragmentAddBudgetBinding: FragmentAddBudgetBinding
private lateinit var mainActivity: MainActivity

class AddBudgetFragment: Fragment() {

    init {
        mainActivity = MainActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentAddBudgetBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_budget,
            container, false
        )
        auth = Firebase.auth
        val categories = createCategories()
        val categoryAdapter = CategoryAdapter(categories)
        fragmentAddBudgetBinding.recyclerViewAddBudget.adapter = categoryAdapter
        fragmentAddBudgetBinding.recyclerViewAddBudget.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        return fragmentAddBudgetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentAddBudgetBinding.addBudgetBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
            mainActivity.visibleTabBarVisibility()
        }

        fragmentAddBudgetBinding.btnConfirmAddBudget.setOnClickListener {
            parentFragmentManager.popBackStack()
            mainActivity.visibleTabBarVisibility()
        }

    }

    private fun createCategories(): MutableList<Category>{
        val categories = mutableListOf<Category>()
        categories.add(Category(CategoryEnum.GROCERIES.categoryName, CategoryEnum.GROCERIES.categoryColor, CategoryEnum.GROCERIES.categoryIcon))
        categories.add(Category(CategoryEnum.ENTERTAINMENT.categoryName, CategoryEnum.ENTERTAINMENT.categoryColor, CategoryEnum.ENTERTAINMENT.categoryIcon))
        categories.add(Category(CategoryEnum.TRANSPORTATION.categoryName, CategoryEnum.TRANSPORTATION.categoryColor, CategoryEnum.TRANSPORTATION.categoryIcon))
        categories.add(Category(CategoryEnum.SUBSCRIPTIONS.categoryName, CategoryEnum.SUBSCRIPTIONS.categoryColor, CategoryEnum.SUBSCRIPTIONS.categoryIcon))
        categories.add(Category(CategoryEnum.BILLS.categoryName, CategoryEnum.BILLS.categoryColor, CategoryEnum.BILLS.categoryIcon))
        categories.add(Category(CategoryEnum.PERSONAL_SPENDING.categoryName, CategoryEnum.PERSONAL_SPENDING.categoryColor, CategoryEnum.PERSONAL_SPENDING.categoryIcon))
        return categories
    }


}