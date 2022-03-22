package com.example.app_expenses;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.app_expenses.databinding.FragmentGalleryBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Transactions_List extends Fragment {

    //private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private FloatingActionButton add_exp_button;
    static boolean deleted_transList;
    TextView tot_bal, exp_bal;
    ArrayList<String> new_items_big;
    ArrayAdapter<String> adapter_big;
    ListView trans_big;
    HomeFragment home_frag = new HomeFragment();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ((MainActivity) getActivity()).setActionBarTitle("Transaction List");

        //trans_big = (ListView)root.findViewById(R.id.transaction_list);

        // Set amount to 0
        Add_Transaction.amount_flt = 0;
        Add_Budget.budget_amount = 0;

        // List view setup
        SwipeMenuListView listView = (SwipeMenuListView) root.findViewById(R.id.listView);

        // Display the list of transactions
        new_items_big = Add_Transaction.getItems();
        adapter_big = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_list_item_1, new_items_big);
        listView.setAdapter(adapter_big) ;

        // Add transaction button
        add_exp_button = root.findViewById(R.id.plus_button_list);
        add_exp_button.show();
        add_exp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAdd_transaction();
            }
        });

        // Delete Transactions
        //swipeMenuCreated(root, listView, "TransList");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openAdd_transaction(){
        Intent intent = new Intent(Transactions_List.this.getActivity(), Add_Transaction.class);
        startActivity(intent);
    }

    public void openHome(){
        Intent intent = new Intent(Transactions_List.this.getActivity(), MainActivity.class);
        startActivity(intent);
    }

}