package com.example.app_expenses.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.databinding.FragmentBudgetBinding
import com.example.app_expenses.utils.PrefsHelper
import com.example.app_expenses.utils.StringUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth
private lateinit var fragmentBudgetBinding: FragmentBudgetBinding
private lateinit var mainActivity: MainActivity

class BudgetFragment: Fragment() {

    init {
        mainActivity = MainActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentBudgetBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_budget,
            container, false)
        auth = Firebase.auth
        return fragmentBudgetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBudgetBinding.addNewBudgetButton.setOnClickListener {
            UtilitiesFunctions.replaceFragment(requireActivity(), AddBudgetFragment(), R.id.relativeLayoutMainActivity)
            mainActivity.hideTabBarVisibility()
        }
        fragmentBudgetBinding.tvBudgetAmount.text = "$%.2f".format(PrefsHelper.readFloat(StringUtils.TOTAL_BUDGET))
        PrefsHelper.onBudgetChangeListener(fragmentBudgetBinding.tvBudgetAmount)


    }
}