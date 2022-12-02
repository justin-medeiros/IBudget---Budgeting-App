package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.CustomItemAnimator
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.BudgetsAdapter
import com.example.app_expenses.adapters.MyBudgetsListAdapter
import com.example.app_expenses.data.BudgetCategoryData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.databinding.FragmentMybudgetListBinding
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.BudgetsViewModel
import com.example.app_expenses.viewModels.CategoryBudgetsViewModel
import com.google.android.material.snackbar.Snackbar

class BudgetCategoryListFragment(private val category: CategoryEnum): Fragment() {
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
        fragmentBudgetCategoryListBinding.rvMyBudgets.itemAnimator = CustomItemAnimator()
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
            myBudgetsCategoryAdapter.addAllItems(categoryBudgetsList)
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
                    myBudgetsCategoryAdapter.removeItem(position)
                    myBudgetsViewModel.removeBudget(item.budgetName!!, item.categoryName!!)
                    myBudgetsViewModel.removeFromTotalBudget(item.budgetAmount!!.toFloat())
                    Snackbar.make(fragmentBudgetCategoryListBinding.root, "Budget deleted.", Snackbar.LENGTH_LONG).setAction(
                        "Undo") {
                        myBudgetsCategoryAdapter.addItem(item, position)
                        myBudgetsViewModel.addBudget(item)
                        categoryBudgetsViewModel.addToCategoryTotalBudget(BudgetCategoryData(item.categoryName, item.budgetAmount))
                        myBudgetsViewModel.addToTotalBudget(item.budgetAmount!!.toFloat())
                    }.show()
                }

                override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                    return 0.2F
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(fragmentBudgetCategoryListBinding.rvMyBudgets)
    }

    private fun setDeleteIcon(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                              dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean){
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val background = GradientDrawable()
        val deleteDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash_can)
        val itemView = viewHolder.itemView

        if(dX == 0F){
            c.drawRoundRect(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), 10F, 10F, clearPaint)
            return
        }

        background.color = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.red_bright))
        background.cornerRadius = UtilitiesFunctions.convertDpToPixel(10F, requireContext())

        if(dX <= -540.0F){
            background.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
        } else{
            background.setBounds(itemView.right + dX.toInt() - 20, itemView.top, itemView.right, itemView.bottom)
        }

        background.draw(c)

        val deleteIconTop = itemView.top + (itemView.height - deleteDrawable!!.intrinsicHeight) / 2
        val deleteIconMargin = (itemView.height - deleteDrawable!!.intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - deleteDrawable!!.intrinsicHeight
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + deleteDrawable!!.intrinsicHeight

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)
    }
}