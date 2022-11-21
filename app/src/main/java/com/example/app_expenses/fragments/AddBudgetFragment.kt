package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private lateinit var auth: FirebaseAuth
private lateinit var fragmentAddBudgetBinding: FragmentAddBudgetBinding
private lateinit var mainActivity: MainActivity
private lateinit var categoryAdapter: CategoryAdapter

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
        categoryAdapter = CategoryAdapter(categories)
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
            fragmentAddBudgetBinding.etBudgetName.clearFocus()
            fragmentAddBudgetBinding.etAmount.clearFocus()
            if(validateFields() && categoryAdapter.isCategorySelected.value == true){
                parentFragmentManager.popBackStack()
                mainActivity.visibleTabBarVisibility()
            }
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

    private fun validateFields(): Boolean{
        var isValidName = false
        var isValidAmount = false
        if(TextUtils.isEmpty(fragmentAddBudgetBinding.etBudgetName.text.toString())){
            fragmentAddBudgetBinding.tvInvalidBudgetName.visibility = View.VISIBLE
            fragmentAddBudgetBinding.etBudgetName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.etBudgetName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.etBudgetName.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.tvBudgetNameTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        } else{
            isValidName = true
        }
        if (TextUtils.isEmpty(fragmentAddBudgetBinding.etAmount.text.toString()) || !validateAmount(
                fragmentAddBudgetBinding.etAmount.text.toString())) {
            fragmentAddBudgetBinding.tvInvalidBudgetAmount.visibility = View.VISIBLE
            fragmentAddBudgetBinding.etAmount.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.etAmount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.tvBudgetAmountTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }else{
            isValidAmount = true
        }

        if(categoryAdapter.isCategorySelected.value == false){
            fragmentAddBudgetBinding.tvInvalidCategorySelected.visibility = View.VISIBLE
        }

        resetInvalidFields()

        return isValidAmount && isValidName
    }

    private fun validateAmount(amount: String?): Boolean{
        amount?.let {
            val amountPattern = "^[0-9]+[.][0-9][0-9]\$"
            val amountMatcher = Regex(amountPattern)
            return amountMatcher.find(it) != null
        } ?: return false
    }

    private fun resetInvalidFields(){
        fragmentAddBudgetBinding.etBudgetName.setOnFocusChangeListener { _, _ ->
            fragmentAddBudgetBinding.tvInvalidBudgetName.visibility = View.GONE
            fragmentAddBudgetBinding.etBudgetName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.background_tertiary))
            fragmentAddBudgetBinding.etBudgetName.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentAddBudgetBinding.etBudgetName.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(), R.color.foreground_primary))
            fragmentAddBudgetBinding.tvBudgetNameTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
        }

        fragmentAddBudgetBinding.etAmount.setOnFocusChangeListener { _, _ ->
            fragmentAddBudgetBinding.tvInvalidBudgetAmount.visibility = View.GONE
            fragmentAddBudgetBinding.etAmount.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.background_tertiary))
            fragmentAddBudgetBinding.etAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentAddBudgetBinding.etAmount.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(), R.color.foreground_primary))
            fragmentAddBudgetBinding.tvBudgetAmountTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
        }

        lifecycleScope.launch(Dispatchers.Main){
            categoryAdapter.isCategorySelected.observe(requireActivity()){ selected ->
                    if(selected){
                        fragmentAddBudgetBinding.tvInvalidCategorySelected.visibility = View.GONE
                    }
            }
        }

    }


}