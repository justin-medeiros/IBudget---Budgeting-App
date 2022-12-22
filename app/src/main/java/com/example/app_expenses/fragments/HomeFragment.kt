package com.example.app_expenses.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.databinding.FragmentHomeBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel


class HomeFragment: Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
            container, false)
        fragmentHomeBinding.currentDate = UtilitiesFunctions.timestampToDate(System.currentTimeMillis())

        authViewModel.getCurrentUserName()
        authViewModel.getCurrentUserNameLiveData().observe(viewLifecycleOwner){ name ->
            fragmentHomeBinding.name = "Welcome, $name"
        }

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHomeBinding.signOutButton.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun showSignOutDialog(){
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)

        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.custom_signout_alert_dialog, null)
        dialogBuilder.setView(dialogView)

        val cancelButton = dialogView.findViewById<Button>(R.id.btnSignOutCancel)
        val confirmButton = dialogView.findViewById<Button>(R.id.btnSignOutConfirm)
        val alertDialog: AlertDialog = dialogBuilder.create()

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        confirmButton.setOnClickListener {

        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        alertDialog.show()
    }

}