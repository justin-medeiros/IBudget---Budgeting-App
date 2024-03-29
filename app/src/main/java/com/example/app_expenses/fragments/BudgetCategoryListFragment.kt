package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.BudgetsAdapter
import com.example.app_expenses.adapters.MyBudgetsListAdapter
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.databinding.FragmentMybudgetListBinding
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.BudgetsViewModel
import com.example.app_expenses.viewModels.CategoryBudgetsViewModel
import com.google.android.material.snackbar.Snackbar

class BudgetCategoryListFragment(private val category: CategoryEnum, private val budgetsAdapter: BudgetsAdapter): Fragment() {
    private lateinit var fragmentBudgetCategoryListBinding: FragmentMybudgetListBinding
    private var mainActivity: MainActivity = MainActivity()
    var myBudgetsCategoryAdapter = MyBudgetsListAdapter()
    private val myBudgetsViewModel: BudgetsViewModel by viewModels()
    private val categoryBudgetsViewModel: CategoryBudgetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentBudgetCategoryListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mybudget_list,
            container, false)
        myBudgetsViewModel.getMyBudgets(category)
        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentBudgetCategoryListBinding.rvMyBudgets.layoutManager = myLinearLayoutManager
        fragmentBudgetCategoryListBinding.rvMyBudgets.adapter = myBudgetsCategoryAdapter
        setSwipeToDelete()
        return fragmentBudgetCategoryListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBudgetCategoryListBinding.myBudgetListNavTitle.text = category.categoryName
        fragmentBudgetCategoryListBinding.mybudgetsListNavBar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), category.categoryColor!!))

        myBudgetsViewModel.getMyBudgetsLiveData().observe(viewLifecycleOwner){ categoryBudgetsList ->
            if(categoryBudgetsList.isEmpty()){
                fragmentBudgetCategoryListBinding.tvRvNoBudgets.visibility = View.VISIBLE
                fragmentBudgetCategoryListBinding.rvMyBudgets.visibility = View.GONE
            } else{
                fragmentBudgetCategoryListBinding.rvMyBudgets.visibility = View.VISIBLE
                fragmentBudgetCategoryListBinding.tvRvNoBudgets.visibility = View.GONE
                myBudgetsCategoryAdapter.addAllItems(categoryBudgetsList)
            }
        }

        fragmentBudgetCategoryListBinding.mybudgetsListBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
            mainActivity.visibleTabBarVisibility()
        }
    }

    private fun setSwipeToDelete(){
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = myBudgetsCategoryAdapter.getList()[position]
                    val myCategoryData = CategoryData(item.categoryName, item.budgetAmount)
                    val categoryPosition = UtilitiesFunctions.getCategoryBudgetsPosition(item.categoryName!!)

                    // Swipe to remove budget from database, subtract amount from category total budget amount and subtract amount
                    // all budgets total amount
                    myBudgetsCategoryAdapter.removeItem(position)
                    budgetsAdapter.subtractBudgetAmount(categoryPosition, item.budgetAmount!!)
                    myBudgetsViewModel.removeBudget(item.budgetName!!, item.categoryName!!)
                    myBudgetsViewModel.removeFromTotalBudget(item.budgetAmount!!.toFloat())
                    categoryBudgetsViewModel.subtractFromCategoryTotalBudget(myCategoryData)


                    if(myBudgetsCategoryAdapter.getList().isEmpty()){
                        fragmentBudgetCategoryListBinding.tvRvNoBudgets.visibility = View.VISIBLE
                        fragmentBudgetCategoryListBinding.rvMyBudgets.visibility = View.GONE
                        fragmentBudgetCategoryListBinding.rvMyBudgets.itemAnimator = null
                    } else{
                        fragmentBudgetCategoryListBinding.rvMyBudgets.itemAnimator = DefaultItemAnimator()
                    }

                    val snackBar = Snackbar.make(fragmentBudgetCategoryListBinding.root, "Budget deleted.", Snackbar.LENGTH_LONG)
                    val snackbarView = snackBar.view
                    val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout
                    val customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null)
                    val snackIcon = customView.findViewById<ImageView>(R.id.snackbarIcon)
                    val snackText = customView.findViewById<TextView>(R.id.snackbarText)
                    val snackAction = customView.findViewById<TextView>(R.id.snackbarActionText)

                    // When click undo add budget back to database, add amount to category total budget amount and add amount
                    // to all budgets total amount
                    snackAction.setOnClickListener {
                        myBudgetsCategoryAdapter.addItem(item, position)
                        budgetsAdapter.addToBudgetAmount(categoryPosition, item.budgetAmount!!)
                        myBudgetsViewModel.addBudget(item)
                        categoryBudgetsViewModel.addToCategoryTotalBudget(myCategoryData)
                        myBudgetsViewModel.addToTotalBudget(item.budgetAmount!!.toFloat())
                        fragmentBudgetCategoryListBinding.rvMyBudgets.visibility = View.VISIBLE
                        fragmentBudgetCategoryListBinding.tvRvNoBudgets.visibility = View.GONE
                        snackBar.dismiss()
                    }

                    snackIcon.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash_can)
                    snackText.text = "Budget deleted."

                    val params = snackbarView.layoutParams as FrameLayout.LayoutParams
                    params.setMargins(params.leftMargin, params.topMargin + 30, params.rightMargin, params.bottomMargin)
                    params.gravity = Gravity.TOP
                    snackbarView.layoutParams = params

                    snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
                    snackbarLayout.setPadding(0, 0, 0, 0)
                    snackbarLayout.addView(customView)

                    snackBar.show()
                }

                override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                    return 0.2F
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    UtilitiesFunctions.setDeleteIcon(c, viewHolder, dX, requireContext())
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(fragmentBudgetCategoryListBinding.rvMyBudgets)
    }

}