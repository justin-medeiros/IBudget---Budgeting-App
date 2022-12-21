package com.example.app_expenses.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.fragments.BudgetCategoryListFragment
import com.example.app_expenses.utils.UtilitiesFunctions

class LatestBudgetsAdapter(): RecyclerView.Adapter<LatestBudgetsAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfLatestBudgets: MutableList<BudgetData> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val graphItemIcon: ImageView = itemView.findViewById(R.id.ivIconItemGraph)
        val graphItemTitle: TextView = itemView.findViewById(R.id.tvTitleItemGraph)
        val graphItemAmount: TextView = itemView.findViewById(R.id.tvAmountItemGraph)
        val graphItemContainer: RelativeLayout = itemView.findViewById(R.id.containerItemGraph)
        val borderItemContainer: RelativeLayout = itemView.findViewById(R.id.borderContainerItemGraph)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestBudgetsAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.categories_graph, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: LatestBudgetsAdapter.ViewHolder, position: Int) {
        val latestBudget: BudgetData = listOfLatestBudgets[position]
        val category = UtilitiesFunctions.getCategoryEnum(latestBudget.categoryName!!)
        holder.graphItemTitle.text = latestBudget.budgetName
        holder.graphItemAmount.text = "$%.2f".format(latestBudget.budgetAmount!!.toFloat())
        holder.graphItemTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.graphItemAmount.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.graphItemIcon.background = ContextCompat.getDrawable(context, category?.categoryIcon!!)
        holder.graphItemIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white))

        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            UtilitiesFunctions.convertDpToPixel(60.0F, context).toInt()
        )
        if(position != itemCount-1){
            relativeParams.setMargins(0, 0, 0, 10)
        }
        holder.borderItemContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,category?.categoryColor!!))
        holder.borderItemContainer.layoutParams = relativeParams
        holder.graphItemContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.background_secondary))
        holder.graphItemContainer.requestLayout()

    }

    override fun getItemCount(): Int {
        return listOfLatestBudgets.size
    }

    fun replaceAll(latestBudgetsList: List<BudgetData>){
        listOfLatestBudgets.clear()
        listOfLatestBudgets.addAll(latestBudgetsList)
        notifyDataSetChanged()
    }
}