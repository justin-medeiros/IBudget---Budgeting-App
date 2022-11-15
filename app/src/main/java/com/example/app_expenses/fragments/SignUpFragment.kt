package com.example.app_expenses.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.app_expenses.R
import com.example.app_expenses.data.UserData
import com.example.app_expenses.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


private lateinit var fragmentSignUpBinding: FragmentSignUpBinding
private lateinit var auth: FirebaseAuth
private lateinit var firebaseDatabase: DatabaseReference
private lateinit var firstName: String
private lateinit var lastName: String
private lateinit var email: String
private lateinit var password: String
private lateinit var newUser: UserData

class SignUpFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentSignUpBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_up,
            container, false
        )
        auth = Firebase.auth
        firebaseDatabase = Firebase.database.reference
        return fragmentSignUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentSignUpBinding.tvSignIn.setOnClickListener {
            replaceFragment()
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

    private fun replaceFragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutSplashScreen, LoginFragment())
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun userToDatabase() {
        firstName = fragmentSignUpBinding.tvFirstName.text.toString()
        lastName = fragmentSignUpBinding.tvLastName.text.toString()
        email = fragmentSignUpBinding.tvEmailSignUp.text.toString()
        password = fragmentSignUpBinding.tvPasswordSignUp.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                newUser = UserData(firstName, lastName, email, password)
                firebaseDatabase.child("users").child(FirebaseAuth.getInstance().currentUser?.uid!!).child("profile").setValue(newUser).addOnCompleteListener { registerUser ->
                    if(registerUser.isSuccessful){
                        Toast.makeText(context, "User has been registered successfully!", Toast.LENGTH_LONG).show()
                        replaceFragment()
                    }
                    else{
                        Toast.makeText(context, "Error. User not registered.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(context, "Error. Email has been taken. Try again", Toast.LENGTH_LONG).show()
                fragmentSignUpBinding.tvInvalidEmail.visibility = View.VISIBLE
                fragmentSignUpBinding.tvInvalidEmail.text = resources.getString(R.string.email_taken)
                fragmentSignUpBinding.inputLayoutEmail.boxBackgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
                fragmentSignUpBinding.inputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright))
                fragmentSignUpBinding.inputLayoutEmail.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red_bright)))
                fragmentSignUpBinding.tvEmailSignUp.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_bright))
            }
        }
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
}