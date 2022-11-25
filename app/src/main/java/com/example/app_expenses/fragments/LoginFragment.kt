package com.example.app_expenses.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.databinding.FragmentLoginBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel

class LoginFragment: Fragment() {
    private lateinit var password: String
    private lateinit var email: String
    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private var forgetPasswordSent = false
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,
            container, false)

        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authViewModel.getSignInAuthLiveData().observe(viewLifecycleOwner){ isSuccessful ->
            if(isSuccessful){
                replaceActivity()
            } else{
                Toast.makeText(context, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
                invalidUI()
            }
        }

        fragmentLoginBinding.tvSignUp.setOnClickListener {
            UtilitiesFunctions.replaceFragment(requireActivity(), SignUpFragment(), R.id.frameLayoutSplashScreen, false)
        }

        fragmentLoginBinding.btnLogin.setOnClickListener {
            fragmentLoginBinding.tvLoginEmail.clearFocus()
            fragmentLoginBinding.tvLoginPassword.clearFocus()
            loginUser()
        }
        fragmentLoginBinding.tvLoginForgotPassword.setOnClickListener {
            ForgetPasswordFragment().show(childFragmentManager, "")
        }
    }

    private fun replaceActivity(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun loginUser(){
        email = fragmentLoginBinding.tvLoginEmail.text.toString()
        password = fragmentLoginBinding.tvLoginPassword.text.toString()
        authViewModel.signInAuth(email, password)
    }

    private fun invalidUI(){
        fragmentLoginBinding.tvInvalidLogin.visibility = View.VISIBLE

        fragmentLoginBinding.loginEmailTextInput.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
        fragmentLoginBinding.loginEmailTextInput.defaultHintTextColor = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.red_bright))
        fragmentLoginBinding.loginEmailTextInput.setStartIconTintList(
            ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.red_bright)))
        fragmentLoginBinding.tvLoginEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))

        fragmentLoginBinding.loginPasswordTextInput.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
        fragmentLoginBinding.loginPasswordTextInput.defaultHintTextColor = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.red_bright))
        fragmentLoginBinding.loginPasswordTextInput.setStartIconTintList(
            ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.red_bright)))
        fragmentLoginBinding.tvLoginEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))

        resetInvalidFields()
    }

    private fun resetInvalidFields(){
        fragmentLoginBinding.tvLoginEmail.setOnFocusChangeListener { _, _ ->
            resetUI()
        }

        fragmentLoginBinding.tvLoginPassword.setOnFocusChangeListener { _, _ ->
            resetUI()
        }
    }

    private fun resetUI(){
        fragmentLoginBinding.tvInvalidLogin.visibility = View.GONE
        fragmentLoginBinding.loginPasswordTextInput.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
        fragmentLoginBinding.loginPasswordTextInput.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        fragmentLoginBinding.loginPasswordTextInput.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
        fragmentLoginBinding.tvLoginPassword.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        fragmentLoginBinding.tvInvalidLogin.visibility = View.GONE
        fragmentLoginBinding.loginEmailTextInput.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
        fragmentLoginBinding.loginEmailTextInput.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        fragmentLoginBinding.loginEmailTextInput.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
        fragmentLoginBinding.tvLoginEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

    }
}
