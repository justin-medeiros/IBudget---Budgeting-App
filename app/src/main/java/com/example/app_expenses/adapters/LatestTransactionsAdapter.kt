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
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.utils.UtilitiesFunctions

class LatestTransactionsAdapter(): RecyclerView.Adapter<LatestTransactionsAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfLatestTransactions: MutableList<TransactionData> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIcon: ImageView = itemView.findViewById(R.id.ivIconItemGraph)
        val itemTitle: TextView = itemView.findViewById(R.id.tvTitleItemGraph)
        val itemAmount: TextView = itemView.findViewById(R.id.tvAmountItemGraph)
        val itemContainer: RelativeLayout = itemView.findViewById(R.id.containerItemGraph)
        val borderItemContainer: RelativeLayout = itemView.findViewById(R.id.borderContainerItemGraph)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestTransactionsAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.categories_graph, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: LatestTransactionsAdapter.ViewHolder, position: Int) {
        val latestTransaction: TransactionData = listOfLatestTransactions[position]
        val category = UtilitiesFunctions.getCategoryEnum(latestTransaction.categoryName!!)
        holder.itemTitle.text = latestTransaction.transactionName
        holder.itemAmount.text = UtilitiesFunctions.formatNumber(latestTransaction.transactionAmount!!.toFloat())
        holder.itemTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.itemAmount.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.itemIcon.background = ContextCompat.getDrawable(context, category?.categoryIcon!!)
        holder.itemIcon.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.white))

        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            UtilitiesFunctions.convertDpToPixel(60.0F, context).toInt()
        )
        if(position != itemCount-1){
            relativeParams.setMargins(0, 0, 0, 10)
        }
        holder.borderItemContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
        holder.borderItemContainer.layoutParams = relativeParams
        holder.itemContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.foreground_primary))
        holder.itemContainer.requestLayout()

    }

    override fun getItemCount(): Int {
        return listOfLatestTransactions.size
    }

    fun replaceAll(latestTransactionsList: List<TransactionData>){
        listOfLatestTransactions.clear()
        listOfLatestTransactions.addAll(latestTransactionsList)
        notifyDataSetChanged()
    }
}