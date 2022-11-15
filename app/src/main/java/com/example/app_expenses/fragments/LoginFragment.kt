package com.example.app_expenses.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.app_expenses.MainActivity
import com.example.app_expenses.R
import com.example.app_expenses.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private lateinit var auth: FirebaseAuth
private lateinit var password: String
private lateinit var email: String
private lateinit var fragmentLoginBinding: FragmentLoginBinding

class LoginFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,
            container, false)
        auth = Firebase.auth
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentLoginBinding.tvSignUp.setOnClickListener {
            replaceFragment()
        }
        fragmentLoginBinding.btnLogin.setOnClickListener {
            fragmentLoginBinding.tvLoginEmail.clearFocus()
            fragmentLoginBinding.tvLoginPassword.clearFocus()
            loginUser()
        }
        fragmentLoginBinding.tvLoginForgotPassword.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun replaceActivity(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutSplashScreen, SignUpFragment())
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun loginUser(){
        email = fragmentLoginBinding.tvLoginEmail.text.toString()
        password = fragmentLoginBinding.tvLoginPassword.text.toString()
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener  { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = auth.currentUser
                    replaceActivity()
                } else {
                    // Sign in fails
                    invalidUI()
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
        }
        else{
            invalidUI()
        }
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
            fragmentLoginBinding.tvInvalidLogin.visibility = View.GONE
            fragmentLoginBinding.loginEmailTextInput.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentLoginBinding.loginEmailTextInput.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentLoginBinding.loginEmailTextInput.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentLoginBinding.tvLoginEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        fragmentLoginBinding.tvLoginPassword.setOnFocusChangeListener { _, _ ->
            fragmentLoginBinding.tvInvalidLogin.visibility = View.GONE
            fragmentLoginBinding.loginPasswordTextInput.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            fragmentLoginBinding.loginPasswordTextInput.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            fragmentLoginBinding.loginPasswordTextInput.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.foreground_primary)))
            fragmentLoginBinding.tvLoginPassword.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun showAlertDialog(){
        val builder = AlertDialog.Builder(activity)

        val view: View = requireActivity().layoutInflater.inflate(R.layout.alertbox_forget_password, null)
        builder.setView(view)

        val alertDialog: AlertDialog = builder.create()
        val cancelButton = view.findViewById<Button>(R.id.btnCancelAlertBox)
        val sendButton = view.findViewById<Button>(R.id.btnSendAlertBox)

        val emailAlert = view.findViewById<TextInputEditText>(R.id.tvAlertEmail)
        val emailTextBox = view.findViewById<TextInputLayout>(R.id.alertEmailTextInput)
        val invalidAlert = view.findViewById<TextView>(R.id.tvInvalidAlert)

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        sendButton.setOnClickListener {
            emailTextBox.clearFocus()
            if(!TextUtils.isEmpty(emailAlert.text)){
                auth.sendPasswordResetEmail(emailAlert.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            alertDialog.dismiss()
                        }
                        else{
                            forgotPasswordInvalid(invalidAlert, emailTextBox, emailAlert)
                        }
                    }
            }
            else{
                forgotPasswordInvalid(invalidAlert, emailTextBox, emailAlert)
            }

        }

        emailAlert.setOnFocusChangeListener { _, _ ->
            invalidAlert.visibility = View.GONE
            emailTextBox.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.background_secondary)
            emailTextBox.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            emailAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        if(alertDialog.window != null){
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.show()
    }
    private fun forgotPasswordInvalid(invalidAlert: TextView, textBox: TextInputLayout, text: TextInputEditText){
        invalidAlert.visibility = View.VISIBLE
        textBox.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
        textBox.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
    }


}
