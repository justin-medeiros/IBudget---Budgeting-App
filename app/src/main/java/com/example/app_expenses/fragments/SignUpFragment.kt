package com.example.app_expenses.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.databinding.FragmentSignUpBinding
import com.example.app_expenses.enums.SignUpEnum
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel

class SignUpFragment: Fragment() {
    private lateinit var fragmentSignUpBinding: FragmentSignUpBinding
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var password: String
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
            when(userCreated){
                SignUpEnum.USER_CREATED -> {
                    Toast.makeText(context, "User has been registered successfully!", Toast.LENGTH_LONG).show()
                    UtilitiesFunctions.replaceFragment(requireActivity(), LoginFragment(),
                        R.id.frameLayoutSplashScreen, false)
                }
                SignUpEnum.USER_NOT_CREATED ->{
                    Toast.makeText(context, "Error. User not created. Try again", Toast.LENGTH_LONG).show()
                }
                SignUpEnum.EMAIL_TAKEN ->{
                    emailIsTaken()
                }
            }
        }

        fragmentSignUpBinding.tvSignIn.setOnClickListener {
            UtilitiesFunctions.replaceFragment(requireActivity(), LoginFragment(),
                R.id.frameLayoutSplashScreen,false)
        }

        fragmentSignUpBinding.btnSignUp.setOnClickListener {
            fragmentSignUpBinding.tvFirstName.clearFocus()
            fragmentSignUpBinding.tvLastName.clearFocus()
            fragmentSignUpBinding.tvEmailSignUp.clearFocus()
            fragmentSignUpBinding.tvPasswordSignUp.clearFocus()
            if(validateFields()){
                userToDatabase()
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
            fragmentSignUpBinding.inputLayoutFirstName.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutFirstName.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentSignUpBinding.inputLayoutFirstName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
            fragmentSignUpBinding.tvFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }
        else{
            fragmentSignUpBinding.tvInvalidFirstName.visibility = View.GONE
        }

        if(TextUtils.isEmpty(fragmentSignUpBinding.tvLastName.text)){
            fieldsValid = false
            fragmentSignUpBinding.tvInvalidLastName.visibility = View.VISIBLE
            fragmentSignUpBinding.inputLayoutLastName.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutLastName.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentSignUpBinding.inputLayoutLastName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
            fragmentSignUpBinding.tvLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        } else{
            fragmentSignUpBinding.tvInvalidLastName.visibility = View.GONE
        }

        if(TextUtils.isEmpty(fragmentSignUpBinding.tvEmailSignUp.text.toString()) ||
            !Patterns.EMAIL_ADDRESS.matcher(fragmentSignUpBinding.tvEmailSignUp.text.toString()).matches()){
            fieldsValid = false
            fragmentSignUpBinding.tvInvalidEmail.visibility = View.VISIBLE
            fragmentSignUpBinding.inputLayoutEmail.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
            fragmentSignUpBinding.inputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
            fragmentSignUpBinding.tvEmailSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        }else{
            fragmentSignUpBinding.tvInvalidEmail.visibility = View.GONE
        }

        if(TextUtils.isEmpty(fragmentSignUpBinding.tvPasswordSignUp.text.toString()) ||
            !isValidPassword(fragmentSignUpBinding.tvPasswordSignUp.text.toString())){
            fieldsValid = false
            fragmentSignUpBinding.tvInvalidPassword.visibility = View.VISIBLE
            fragmentSignUpBinding.inputLayoutPassword.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
            fragmentSignUpBinding.inputLayoutPassword.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
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
        }
        fragmentSignUpBinding.tvLastName.setOnFocusChangeListener { _, _ ->
            fragmentSignUpBinding.inputLayoutLastName.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentSignUpBinding.inputLayoutLastName.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.inputLayoutLastName.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentSignUpBinding.tvLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.tvInvalidLastName.visibility = View.GONE
        }
        fragmentSignUpBinding.tvEmailSignUp.setOnFocusChangeListener { _, _ ->
            fragmentSignUpBinding.inputLayoutEmail.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentSignUpBinding.inputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.inputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentSignUpBinding.tvEmailSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.tvInvalidEmail.visibility = View.GONE
        }
        fragmentSignUpBinding.tvPasswordSignUp.setOnFocusChangeListener { _, _ ->
            fragmentSignUpBinding.inputLayoutPassword.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentSignUpBinding.inputLayoutPassword.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.inputLayoutPassword.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentSignUpBinding.tvPasswordSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentSignUpBinding.tvInvalidPassword.visibility = View.GONE
        }
    }

    private fun emailIsTaken(){
        Toast.makeText(context, "Error. Email has been taken. Try again", Toast.LENGTH_LONG).show()
        fragmentSignUpBinding.tvInvalidEmail.visibility = View.VISIBLE
        fragmentSignUpBinding.tvInvalidEmail.text = resources.getString(R.string.email_taken)
        fragmentSignUpBinding.inputLayoutEmail.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
        fragmentSignUpBinding.inputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
        fragmentSignUpBinding.inputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
        fragmentSignUpBinding.tvEmailSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
    }
}