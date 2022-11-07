package com.example.app_expenses

import android.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.app_expenses.MainActivity
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
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.example.app_expenses.databinding.FragmentHomeBinding
import java.util.ArrayList

class HomeFragment : Fragment() {
    //private HomeViewModel homeViewModel;
    private var binding: FragmentHomeBinding? = null
    private var expense_button: FloatingActionButton? = null
    private val delete_button: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    var mAdapt: RecyclerView.Adapter<*>? = null
    var index_arr = 0
    var budget_bal: TextView? = null
    var expenses_bal: TextView? = null
    var total_bal: TextView? = null
    var total_bal_number = 0.00
    var amount_exp = 0.0
    var amount_budg = 0.0
    var transactions_list: ListView? = null
    var adapter: ArrayAdapter<String>? = null
    var new_items: ArrayList<String>? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        // List view setup
        val listView = root.findViewById<View>(R.id.listView) as SwipeMenuListView

        // Get array of items
        new_items = Add_Transaction.items
        adapter = ArrayAdapter(root.context, android.R.layout.simple_list_item_1, new_items!!)
        listView.adapter = adapter

        // Budget Amount
        amount_budg = Add_Budget.budgAmount
        totalBudget += amount_budg
        budget_bal = root.findViewById<View>(R.id.budget_amount) as TextView
        budget_bal!!.text = "+ $ " + String.format("%.2f", totalBudget)

        // Expenses Amount
        amount_exp = Add_Transaction.amount
        if (amount_exp != 0.0) {
            expense_arr.add(0, amount_exp) // Add each amount to array
        }
        expenses_amount += amount_exp
        expenses_bal = root.findViewById<View>(R.id.expenses_amount) as TextView
        expenses_bal!!.text = "- $ " + String.format("%.2f", expenses_amount)

        // Total Balance
        total_bal_number = total_bal(totalBudget, expenses_amount)
        total_bal = root.findViewById<View>(R.id.total_balance) as TextView
        total_bal!!.text = "$ " + String.format("%.2f", total_bal_number)

        // Add transaction button
        expense_button = root.findViewById(R.id.plus_button)
        expense_button?.show()
        expense_button?.setOnClickListener(View.OnClickListener { openAdd_transaction() })

        // Swipe items in list and delete
        swipeMenuCreated(root, listView, "Home")
        return root
    }

    fun openAdd_transaction() {
        val intent = Intent(this@HomeFragment.activity, Add_Transaction::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun swipeMenuCreated(root: View, listView: SwipeMenuListView, choose_class: String) {
        val creator = SwipeMenuCreator { menu -> // create "delete" item
            val deleteItem = SwipeMenuItem(root.context)
            // set item background
            deleteItem.background = ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25))
            // set item width
            deleteItem.width = 170
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete)
            // add to menu
            menu.addMenuItem(deleteItem)
        }
        listView.setMenuCreator(creator)

        // For touching the items in the list
        listView.setOnMenuItemClickListener { position, menu, index ->
            delete_list_item(root, position, choose_class)
            false
        }
    }

    fun delete_list_item(view: View, position: Int, choose_class: String) {
        val adb = AlertDialog.Builder(view.context)
        adb.setTitle("Delete?")
        adb.setMessage("Are you sure you want to delete?")
        adb.setNegativeButton("Cancel", null)
        adb.setPositiveButton("Ok") { dialogInterface, i ->
            Log.d("EXP ARR POSITION", java.lang.Double.toString(expense_arr[position]))
            adapter!!.remove(adapter!!.getItem(position))

            // If one item in list
            if (adapter!!.count == 0) {
                expenses_amount = 0.0
                expense_arr.removeAt(0)
            } else {
                expenses_amount -= expense_arr.removeAt(position)
            }
            total_bal_number = total_bal(totalBudget, expenses_amount)
            expenses_bal!!.text = "- $ " + String.format("%.2f", expenses_amount)
            total_bal!!.text = "$ " + String.format("%.2f", total_bal_number)
            if (choose_class === "TransList") {
                val intent = Intent(this@HomeFragment.activity, MainActivity::class.java)
                startActivity(intent)
            }
        }
        adb.show()
    }

    companion object {
        var expenses_amount = 0.00
        var totalBudget = 0.00
        var expense_arr = ArrayList<Double>()
        fun total_bal(x: Double, y: Double): Double {
            return x - y
        }
    }
}