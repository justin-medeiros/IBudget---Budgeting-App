package com.example.app_expenses

import com.google.android.material.textfield.TextInputLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.app_expenses.Add_Transaction
import android.widget.TextView
import com.example.app_expenses.R
import com.example.app_expenses.HomeFragment
import com.google.android.material.snackbar.Snackbar
import com.example.app_expenses.Add_Budget
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.app_expenses.Transactions_List
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.widget.ArrayAdapter
import android.graphics.drawable.ColorDrawable
import android.content.DialogInterface
import android.view.View
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.databinding.FragmentGalleryBinding
import java.util.ArrayList

class Transactions_List : Fragment() {
    private var binding: FragmentGalleryBinding? = null
    private var add_exp_button: FloatingActionButton? = null
    var new_items_big: ArrayList<String>? = null
    var adapter_big: ArrayAdapter<String>? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        // Set amount to 0
        Add_Transaction.amount = 0.0
        Add_Budget.budgAmount = 0.0


        // Display the list of transactions
        new_items_big = Add_Transaction.items
        adapter_big = ArrayAdapter(root.context, android.R.layout.simple_list_item_1, new_items_big!!)

        // Add transaction button
        add_exp_button = root.findViewById(R.id.plus_button_list)
        add_exp_button?.show()
        add_exp_button?.setOnClickListener(View.OnClickListener { openAdd_transaction() })

        // Delete Transactions
        //swipeMenuCreated(root, listView, "TransList");
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun openAdd_transaction() {
        val intent = Intent(this@Transactions_List.activity, Add_Transaction::class.java)
        startActivity(intent)
    }

    fun openHome() {
        val intent = Intent(this@Transactions_List.activity, MainActivity::class.java)
        startActivity(intent)
    }
}