package com.example.app_expenses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.app_expenses.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var passwordText: TextInputEditText
private lateinit var emailText: TextInputEditText
private lateinit var fragmentLoginBinding: FragmentLoginBinding

class LoginFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,
            container, false)

        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentLoginBinding.tvSignUp.setOnClickListener {
            replaceFragment()
        }
    }
    private fun replaceFragment(){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutSplashScreen, SignUpFragment())
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}
