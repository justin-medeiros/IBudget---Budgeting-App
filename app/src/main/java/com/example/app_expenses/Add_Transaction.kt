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
import com.google.android.material.navigation.NavigationView
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.example.app_expenses.activities.MainActivity
import java.lang.Exception
import java.util.ArrayList

class Add_Transaction : AppCompatActivity() {
    var add_button: Button? = null
    var cancel_click: Button? = null
    var trans_lay: TextInputLayout? = null
    var amount_lay: TextInputLayout? = null
    var transaction_line: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Set the budget amount to 0
        Add_Budget.budgAmount = 0.0

        // Set title
        val actionBar = supportActionBar
        actionBar!!.setTitle("Transactions")

        // Add button
        add_button = findViewById(R.id.button_add)
        add_button?.setOnClickListener(View.OnClickListener { view ->
            // Get text inputted
            trans_lay = findViewById<View>(R.id.transaction_name) as TextInputLayout
            amount_lay = findViewById<View>(R.id.money_amount) as TextInputLayout

            // Try catch to see if valid input
            try {
                if (trans_lay!!.editText!!.text.toString().isEmpty()) {
                    val invalid_str = Snackbar.make(view, "Transaction cannot be empty.", 600)
                    invalid_str.show()
                } else {
                    //Log.d("CHECK", amount_lay.getEditText().getText().toString());
                    amount = amount_lay!!.editText!!.text.toString().toDouble()
                    transaction_line = String.format("%s\n+ $ %.2f", trans_lay!!.editText!!.text, amount.toFloat())
                    items.add(0, transaction_line!!)
                    home_menu()
                }
            } catch (e: Exception) {
                val invalid_num = Snackbar.make(view, "Invalid amount, please try again.", 600)
                invalid_num.show()
            }
        })

        // Cancel Button
        cancel_click = findViewById(R.id.cancel_clicked)
        cancel_click?.setOnClickListener(View.OnClickListener {
            amount = 0.0
            home_menu()
        })
    }

    fun home_menu() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun transactions_frag() {
        val intent = Intent(this, Transactions_List::class.java)
        startActivity(intent)
    }

    // Override the back pressed so users can't press the backspace button when on transaction menu
    override fun onBackPressed() {}

    companion object {
        var amount = 0.0
        var items = ArrayList<String>()
    }
}