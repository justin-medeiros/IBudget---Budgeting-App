package com.example.app_expenses.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.app_expenses.R
import com.example.app_expenses.databinding.FragmentSignUpBinding

private lateinit var fragmentSignUpBinding: FragmentSignUpBinding

class SignUpFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentSignUpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up,
            container, false)
        return fragmentSignUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentSignUpBinding.tvSignIn.setOnClickListener {
            replaceFragment()
        }
    }

    private fun replaceFragment(){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutSplashScreen, LoginFragment())
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}