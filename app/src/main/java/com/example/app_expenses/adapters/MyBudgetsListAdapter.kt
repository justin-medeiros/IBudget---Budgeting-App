package com.example.app_expenses.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions

class MyBudgetsListAdapter(): RecyclerView.Adapter<MyBudgetsListAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfBudgets: MutableList<BudgetData> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryMyBudgetsNameTitle: TextView = itemView.findViewById(R.id.tvBudgetNameMyBudgets)
        val categoryMyBudgetsAmount: TextView = itemView.findViewById(R.id.tvBudgetAmountMyBudgets)
        val categoryMyBudgetContainer: RelativeLayout = itemView.findViewById(R.id.containerMyBudgets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBudgetsListAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.categories_mybudget_list, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: MyBudgetsListAdapter.ViewHolder, position: Int) {
        val myBudget: BudgetData = listOfBudgets[position]
        val category: CategoryEnum? = UtilitiesFunctions.getCategoryEnum(myBudget.categoryName!!)
        holder.categoryMyBudgetsNameTitle.text = myBudget.budgetName
        holder.categoryMyBudgetsAmount.text = myBudget.budgetAmount

        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            UtilitiesFunctions.convertDpToPixel(48.0F, context).toInt()
        )
        if(position != itemCount-1){
            relativeParams.setMargins(0, 0, 0, 10)
        }
        holder.categoryMyBudgetContainer.layoutParams = relativeParams
        holder.categoryMyBudgetContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, category?.categoryColor!!))
        holder.categoryMyBudgetContainer.requestLayout()
    }

    override fun getItemCount(): Int {
        return listOfBudgets.size
    }

    fun getList(): List<BudgetData>{
        return listOfBudgets
    }

    fun addAllItems(items: List<BudgetData>){
        this.listOfBudgets.removeAll(listOfBudgets)
        this.listOfBudgets.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: BudgetData, position: Int){
        this.listOfBudgets.add(position, item)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int){
        this.listOfBudgets.removeAt(position)
        notifyItemRemoved(position)
    }
}