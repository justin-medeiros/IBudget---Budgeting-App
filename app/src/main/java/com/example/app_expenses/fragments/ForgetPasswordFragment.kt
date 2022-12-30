package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.databinding.FragmentForgetPasswordBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgetPasswordFragment(private val thisFragment: LoginFragment): DialogFragment() {

    private lateinit var fragmentForgetPassword: FragmentForgetPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var progressBar: ProgressBar

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
            progressBar.visibility = View.GONE
            if(isSent){
                dismiss()
                UtilitiesFunctions.createSuccessSnackbar(thisFragment.requireView()!!, "Password sent.",
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!, Snackbar.LENGTH_LONG, requireContext(),
                    true, true).show()
            }else{
                UtilitiesFunctions.createSuccessSnackbar(thisFragment.requireView(), "Invalid email. Try again.",
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_thick)!!, Snackbar.LENGTH_LONG, requireContext(),
                    true, false).show()
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
            createProgressBar()
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

    private fun createProgressBar(){
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(200, 200)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        fragmentForgetPassword.forgetPasswordContainer.addView(progressBar, params)
    }

}