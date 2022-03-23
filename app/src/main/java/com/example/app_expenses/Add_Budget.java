package com.example.app_expenses;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_expenses.databinding.FragmentSlideshowBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class Add_Budget extends Fragment {

    Button budget_button, remove_button;
    TextInputLayout budget_lay;
    public static double budget_amount = 0;
    private FragmentSlideshowBinding binding;
    public static double budget_bal = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set title
        ((MainActivity) getActivity()).setActionBarTitle("Add Budget");

        // Set expenses amount to 0
        Add_Transaction.amount_flt = 0;

        // Display the budget
        TextView budget_add = root.findViewById(R.id.budget_total);
        budget_add.setText(String.format("$%.2f", HomeFragment.getTotalBudget()));

        // Get text inputted
        budget_lay = (TextInputLayout) root.findViewById(R.id.budget_add);

        // Add button
        budget_button = root.findViewById(R.id.budget_button);
        budget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Try catch to see if valid input
                try {
                    if(budget_lay.getEditText().getText().toString().isEmpty()){
                        Snackbar invalid_str = Snackbar.make(view, "Transaction cannot be empty.", 600);
                        invalid_str.show();
                    }
                    else{
                        budget_amount = Double.parseDouble(budget_lay.getEditText().getText().toString());
                        home_menu(root);
                    }


                }catch(Exception e) {
                    Snackbar invalid_num = Snackbar.make(view, "Invalid amount, please try again.", 600);
                    invalid_num.show();
                }

            }
        });

        // Remove button
        remove_button = root.findViewById(R.id.remove_button);
        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Try catch to see if valid input
                try {
                    if(budget_lay.getEditText().getText().toString().isEmpty()){
                        Snackbar invalid_str = Snackbar.make(view, "Transaction cannot be empty.", 600);
                        invalid_str.show();
                    }
                    else {

                        budget_amount = Double.parseDouble(budget_lay.getEditText().getText().toString());
                        if (budget_amount > HomeFragment.getTotalBudget()) {
                            // To ensure user does not enter a value to remove that will exceed the total budget balance
                            Snackbar below_total = Snackbar.make(view, "Amount exceeds total balance. Please try again.", 800);
                            below_total.show();
                        } else{
                            budget_amount*= -1;
                            home_menu(root);
                        }
                    }


                }catch(Exception e) {
                    Snackbar invalid_num = Snackbar.make(view, "Invalid amount, please try again.", 600);
                    invalid_num.show();

                }

            }
        });

        return root;
    }

    public void home_menu(View root){
        Intent intent = new Intent(root.getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static double getBudgAmount(){
        return budget_amount;
    }


}