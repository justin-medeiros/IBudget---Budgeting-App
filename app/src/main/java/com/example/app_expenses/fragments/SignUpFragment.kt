package com.example.app_expenses.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.databinding.FragmentSignUpBinding
import com.example.app_expenses.enums.SignUpEnum
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class SignUpFragment: Fragment() {
    private lateinit var fragmentSignUpBinding: FragmentSignUpBinding
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var progressBar: ProgressBar
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSignUpBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_up,
            container, false
        )
        return fragmentSignUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        authViewModel.getCreateUserLiveData().observe(viewLifecycleOwner){ userCreated ->
            progressBar.visibility = View.GONE
            when(userCreated){
                SignUpEnum.USER_CREATED -> {
                    UtilitiesFunctions.createSuccessSnackbar(view, "User has been registered successfully!", ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!,
                        Snackbar.LENGTH_LONG, requireContext(), true, true).show()
                    UtilitiesFunctions.replaceFragment(requireActivity(), OnboardingFragment(), R.id.loginRelativeLayout, false)
                }
                SignUpEnum.USER_NOT_CREATED ->{
                    UtilitiesFunctions.createSuccessSnackbar(view, "Error. User not created. Try again", ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_thick)!!,
                        Snackbar.LENGTH_LONG, requireContext(), true, false).show()
                }
                SignUpEnum.EMAIL_TAKEN ->{
                    emailIsTaken()
                }
            }
        }

        fragmentSignUpBinding.tvSignIn.setOnClickListener {
            UtilitiesFunctions.replaceFragment(requireActivity(), LoginFragment(),
                R.id.loginRelativeLayout,false)
        }

        fragmentSignUpBinding.btnSignUp.setOnClickListener {
            fragmentSignUpBinding.tvFirstName.clearFocus()
            fragmentSignUpBinding.tvLastName.clearFocus()
            fragmentSignUpBinding.tvEmailSignUp.clearFocus()
            fragmentSignUpBinding.tvPasswordSignUp.clearFocus()
            createProgressBar()
            if(validateFields()){
                userToDatabase()
            } else{
                UtilitiesFunctions.createSuccessSnackbar(requireView(), "Error. User not created. Try again", ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_thick)!!,
                    Snackbar.LENGTH_LONG, requireContext(), true, false).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun userToDatabase() {
        firstName = fragmentSignUpBinding.tvFirstName.text.toString()
        lastName = fragmentSignUpBinding.tvLastName.text.toString()
        email = fragmentSignUpBinding.tvEmailSignUp.text.toString()
        password = fragmentSignUpBinding.tvPasswordSignUp.text.toString()

        authViewModel.createUserAuth(firstName, lastName, email, password)
    }

    @SuppressLint("ResourceAsColor")
    private fun validateFields(): Boolean{
        var fieldsValid = true
        if(TextUtils.isEmpty(fragmentSignUpBinding.tvFirstName.text)){
            fieldsValid = false
            fragmentSignUpBinding.tvInvalidFirstName.visibility = View.VISIBLE
            fragmentSignUpBinding.tvFirstName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentSignUpBinding.inputLayoutFirstName.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutFirstName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
            fragmentSignUpBinding.tvFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }
        else{
            fragmentSignUpBinding.tvInvalidFirstName.visibility = View.GONE
        }

        if(TextUtils.isEmpty(fragmentSignUpBinding.tvLastName.text)){
            fieldsValid = false
            fragmentSignUpBinding.tvInvalidLastName.visibility = View.VISIBLE
            fragmentSignUpBinding.tvLastName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentSignUpBinding.inputLayoutLastName.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutLastName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
            fragmentSignUpBinding.tvLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        } else{
            fragmentSignUpBinding.tvInvalidLastName.visibility = View.GONE
        }

        if(TextUtils.isEmpty(fragmentSignUpBinding.tvEmailSignUp.text.toString()) ||
            !Patterns.EMAIL_ADDRESS.matcher(fragmentSignUpBinding.tvEmailSignUp.text.toString()).matches()){
            fieldsValid = false
            fragmentSignUpBinding.tvInvalidEmail.visibility = View.VISIBLE
            fragmentSignUpBinding.tvEmailSignUp.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentSignUpBinding.inputLayoutEmail.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
            fragmentSignUpBinding.tvEmailSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }else{
            fragmentSignUpBinding.tvInvalidEmail.visibility = View.GONE
        }

        if(TextUtils.isEmpty(fragmentSignUpBinding.tvPasswordSignUp.text.toString()) ||
            !isValidPassword(fragmentSignUpBinding.tvPasswordSignUp.text.toString())){
            fieldsValid = false
            fragmentSignUpBinding.tvInvalidPassword.visibility = View.VISIBLE
            fragmentSignUpBinding.tvPasswordSignUp.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentSignUpBinding.inputLayoutPassword.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutPassword.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
            fragmentSignUpBinding.tvPasswordSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }else{
            fragmentSignUpBinding.tvInvalidPassword.visibility = View.GONE
        }
        resetInvalidFields()
        return fieldsValid
    }

    private fun isValidPassword(password: String?): Boolean{
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)
            return passwordMatcher.find(it) != null
        } ?: return false
    }

    @SuppressLint("ResourceAsColor")
    private fun resetInvalidFields(){
        fragmentSignUpBinding.tvFirstName.setOnFocusChangeListener { _, _ ->
            fragmentSignUpBinding.inputLayoutFirstName.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentSignUpBinding.inputLayoutFirstName.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.inputLayoutFirstName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentSignUpBinding.tvFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.tvInvalidFirstName.visibility = View.GONE
            fragmentSignUpBinding.tvFirstName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary_50))
        }
        fragmentSignUpBinding.tvLastName.setOnFocusChangeListener { _, _ ->
            fragmentSignUpBinding.inputLayoutLastName.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentSignUpBinding.inputLayoutLastName.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.inputLayoutLastName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentSignUpBinding.tvLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.tvInvalidLastName.visibility = View.GONE
            fragmentSignUpBinding.tvLastName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary_50))
        }
        fragmentSignUpBinding.tvEmailSignUp.setOnFocusChangeListener { _, _ ->
            fragmentSignUpBinding.inputLayoutEmail.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentSignUpBinding.inputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.inputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentSignUpBinding.tvEmailSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.tvInvalidEmail.visibility = View.GONE
            fragmentSignUpBinding.tvEmailSignUp.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary_50))
        }
        fragmentSignUpBinding.tvPasswordSignUp.setOnFocusChangeListener { _, _ ->
            fragmentSignUpBinding.inputLayoutPassword.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentSignUpBinding.inputLayoutPassword.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.inputLayoutPassword.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentSignUpBinding.tvPasswordSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.tvInvalidPassword.visibility = View.GONE
            fragmentSignUpBinding.tvPasswordSignUp.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary_50))
        }
    }

    private fun emailIsTaken(){
        UtilitiesFunctions.createSuccessSnackbar(requireView(), "Error. User not created. Try again", ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_thick)!!,
            Snackbar.LENGTH_LONG, requireContext(), true, false).show()
        fragmentSignUpBinding.tvInvalidEmail.visibility = View.VISIBLE
        fragmentSignUpBinding.tvInvalidEmail.text = resources.getString(R.string.email_taken)
        fragmentSignUpBinding.inputLayoutEmail.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
        fragmentSignUpBinding.inputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
        fragmentSignUpBinding.inputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
        fragmentSignUpBinding.tvEmailSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
    }

    private fun createProgressBar(){
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(200, 200)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        fragmentSignUpBinding.relativeLayoutSignUp.addView(progressBar, params)
    }
}