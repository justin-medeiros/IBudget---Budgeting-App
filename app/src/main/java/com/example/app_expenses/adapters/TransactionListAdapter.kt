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
import com.example.app_expenses.data.BudgetData
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions

class TransactionListAdapter(): RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfTransactions: MutableList<TransactionData> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionName: TextView = itemView.findViewById(R.id.tvTransactionNameTransactionList)
        val transactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmountTransactionList)
        val transactionContainer: RelativeLayout = itemView.findViewById(R.id.containerTransactionListItem)
        val transactionIcon: ImageView = itemView.findViewById(R.id.ivCategoryTransactionList)
        val dateText: TextView = itemView.findViewById(R.id.dateTitleTransactionList)
        val borderTop: RelativeLayout = itemView.findViewById(R.id.topBorderTransactionList)
        val borderBottom: RelativeLayout = itemView.findViewById(R.id.bottomBorderTransactionList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: TransactionListAdapter.ViewHolder, position: Int) {
        Log.e("HELLO", "YES")
        val myTransaction: TransactionData = listOfTransactions[position]
        val category: CategoryEnum? = UtilitiesFunctions.getCategoryEnum(myTransaction.categoryName!!)
        holder.transactionName.text = myTransaction.transactionName
        holder.transactionAmount.text = myTransaction.transactionAmount
        holder.transactionIcon.background = ContextCompat.getDrawable(context, category?.categoryIcon!!)

        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            UtilitiesFunctions.convertDpToPixel(48.0F, context).toInt()
        )
        if(position == itemCount-1){
            holder.borderBottom.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return listOfTransactions.size
    }

    fun getList(): List<TransactionData>{
        return listOfTransactions
    }

    fun addAllItems(items: List<TransactionData>){
        this.listOfTransactions.removeAll(listOfTransactions)
        this.listOfTransactions.addAll(items)
        Log.e("HELLO", items.toString())
        notifyDataSetChanged()
    }

    fun addItem(item: TransactionData, position: Int){
        this.listOfTransactions.add(position, item)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int){
        this.listOfTransactions.removeAt(position)
        notifyItemRemoved(position)
    }
}