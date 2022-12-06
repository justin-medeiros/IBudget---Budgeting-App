package com.example.app_expenses.fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.adapters.CategoryAdapter
import com.example.app_expenses.adapters.CategoryAddTransactionAdapter
import com.example.app_expenses.databinding.FragmentAddTransactionBinding
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


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
}