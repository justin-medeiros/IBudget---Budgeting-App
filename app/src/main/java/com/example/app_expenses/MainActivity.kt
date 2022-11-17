package com.example.app_expenses

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.app_expenses.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        showTabBar(binding)
    }

    private fun showTabBar(binding: ActivityMainBinding){
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
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.colorFilter =
                    PorterDuffColorFilter(ContextCompat.getColor(this@MainActivity, R.color.background_tertiary), PorterDuff.Mode.SRC_IN)
            }
        })
    }


    // Override the back pressed so users can't press the backspace button when on home menu
    override fun onBackPressed() {}
}