package com.example.app_expenses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.app_expenses.MainActivity
import com.example.app_expenses.R
import com.example.app_expenses.databinding.FragmentAddBudgetBinding
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
}