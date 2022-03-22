package com.example.app_expenses.ui.home;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app_expenses.R;
import com.example.app_expenses.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private FragmentHomeBinding binding;
    private FloatingActionButton expense_button;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}