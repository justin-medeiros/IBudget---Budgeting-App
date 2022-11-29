package com.example.app_expenses.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.data.MyBudgetData
import com.example.app_expenses.utils.UtilitiesFunctions

class MyBudgetsAdapter(): RecyclerView.Adapter<MyBudgetsAdapter.ViewHolder>() {
    private lateinit var context: Context
    val listOfBudgets: MutableList<MyBudgetData> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryMyBudgetsIcon: ImageView = itemView.findViewById(R.id.ivCategoryIconMyBudgets)
        val categoryMyBudgetsNameTitle = itemView.findViewById<TextView>(R.id.tvBudgetNameMyBudgets)
        val categoryMyBudgetCategoryTitle = itemView.findViewById<TextView>(R.id.tvBudgetCategoryMyBudgets)
        val categoryMyBudgetsAmount = itemView.findViewById<TextView>(R.id.tvBudgetAmountMyBudgets)
        val categoryMyBudgetContainer = itemView.findViewById<RelativeLayout>(R.id.containerMyBudgets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBudgetsAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.categories_budget, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: MyBudgetsAdapter.ViewHolder, position: Int) {
        val myBudget: MyBudgetData = listOfBudgets[position]
        holder.categoryMyBudgetsNameTitle.text = myBudget.budgetName
        holder.categoryMyBudgetCategoryTitle.text = myBudget.category?.categoryName ?: ""
        holder.categoryMyBudgetsAmount.text = myBudget.budgetAmount
        holder.categoryMyBudgetsIcon.background = ContextCompat.getDrawable(context, R.drawable.ic_cash)
        holder.categoryMyBudgetsIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, myBudget.category?.categoryColor!!))

        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            UtilitiesFunctions.convertDpToPixel(44.0F, context).toInt()
        )
        if(position != itemCount-1){
            relativeParams.setMargins(0, 0, 0, 10)
        }
        holder.categoryMyBudgetContainer.layoutParams = relativeParams
        holder.categoryMyBudgetContainer.requestLayout()
    }

    override fun getItemCount(): Int {
        return listOfBudgets.size
    }

    fun getList(): List<MyBudgetData>{
        return listOfBudgets
    }

    fun addAllItems(items: List<MyBudgetData>){
        this.listOfBudgets.removeAll(listOfBudgets)
        this.listOfBudgets.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: MyBudgetData, position: Int){
        this.listOfBudgets.add(position, item)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int){
        this.listOfBudgets.removeAt(position)
        notifyItemRemoved(position)
    }
}