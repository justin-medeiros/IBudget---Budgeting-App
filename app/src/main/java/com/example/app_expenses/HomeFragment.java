package com.example.app_expenses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.app_expenses.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    //private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private FloatingActionButton expense_button, delete_button;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapt;
    int index_arr;
    TextView budget_bal, expenses_bal, total_bal;
    public double total_bal_number = 0.00, amount_exp, amount_budg;
    public static double expenses_amount = 0.00, budget_amount = 0.00;
    public ListView transactions_list;
    ArrayAdapter<String> adapter;
    ArrayList<String> new_items;
    static ArrayList<Double> expense_arr = new ArrayList<Double>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // List view setup
        SwipeMenuListView listView = (SwipeMenuListView) root.findViewById(R.id.listView);

        // Get array of items
        new_items = Add_Transaction.getItems();
        adapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_1, new_items);
        listView.setAdapter(adapter);

        // Budget Amount
        amount_budg = Add_Budget.getBudgAmount();
        budget_amount += amount_budg;
        budget_bal = (TextView) root.findViewById(R.id.budget_amount);
        budget_bal.setText("+ $ " + String.format("%.2f", budget_amount));

        // Expenses Amount
        amount_exp = Add_Transaction.getAmount();
        if (amount_exp != 0) {
            expense_arr.add(0, amount_exp);        // Add each amount to array
        }
        expenses_amount += amount_exp;
        expenses_bal = (TextView) root.findViewById(R.id.expenses_amount);
        expenses_bal.setText("- $ " + String.format("%.2f", expenses_amount));

        // Total Balance
        total_bal_number = total_bal(budget_amount, expenses_amount);
        total_bal = (TextView) root.findViewById(R.id.total_balance);
        total_bal.setText("$ " + String.format("%.2f", total_bal_number));

        // Add transaction button
        expense_button = root.findViewById(R.id.plus_button);
        expense_button.show();
        expense_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAdd_transaction();
            }

        });

        // Swipe items in list and delete
        swipeMenuCreated(root, listView, "Home");

        return root;
    }

    public void openAdd_transaction(){
        Intent intent = new Intent(HomeFragment.this.getActivity(), Add_Transaction.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static double total_bal(double x, double y){
        return x - y;
    }

    public static double getTotalBudget(){
        return budget_amount;
    }

    public void swipeMenuCreated(View root, SwipeMenuListView listView, String choose_class){
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(root.getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);

        // For touching the items in the list
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                delete_list_item(root, position, choose_class);
                return false;
            }
        });
    }


    public void delete_list_item(View view, int position, String choose_class){
        AlertDialog.Builder adb=new AlertDialog.Builder(view.getContext());
        adb.setTitle("Delete?");
        adb.setMessage("Are you sure you want to delete?");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("EXP ARR POSITION", Double.toString(expense_arr.get(position)));
                adapter.remove(adapter.getItem(position));

                // If one item in list
                if (adapter.getCount() == 0){
                    expenses_amount = 0;
                    expense_arr.remove(0);
                }
                else{
                    expenses_amount-= expense_arr.remove(position);


                }
                total_bal_number = total_bal(budget_amount, expenses_amount);
                expenses_bal.setText("- $ " + String.format("%.2f", expenses_amount));
                total_bal.setText("$ " + String.format("%.2f", total_bal_number));
                if (choose_class == "TransList"){
                    Intent intent = new Intent(HomeFragment.this.getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        adb.show();
    }

}