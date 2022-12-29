package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.databinding.FragmentForgetPasswordBinding
import com.example.app_expenses.viewModels.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgetPasswordFragment: DialogFragment() {

    private lateinit var fragmentForgetPassword: FragmentForgetPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentForgetPassword = DataBindingUtil.inflate(inflater, R.layout.fragment_forget_password,
            container, false)
        return fragmentForgetPassword.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authViewModel.getSendPasswordResetLiveData().observe(viewLifecycleOwner){ isSent ->
            if(isSent){
                dismiss()
                Toast.makeText(context, "Password sent.",
                    Toast.LENGTH_SHORT).show()
            }else{
                forgotPasswordInvalid(fragmentForgetPassword.tvInvalidAlert, fragmentForgetPassword.alertEmailTextInput,
                    fragmentForgetPassword.tvAlertEmail)
            }
        }

        createAlertDialog()

    }

    private fun createAlertDialog(){
        fragmentForgetPassword.btnCancelAlertBox.setOnClickListener {
            dismiss()
        }

        fragmentForgetPassword.btnSendAlertBox.setOnClickListener {
            fragmentForgetPassword.alertEmailTextInput.clearFocus()
            authViewModel.sendPasswordReset(fragmentForgetPassword.tvAlertEmail.text.toString())
        }

        fragmentForgetPassword.tvAlertEmail.setOnFocusChangeListener { _, _ ->
            fragmentForgetPassword.tvInvalidAlert.visibility = View.GONE
            fragmentForgetPassword.tvAlertEmail.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.foreground_primary_50))
            fragmentForgetPassword.alertEmailTextInput.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentForgetPassword.alertEmailTextInput.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentForgetPassword.tvAlertEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        if(dialog!!.window != null){
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
    }

    private fun forgotPasswordInvalid(invalidAlert: TextView, textBox: TextInputLayout, text: TextInputEditText){
        invalidAlert.visibility = View.VISIBLE
        text.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
        textBox.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
    }


}