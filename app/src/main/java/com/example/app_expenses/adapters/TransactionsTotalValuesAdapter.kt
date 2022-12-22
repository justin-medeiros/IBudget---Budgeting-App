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
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.utils.UtilitiesFunctions

class TransactionsTotalValuesAdapter(): RecyclerView.Adapter<TransactionsTotalValuesAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfTransactions: MutableList<CategoryData> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val graphItemIcon: ImageView = itemView.findViewById(R.id.ivIconItemGraph)
        val graphItemTitle: TextView = itemView.findViewById(R.id.tvTitleItemGraph)
        val graphItemAmount: TextView = itemView.findViewById(R.id.tvAmountItemGraph)
        val graphItemContainer: RelativeLayout = itemView.findViewById(R.id.containerItemGraph)
        val borderItemContainer: RelativeLayout = itemView.findViewById(R.id.borderContainerItemGraph)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsTotalValuesAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.categories_graph, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: TransactionsTotalValuesAdapter.ViewHolder, position: Int) {
        val transaction: CategoryData = listOfTransactions[position]
        val category = UtilitiesFunctions.getCategoryEnum(transaction.categoryName!!)
        holder.graphItemTitle.text = transaction.categoryName
        holder.graphItemAmount.text = "$%.2f".format(transaction.totalAmount!!.toFloat())
        holder.graphItemTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.graphItemAmount.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.graphItemIcon.background = ContextCompat.getDrawable(context, category?.categoryIcon!!)
        holder.graphItemIcon.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context,
                R.color.white))

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
        return listOfTransactions.size
    }

    fun replaceAll(latestTransactionsList: Collection<CategoryData>){
        listOfTransactions.clear()
        listOfTransactions.addAll(latestTransactionsList)
        notifyDataSetChanged()
    }
}