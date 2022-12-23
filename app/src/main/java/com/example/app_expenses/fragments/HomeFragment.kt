package com.example.app_expenses.fragments

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.databinding.FragmentHomeBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar


class HomeFragment: Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var signOutAlertDialog: AlertDialog
    private val mainActivity = MainActivity()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
            container, false)
        signOutAlertDialog = createSignOutDialog()
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
            signOutAlertDialog.show()
        }

        authViewModel.signOutLiveData().observe(viewLifecycleOwner){ signedOut ->
            if(signedOut){
                mainActivity.hideTabBarVisibility()
                signOutAlertDialog.dismiss()
                UtilitiesFunctions.createSimpleSnackbar(fragmentHomeBinding.root, "Signed Out Successfully",
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!, Snackbar.LENGTH_LONG, requireContext(), true).show()
                UtilitiesFunctions.replaceFragment(requireActivity(), LoginFragment(), R.id.relativeLayoutMainActivity, false)
            }
        }
    }

    private fun createSignOutDialog(): AlertDialog{
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
            authViewModel.signOut()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        return alertDialog
    }

}