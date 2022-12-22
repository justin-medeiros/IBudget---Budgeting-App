package com.example.app_expenses.activities

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.app_expenses.R
import com.example.app_expenses.adapters.TabBarAdapter
import com.example.app_expenses.databinding.ActivityMainBinding
import com.example.app_expenses.fragments.AddTransactionFragment
import com.example.app_expenses.utils.PrefsHelper
import com.example.app_expenses.viewModels.TransactionsViewModel
import com.google.android.material.tabs.TabLayout

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var transactionViewModel: TransactionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        transactionViewModel = ViewModelProvider(this)[TransactionsViewModel::class.java]
        setContentView(binding!!.root)
        showTabBar()
        setTabBarMargins()
        PrefsHelper.init(context = this)

        binding.tabBarButton.setOnClickListener {
            AddTransactionFragment().show(supportFragmentManager, "")
        }

        transactionViewModel.amountOfItemsSelected.observe(this){ numberOfItems ->
            binding.buttonDeleteMain.text = "Delete ${numberOfItems} items"
        }

        binding.buttonDeleteMain.setOnClickListener {
            transactionViewModel.deleteButtonClicked.postValue(true)
        }
    }

    fun visibleTabBarVisibility(){
        binding.tabLayoutMain.visibility = View.VISIBLE
        binding.viewPagerMain.visibility = View.VISIBLE
        binding.tabBarButton.visibility = View.VISIBLE
    }

    fun hideTabBarVisibility(){
        binding.tabLayoutMain.visibility = View.GONE
        binding.viewPagerMain.visibility = View.GONE
        binding.tabBarButton.visibility = View.GONE
    }

    private fun showTabBar(){
        val tabBarAdapter = TabBarAdapter(this, binding.tabLayoutMain.tabCount)
        binding.viewPagerMain.adapter = tabBarAdapter
        binding.viewPagerMain.offscreenPageLimit = tabBarAdapter.itemCount
        binding.viewPagerMain.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                binding.tabLayoutMain.selectTab(binding.tabLayoutMain.getTabAt(position))
                binding.tabLayoutMain.getTabAt(position)?.icon?.colorFilter =
                    PorterDuffColorFilter(ContextCompat.getColor(this@MainActivity, R.color.foreground_primary), PorterDuff.Mode.SRC_IN)
            }
        })

        binding.tabLayoutMain.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPagerMain.currentItem = tab?.position!!
                tab?.icon?.colorFilter =
                    PorterDuffColorFilter(ContextCompat.getColor(this@MainActivity, R.color.foreground_primary), PorterDuff.Mode.SRC_IN)

                if(tab?.position!! != 1){
                    binding.buttonDeleteMain.visibility = View.INVISIBLE
                } else{
                    if(transactionViewModel.selectButtonClicked.value == true){
                        binding.buttonDeleteMain.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.colorFilter =
                    PorterDuffColorFilter(ContextCompat.getColor(this@MainActivity, R.color.background_tertiary), PorterDuff.Mode.SRC_IN)
            }
        })
    }

    private fun setTabBarMargins(){
        val tabs = binding.tabLayoutMain.getChildAt(0) as ViewGroup

        for (i in 0 until tabs.childCount ) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            when(i){
                0 -> {
                    layoutParams.weight = 0f
                    layoutParams.marginEnd = 40
                    layoutParams.width = 120
                    tab.layoutParams = layoutParams
                }
                1->{
                    layoutParams.weight = 0f
                    layoutParams.marginEnd = 80
                    layoutParams.width = 120
                    tab.layoutParams = layoutParams
                }
                2->{
                    layoutParams.weight = 0f
                    layoutParams.marginStart = 80
                    layoutParams.width = 120
                    tab.layoutParams = layoutParams
                }
                3->{
                    layoutParams.weight = 0f
                    layoutParams.marginStart = 40
                    layoutParams.width = 120
                    tab.layoutParams = layoutParams
                }
                else ->{

                }

            }
        }
        binding.tabLayoutMain.requestLayout()
    }

    fun showDeleteButton(){
        binding.buttonDeleteMain.visibility = View.VISIBLE
    }

    fun hideDeleteButton(){
        binding.buttonDeleteMain.visibility = View.INVISIBLE
        binding.buttonDeleteMain.text = "Delete 0 items"
    }

    fun switchToTab(position: Int){
        val tab = binding.tabLayoutMain.getTabAt(position)
        binding.tabLayoutMain.selectTab(tab)
        binding.viewPagerMain.currentItem = position
    }

    // Override the back pressed so users can't press the backspace button when on home menu
    override fun onBackPressed() {}
}