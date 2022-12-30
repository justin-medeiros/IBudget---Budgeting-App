package com.example.app_expenses.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.data.BudgetGoalData
import com.example.app_expenses.utils.UtilitiesFunctions
import com.google.android.material.progressindicator.LinearProgressIndicator

class BudgetGoalAdapter(): RecyclerView.Adapter<BudgetGoalAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfBudgetGoalItems: MutableList<BudgetGoalData> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemProgressBar: LinearProgressIndicator = itemView.findViewById(R.id.progressBarBudgetGoal)
        val itemTitle: TextView = itemView.findViewById(R.id.budgetGoalsItemTitle)
        val itemPercentage: TextView = itemView.findViewById(R.id.budgetGoalsItemPercentage)
        val itemContainer: LinearLayout = itemView.findViewById(R.id.budgetGoalsItemContainer)
        val itemSpent: TextView = itemView.findViewById(R.id.currentSpentTotalItem)
        val itemTotalBudget: TextView = itemView.findViewById(R.id.totalBudgetItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetGoalAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.budget_goals_items, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: BudgetGoalAdapter.ViewHolder, position: Int) {
        val budgetGoalItem: BudgetGoalData = listOfBudgetGoalItems[position]
        val category = UtilitiesFunctions.getCategoryEnum(budgetGoalItem.categoryName!!)
        val totalCategoryBudget = budgetGoalItem.totalCategoryBudget
        val totalSpent = budgetGoalItem.totalCategoryTransaction
        val percentage = UtilitiesFunctions.calculateTotalPercentage(totalSpent!!, totalCategoryBudget!!) * 100

        holder.itemTitle.text = budgetGoalItem.categoryName
        holder.itemTotalBudget.text = "/${UtilitiesFunctions.formatNumber(budgetGoalItem.totalCategoryBudget)}"
        holder.itemSpent.text = UtilitiesFunctions.formatNumber(budgetGoalItem.totalCategoryTransaction)
        holder.itemPercentage.text = formatPercentage(percentage)
        holder.itemProgressBar.setProgressCompat(percentage.toInt(), true)

        if(totalSpent <= totalCategoryBudget){
            holder.itemProgressBar.setIndicatorColor(ContextCompat.getColor(context, category?.categoryColor!!))
            holder.itemTotalBudget.setTextColor(ContextCompat.getColor(context, category?.categoryColor!!))
            holder.itemPercentage.setTextColor(ContextCompat.getColor(context, R.color.white))
            if(totalSpent != 0f && percentage.toInt() == 0){
                holder.itemProgressBar.setProgressCompat(1, true)
                holder.itemPercentage.text = "1%"
            }
        } else{
            if(totalCategoryBudget == 0f){
                holder.itemPercentage.text = "N/A"
            }
            holder.itemProgressBar.setIndicatorColor(ContextCompat.getColor(context, R.color.red_bright))
            holder.itemTotalBudget.setTextColor(ContextCompat.getColor(context, R.color.red_bright))
            holder.itemPercentage.setTextColor(ContextCompat.getColor(context, R.color.red_bright))
        }



        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            UtilitiesFunctions.convertDpToPixel(60.0F, context).toInt()
        )
        if(position != itemCount-1){
            relativeParams.setMargins(0, 0, 0, 15)
        }
        holder.itemContainer.layoutParams = relativeParams
        holder.itemContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.background_secondary))
        holder.itemContainer.requestLayout()

    }

    override fun getItemCount(): Int {
        return listOfBudgetGoalItems.size
    }

    fun replaceAll(listOfBudgetGoalData: List<BudgetGoalData>){
        listOfBudgetGoalItems.clear()
        listOfBudgetGoalItems.addAll(listOfBudgetGoalData)
        notifyDataSetChanged()
    }

    private fun formatPercentage(number: Float): String{
        return if(number >= 1000000000){
            "$%dB%%".format((number / 1000000000.00).toInt())
        } else if(number >= 1000000){
            "$%dM%%".format((number / 1000000.00).toInt())
        } else if(number >= 1000){
            "%dk%%".format((number/1000.00).toInt())
        } else{
            "%d%%".format(number.toInt())
        }
    }
}