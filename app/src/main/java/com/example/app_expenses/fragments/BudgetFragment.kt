package com.example.app_expenses.fragments

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
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
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.adapters.MyBudgetsAdapter
import com.example.app_expenses.data.MyBudgetData
import com.example.app_expenses.databinding.FragmentBudgetBinding
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.PrefsHelper
import com.example.app_expenses.utils.StringUtils
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.BudgetsViewModel
import com.google.android.material.snackbar.Snackbar


class BudgetFragment: Fragment() {
    private lateinit var fragmentBudgetBinding: FragmentBudgetBinding
    private var mainActivity: MainActivity = MainActivity()
    var myBudgetsCategoryAdapter = MyBudgetsAdapter()
    private val myBudgetsViewModel: BudgetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentBudgetBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_budget,
            container, false)
        myBudgetsViewModel.getMyBudgets()
        myBudgetsViewModel.getTotalBudget()
        myBudgetsViewModel.setTotalBudget() // Used to listen to when child added/removed
        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        fragmentBudgetBinding.rvBudget.layoutManager = myLinearLayoutManager
        fragmentBudgetBinding.rvBudget.adapter = myBudgetsCategoryAdapter
        setSwipeToDelete()
        return fragmentBudgetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myBudgetsViewModel.getMyBudgetsLiveData().observe(viewLifecycleOwner){ myBudgetsList->
            myBudgetsCategoryAdapter.addAllItems(myBudgetsList)
        }

        myBudgetsViewModel.getTotalBudgetLiveData().observe(viewLifecycleOwner){ totalBudget ->
            fragmentBudgetBinding.tvBudgetAmount.text = "$%.2f".format(totalBudget)
        }

        fragmentBudgetBinding.addNewBudgetButton.setOnClickListener {
            UtilitiesFunctions.replaceFragment(requireActivity(), AddBudgetFragment(myBudgetsCategoryAdapter), R.id.relativeLayoutMainActivity, true)
            mainActivity.hideTabBarVisibility()
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
                    myBudgetsViewModel.removeBudget(item.budgetName!!)
                    Snackbar.make(fragmentBudgetBinding.root, "Budget deleted.", Snackbar.LENGTH_LONG).setAction(
                        "Undo") {
                        myBudgetsCategoryAdapter.addItem(item, position)
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
        itemTouchHelper.attachToRecyclerView(fragmentBudgetBinding.rvBudget)
    }

    private fun setDeleteIcon(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                              dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean){
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val background = GradientDrawable()
        val deleteDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_trash_can)
        val itemView = viewHolder.itemView

        if(dX == 0F){
            c.drawRect(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), clearPaint)
            return
        }

        background.color = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.red_bright))
        background.cornerRadius = UtilitiesFunctions.convertDpToPixel(10F, requireContext())
        background.setBounds(itemView.right + dX.toInt() - 20, itemView.top, itemView.right, itemView.bottom)
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