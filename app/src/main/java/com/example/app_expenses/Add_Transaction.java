package com.example.app_expenses;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class Add_Transaction extends AppCompatActivity {

    Button add_button;
    Button cancel_click;
    public TextInputLayout trans_lay;
    public TextInputLayout amount_lay;
    public static double amount_flt = 0;
    public String transaction_line;
    public static ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Set the budget amount to 0
        Add_Budget.budget_amount = 0;

        // Set title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Transactions");

        // Add button
        add_button = this.findViewById(R.id.button_add);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get text inputted
                trans_lay = (TextInputLayout) findViewById(R.id.transaction_name);
                amount_lay = (TextInputLayout) findViewById(R.id.money_amount);

                // Try catch to see if valid input
                try {
                    if(trans_lay.getEditText().getText().toString().isEmpty()){
                        Snackbar invalid_str = Snackbar.make(view, "Transaction cannot be empty.", 600);
                        invalid_str.show();
                    }
                    else {
                        //Log.d("CHECK", amount_lay.getEditText().getText().toString());
                        amount_flt = Double.parseDouble(amount_lay.getEditText().getText().toString());
                        transaction_line = String.format("%s\n+ $ %.2f", trans_lay.getEditText().getText(), (float)amount_flt);
                        items.add(0, transaction_line);
                        home_menu();
                    }
                }catch(Exception e) {
                        Snackbar invalid_num = Snackbar.make(view, "Invalid amount, please try again.", 600);
                        invalid_num.show();

                }

            }
        });

        // Cancel Button
        cancel_click = this.findViewById(R.id.cancel_clicked);
        cancel_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount_flt = 0;
                home_menu();
            }
        });
    }

    public void home_menu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void transactions_frag(){
        Intent intent = new Intent(this, Transactions_List.class);
        startActivity(intent);
    }


    public static ArrayList<String> getItems(){
        return items;
    }

    public static double getAmount(){
        return amount_flt;
    }

    // Override the back pressed so users can't press the backspace button when on transaction menu
    @Override
    public void onBackPressed(){}


}