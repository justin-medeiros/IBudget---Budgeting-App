package com.example.app_expenses

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
import android.view.Menu
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.app_expenses.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.appBarMain.toolbar)
        binding!!.appBarMain.toolbar.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawer = binding!!.drawerLayout
        val navigationView = binding!!.navView
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    // Override the back pressed so users can't press the backspace button when on home menu
    override fun onBackPressed() {}
    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }
}