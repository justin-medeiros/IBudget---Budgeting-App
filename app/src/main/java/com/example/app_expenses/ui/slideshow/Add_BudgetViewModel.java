package com.example.app_expenses.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Add_BudgetViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Add_BudgetViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}