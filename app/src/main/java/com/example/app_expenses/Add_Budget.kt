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
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuItem
import android.graphics.drawable.ColorDrawable
import android.content.DialogInterface
import android.view.View
import android.widget.Button
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.databinding.FragmentSlideshowBinding
import java.lang.Exception

class Add_Budget : Fragment() {
    var budget_button: Button? = null
    var remove_button: Button? = null
    var budget_lay: TextInputLayout? = null
    private var binding: FragmentSlideshowBinding? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        // Set title
        //(activity as MainActivity?)!!.setActionBarTitle("Add Budget")

//        // Set expenses amount to 0
        Add_Transaction.amount = 0.0

        // Display the budget
        val budget_add = root.findViewById<TextView>(R.id.budget_total)
        budget_add.text = String.format("$%.2f", HomeFragment.totalBudget)

        // Get text inputted
        budget_lay = root.findViewById<View>(R.id.budget_add) as TextInputLayout

        // Add button
        budget_button = root.findViewById(R.id.budget_button)
        budget_button?.setOnClickListener(View.OnClickListener { view -> // Try catch to see if valid input
            try {
                if (budget_lay!!.editText!!.text.toString().isEmpty()) {
                    val invalid_str = Snackbar.make(view, "Transaction cannot be empty.", 600)
                    invalid_str.show()
                } else {
                    budgAmount = budget_lay!!.editText!!.text.toString().toDouble()
                    home_menu(root)
                }
            } catch (e: Exception) {
                val invalid_num = Snackbar.make(view, "Invalid amount, please try again.", 600)
                invalid_num.show()
            }
        })

        // Remove button
        remove_button = root.findViewById(R.id.remove_button)
        remove_button?.setOnClickListener(View.OnClickListener { view ->
            // Try catch to see if valid input
            try {
                if (budget_lay!!.editText!!.text.toString().isEmpty()) {
                    val invalid_str = Snackbar.make(view, "Transaction cannot be empty.", 600)
                    invalid_str.show()
                } else {
                    budgAmount = budget_lay!!.editText!!.text.toString().toDouble()
                    if (budgAmount > HomeFragment.totalBudget) {
                        // To ensure user does not enter a value to remove that will exceed the total budget balance
                        val below_total = Snackbar.make(view, "Amount exceeds total balance. Please try again.", 800)
                        below_total.show()
                    } else {
                        budgAmount *= -1.0
                        home_menu(root)
                    }
                }
            } catch (e: Exception) {
                val invalid_num = Snackbar.make(view, "Invalid amount, please try again.", 600)
                invalid_num.show()
            }
        })
        return root
    }

    fun home_menu(root: View) {
        val intent = Intent(root.context, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        var budgAmount = 0.0
        var budget_bal = 0.0
    }
}