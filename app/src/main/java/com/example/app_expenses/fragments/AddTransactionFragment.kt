package com.example.app_expenses.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.adapters.CategoryAddTransactionAdapter
import com.example.app_expenses.databinding.FragmentAddTransactionBinding
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddTransactionFragment: BottomSheetDialogFragment() {
    private lateinit var fragmentAddTransactionBinding: FragmentAddTransactionBinding
    private lateinit var categoryAddTransactionAdapter: CategoryAddTransactionAdapter
    private lateinit var categories: MutableList<CategoryEnum>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setFullHeight(bottomSheetDialog)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAddTransactionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_transaction,
            container, false)
        categories = UtilitiesFunctions.createCategories()
        categoryAddTransactionAdapter = CategoryAddTransactionAdapter(categories)
        fragmentAddTransactionBinding.rvAddTransaction.adapter = categoryAddTransactionAdapter
        fragmentAddTransactionBinding.rvAddTransaction.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        return fragmentAddTransactionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentAddTransactionBinding.addTransactionCloseButton.setOnClickListener{
            dialog?.dismiss()
        }

        fragmentAddTransactionBinding.addNewTransactionButton.setOnClickListener {
            if(validateFields() && categoryAddTransactionAdapter.isCategorySelected.value == true){
                dialog?.dismiss()
            }
        }
    }

    private fun setFullHeight(bottomSheetDialog: BottomSheetDialog){
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet) as FrameLayout
        val behaviour = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        if(layoutParams != null){
            layoutParams.height =  Resources.getSystem().displayMetrics.heightPixels - 50
        }
        bottomSheet.layoutParams = layoutParams
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun validateFields(): Boolean{
        var isValidName = false
        var isValidAmount = false
        if(TextUtils.isEmpty(fragmentAddTransactionBinding.etTransactionName.text.toString())){
            fragmentAddTransactionBinding.tvInvalidTransactionName.visibility = View.VISIBLE
            fragmentAddTransactionBinding.etTransactionName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddTransactionBinding.etTransactionName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddTransactionBinding.etTransactionName.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                requireContext(), R.color.red_bright))
            fragmentAddTransactionBinding.tvTransactionNameTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }
        else{
            isValidName = true
        }

        if (TextUtils.isEmpty(fragmentAddTransactionBinding.etTransactionAmount.text.toString()) || !UtilitiesFunctions.validateAmount(
                fragmentAddTransactionBinding.etTransactionAmount.text.toString())) {
            fragmentAddTransactionBinding.tvInvalidAddTransactionAmount.visibility = View.VISIBLE
            fragmentAddTransactionBinding.etTransactionAmount.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddTransactionBinding.etTransactionAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddTransactionBinding.addTransactionAmountContainer.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(), R.color.red_bright))
            fragmentAddTransactionBinding.transactionAmountTitleAddTransaction.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }else{
            isValidAmount = true
        }

        if(categoryAddTransactionAdapter.isCategorySelected.value == false){
            fragmentAddTransactionBinding.tvCategoryTitleAddTransaction.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentAddTransactionBinding.tvInvalidRvAddTransaction.visibility = View.VISIBLE
        }

        resetInvalidFields()

        return isValidAmount && isValidName
    }

    private fun resetInvalidFields(){
        fragmentAddTransactionBinding.etTransactionName.setOnFocusChangeListener { _, _ ->
            fragmentAddTransactionBinding.tvInvalidTransactionName.visibility = View.GONE
            fragmentAddTransactionBinding.etTransactionName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary_50))
            fragmentAddTransactionBinding.etTransactionName.setTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
            fragmentAddTransactionBinding.etTransactionName.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(), R.color.foreground_primary))
            fragmentAddTransactionBinding.tvTransactionNameTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_secondary))
        }

        fragmentAddTransactionBinding.etTransactionAmount.setOnFocusChangeListener { _, _ ->
            fragmentAddTransactionBinding.tvInvalidAddTransactionAmount.visibility = View.GONE
            fragmentAddTransactionBinding.etTransactionAmount.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary_50))
            fragmentAddTransactionBinding.etTransactionAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary))
            fragmentAddTransactionBinding.addTransactionAmountContainer.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(), R.color.background_secondary))
            fragmentAddTransactionBinding.transactionAmountTitleAddTransaction.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_secondary))
        }

        lifecycleScope.launch(Dispatchers.Main){
            categoryAddTransactionAdapter.isCategorySelected.observe(requireActivity()){ selected ->
                if(selected){
                    fragmentAddTransactionBinding.tvInvalidRvAddTransaction.visibility = View.GONE
                    fragmentAddTransactionBinding.tvCategoryTitleAddTransaction.setTextColor(ContextCompat.getColor(requireContext(), R.color.background_secondary))
                }
            }
        }

    }
}