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
import com.example.app_expenses.utils.PrefsHelper
import com.example.app_expenses.utils.StringUtils
import com.example.app_expenses.utils.UtilitiesFunctions

class TransactionListAdapter(): RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfTransactions: MutableList<TransactionData> = mutableListOf()
    private var latestDate: String? = null
    private var isAdded: Boolean? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionName: TextView = itemView.findViewById(R.id.tvTransactionNameTransactionList)
        val transactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmountTransactionList)
        val transactionContainer: RelativeLayout = itemView.findViewById(R.id.containerTransactionListItem)
        val transactionIcon: ImageView = itemView.findViewById(R.id.ivCategoryTransactionList)
        val dateText: TextView = itemView.findViewById(R.id.dateTitleTransactionList)
        val borderTop: RelativeLayout = itemView.findViewById(R.id.topBorderTransactionList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: TransactionListAdapter.ViewHolder, position: Int) {
        val myTransaction: TransactionData = listOfTransactions[position]
        val category: CategoryEnum? = UtilitiesFunctions.getCategoryEnum(myTransaction.categoryName!!)
        val transactionDate = UtilitiesFunctions.timestampToDate(myTransaction.timeStamp)
        holder.transactionName.text = myTransaction.transactionName
        holder.transactionAmount.text = myTransaction.transactionAmount
        holder.transactionIcon.background = ContextCompat.getDrawable(context, category?.categoryIcon!!)
        if(latestDate == null){
            latestDate = transactionDate
        } else {
            if(isAdded == true){
                if(position != 0 && latestDate == transactionDate){
                    holder.dateText.visibility = View.GONE
                    holder.borderTop.visibility = View.GONE
                }else{
                    latestDate = transactionDate
                }
            }
        }
        holder.dateText.text = latestDate


    }

    override fun getItemCount(): Int {
        return listOfTransactions.size
    }

    fun getList(): List<TransactionData>{
        return listOfTransactions
    }

    fun addAllItems(items: List<TransactionData>){
        this.listOfTransactions.clear()
        this.listOfTransactions.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: TransactionData, position: Int){
        isAdded = true
        this.listOfTransactions.add(position, item)
        notifyItemInserted(position)
        notifyItemChanged(position+1)
    }

    fun removeItem(position: Int){
        isAdded = false
        this.listOfTransactions.removeAt(position)
        notifyItemRemoved(position)
    }
}