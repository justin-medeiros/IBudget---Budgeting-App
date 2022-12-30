package com.example.app_expenses.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.databinding.FragmentBudgetBinding
import com.example.app_expenses.databinding.FragmentOnboardingBinding
import com.example.app_expenses.utils.UtilitiesFunctions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingFragment: Fragment() {
    private var onboardingPage = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var fragmentOnboardingBinding: FragmentOnboardingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentOnboardingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding,
            container, false)
        Picasso.get().load(R.drawable.onboard_first_logo).into(fragmentOnboardingBinding.imageViewOnboarding)
        return fragmentOnboardingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentOnboardingBinding.onboardingButton.setOnClickListener {
            when(onboardingPage){
                0 -> {
                    Picasso.get().load(R.drawable.onboard_second_logo).into(fragmentOnboardingBinding.imageViewOnboarding)
                    fragmentOnboardingBinding.onboardingDots.background = ContextCompat.getDrawable(requireContext(), R.drawable.onboard_dots_second)
                    fragmentOnboardingBinding.onboardTitle.text = resources.getString(R.string.onboard_second_title)
                    fragmentOnboardingBinding.onboardDesc.text = resources.getString(R.string.onboard_second_desc)
                } 1->{
                    Picasso.get().load(R.drawable.onboard_third_logo).into(fragmentOnboardingBinding.imageViewOnboarding)
                fragmentOnboardingBinding.onboardingDots.background = ContextCompat.getDrawable(requireContext(), R.drawable.onboard_dots_last)
                    fragmentOnboardingBinding.onboardTitle.text = resources.getString(R.string.onboard_third_title)
                    fragmentOnboardingBinding.onboardDesc.text = resources.getString(R.string.onboard_third_desc)
                } else ->{
                    lifecycleScope.launch {
                        createProgressBar()
                        replaceActivity()
                        delay(300)
                        progressBar.visibility = View.GONE
                    }
                }
            }
            onboardingPage++
        }
    }

    private fun replaceActivity(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun createProgressBar(){
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(200, 200)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        fragmentOnboardingBinding.relativeLayoutOnboarding.addView(progressBar, params)
    }
}