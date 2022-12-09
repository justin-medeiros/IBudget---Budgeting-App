package com.example.app_expenses.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_expenses.R
import com.example.app_expenses.adapters.TransactionListAdapter
import com.example.app_expenses.databinding.FragmentTransactionListBinding
import com.example.app_expenses.viewModels.TransactionsViewModel
import com.google.android.material.snackbar.Snackbar

class TransactionListFragment: Fragment() {
    private lateinit var fragmentTransactionListBinding: FragmentTransactionListBinding
    private var myTransactionsListAdapter = TransactionListAdapter()
    private lateinit var transactionViewModel: TransactionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentTransactionListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_list,
            container, false)
        transactionViewModel = ViewModelProvider(requireActivity())[TransactionsViewModel::class.java]
        transactionViewModel.getMyTransactions()

        val categoriesArray = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categoriesArray)

        fragmentTransactionListBinding.tvAutoCompleteTransactionList.setAdapter(arrayAdapter)
        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentTransactionListBinding.rvTransactionList.layoutManager = myLinearLayoutManager
        fragmentTransactionListBinding.rvTransactionList.adapter = myTransactionsListAdapter
        return fragmentTransactionListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionViewModel.getMyTransactionsLiveData().observe(viewLifecycleOwner){ transactionsList ->

            myTransactionsListAdapter.addAllItems(transactionsList)
        }

        transactionViewModel.getAddTransactionLiveData().observe(viewLifecycleOwner){ transactionData ->
            myTransactionsListAdapter.addItem(transactionData!!, 0)
        }
    }

//    private fun setSwipeToDelete(){
//        val itemTouchHelperCallback =
//            object :
//                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
//                                    target: RecyclerView.ViewHolder): Boolean {
//                    return false
//                }
//
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                    val position = viewHolder.adapterPosition
//                    val item = myTransactionsListAdapter.getList()[position]
//                    val categoryPosition = UtilitiesFunctions.getCategoryBudgetsPosition(item.categoryName!!)
//
//                    // Swipe to remove transaction from database, subtract amount from category total transaction amount and subtract amount
//                    // all budgets total amount
//                    myTransactionsListAdapter.removeItem(position)
//                    //transactionViewModel.subtractBudgetAmount(categoryPosition, item.budgetAmount!!)
//                    //myBudgetsViewModel.removeBudget(item.budgetName!!, item.categoryName!!)
//                    //myBudgetsViewModel.removeFromTotalBudget(item.budgetAmount!!.toFloat())
//                    //categoryBudgetsViewModel.subtractFromCategoryTotalBudget(myBudgetCategoryData)
//
//
//                    if(myBudgetsCategoryAdapter.getList().isEmpty()){
//                        fragmentBudgetCategoryListBinding.tvRvNoBudgets.visibility = View.VISIBLE
//                        fragmentBudgetCategoryListBinding.rvMyBudgets.visibility = View.GONE
//                        fragmentBudgetCategoryListBinding.rvMyBudgets.itemAnimator = null
//                    } else{
//                        fragmentBudgetCategoryListBinding.rvMyBudgets.itemAnimator = DefaultItemAnimator()
//                    }
//
//                    // When click undo add budget back to database, add amount to category total budget amount and add amount
//                    // to all budgets total amount
//                    Snackbar.make(fragmentBudgetCategoryListBinding.root, "Budget deleted.", Snackbar.LENGTH_LONG).setAction(
//                        "Undo") {
//                        myBudgetsCategoryAdapter.addItem(item, position)
//                        budgetsAdapter.addToBudgetAmount(categoryPosition, item.budgetAmount!!)
//                        myBudgetsViewModel.addBudget(item)
//                        categoryBudgetsViewModel.addToCategoryTotalBudget(myBudgetCategoryData)
//                        myBudgetsViewModel.addToTotalBudget(item.budgetAmount!!.toFloat())
//                        fragmentBudgetCategoryListBinding.rvMyBudgets.visibility = View.VISIBLE
//                        fragmentBudgetCategoryListBinding.tvRvNoBudgets.visibility = View.GONE
//                    }.show()
//                }
//
//                override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
//                    return 0.2F
//                }
//
//                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
//                                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
//                    UtilitiesFunctions.setDeleteIcon(c, viewHolder, dX, requireContext())
//                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                }
//
//            }
//
//        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
//        itemTouchHelper.attachToRecyclerView(fragmentBudgetCategoryListBinding.rvMyBudgets)
//    }
}