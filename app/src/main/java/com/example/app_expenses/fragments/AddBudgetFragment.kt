package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.CategoryAdapter
import com.example.app_expenses.adapters.BudgetsAdapter
import com.example.app_expenses.data.BudgetCategoryData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.databinding.FragmentAddBudgetBinding
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.PrefsHelper
import com.example.app_expenses.utils.StringUtils
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.BudgetsViewModel
import com.example.app_expenses.viewModels.CategoryBudgetsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddBudgetFragment(private val budgetsAdapter: BudgetsAdapter): Fragment() {
    private lateinit var fragmentAddBudgetBinding: FragmentAddBudgetBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categories: MutableList<CategoryEnum>
    private var selectedCategoryName: String? = ""
    private var mainActivity: MainActivity = MainActivity()
    private val budgetsViewModel: BudgetsViewModel by viewModels()
    private val categoryBudgetsViewModel: CategoryBudgetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentAddBudgetBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_budget,
            container, false
        )
        categories = UtilitiesFunctions.createCategories()
        categoryAdapter = CategoryAdapter(categories)
        fragmentAddBudgetBinding.recyclerViewAddBudget.adapter = categoryAdapter
        fragmentAddBudgetBinding.recyclerViewAddBudget.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        return fragmentAddBudgetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        budgetsViewModel.getAddBudgetLiveData().observe(viewLifecycleOwner){ newBudget ->
            if(newBudget != null){
                val total = newBudget.budgetAmount!!.toFloat() + PrefsHelper.readFloat(StringUtils.TOTAL_BUDGET)!!
                PrefsHelper.writeFloat(StringUtils.TOTAL_BUDGET, total)
                budgetsViewModel.addToTotalBudget(newBudget.budgetAmount!!.toFloat())
                categoryBudgetsViewModel.getCategoryTotalBudget(newBudget.categoryName!!)
                Toast.makeText(context, "Budget has been created successfully!", Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(context, "Error. User not registered.", Toast.LENGTH_LONG).show()
            }
        }

        categoryBudgetsViewModel.getCategoryTotalBudgetLiveData().observe(viewLifecycleOwner){ categoryTotalBudget ->
            val position = UtilitiesFunctions.getCategoryBudgetsPosition(categoryTotalBudget.categoryName!!)
            budgetsAdapter.replaceBudgetAmount(position, categoryTotalBudget.budgetAmount!!)
            parentFragmentManager.popBackStack()
            mainActivity.visibleTabBarVisibility()
        }

        fragmentAddBudgetBinding.addBudgetBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
            mainActivity.visibleTabBarVisibility()
        }

        fragmentAddBudgetBinding.btnConfirmAddBudget.setOnClickListener {
            fragmentAddBudgetBinding.etBudgetName.clearFocus()
            fragmentAddBudgetBinding.etAmount.clearFocus()
            if(categoryAdapter.isCategorySelected.value == true){
                selectedCategoryName = categories[categoryAdapter.rowIndex].categoryName
            }
            categoryBudgetsViewModel.validateBudgetNames(selectedCategoryName!!)
        }

        // Used to validate all fields and make sure that the budget name is unique.
        categoryBudgetsViewModel.getBudgetsListLiveData().observe(viewLifecycleOwner){ budgetList ->
            val budgetName = fragmentAddBudgetBinding.etBudgetName.text.toString()
            if(validateFields(budgetList.contains(budgetName.lowercase())) && categoryAdapter.isCategorySelected.value == true){
                val budgetAmount = fragmentAddBudgetBinding.etAmount.text.toString()
                val newBudget = BudgetData(System.currentTimeMillis(), selectedCategoryName, budgetName, budgetAmount)
                budgetsViewModel.addBudget(newBudget)
                categoryBudgetsViewModel.addToCategoryTotalBudget(BudgetCategoryData(selectedCategoryName, budgetAmount))
            }
        }
    }

    private fun validateFields(isEmailUnique: Boolean): Boolean{
        var isValidName = false
        var isValidAmount = false
        if(TextUtils.isEmpty(fragmentAddBudgetBinding.etBudgetName.text.toString())){
            fragmentAddBudgetBinding.tvInvalidBudgetName.visibility = View.VISIBLE
            fragmentAddBudgetBinding.etBudgetName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.etBudgetName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.etBudgetName.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.tvBudgetNameTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddBudgetBinding.tvInvalidBudgetName.text = resources.getString(R.string.invalid_budget_name)
        } else if (isEmailUnique){
            invalidBudgetName()
        }
        else{
            isValidName = true
        }

        if (TextUtils.isEmpty(fragmentAddBudgetBinding.etAmount.text.toString()) || !UtilitiesFunctions.validateAmount(
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

    private fun invalidBudgetName(){
        fragmentAddBudgetBinding.tvInvalidBudgetName.visibility = View.VISIBLE
        fragmentAddBudgetBinding.etBudgetName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        fragmentAddBudgetBinding.etBudgetName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        fragmentAddBudgetBinding.etBudgetName.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(), R.color.red_bright))
        fragmentAddBudgetBinding.tvBudgetNameTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        fragmentAddBudgetBinding.tvInvalidBudgetName.text = resources.getString(R.string.duplicate_budget_name)
        return
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