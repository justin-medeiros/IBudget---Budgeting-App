package com.example.app_expenses.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.data.TransactionData
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.PrefsHelper
import com.example.app_expenses.utils.StringUtils
import com.example.app_expenses.utils.UtilitiesFunctions
import com.example.app_expenses.viewModels.TransactionsViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*


class TransactionListAdapter(private val activity: FragmentActivity): RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var transactionViewModel: TransactionsViewModel
    private val mainActivity = MainActivity()
    private val listOfTransactions: MutableList<TransactionData> = mutableListOf()
    private var itemsToRemove: TreeMap<Int, TransactionData> = TreeMap()
    private var latestDate: String? = null
    private var isAdded: Boolean = false
    private var isSelectButtonClicked = false
    private var itemsSelected = 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        transactionViewModel = ViewModelProvider(activity)[TransactionsViewModel::class.java]

        // Will execute every time the "Select" button is clicked in Transactions List page
        transactionViewModel.selectButtonClicked.observe(activity){ isClicked ->
            isSelectButtonClicked = isClicked
            isAdded = false
            notifyDataSetChanged()
        }

        // When the delete button is clicked in main activity, delete the transactions from database and adapter
        transactionViewModel.deleteButtonClicked.observe(activity){ clicked ->
            if(clicked && itemsToRemove.values.isNotEmpty()){
                transactionViewModel.amountOfItemsSelected.postValue(0)
                listOfTransactions.removeAll(itemsToRemove.values)
                mainActivity.hideDeleteButton()
                notifyDataSetChanged()
                if(!recyclerView.isComputingLayout){
                    showSnackbar(recyclerView.rootView)
                }
            }
        }
    }

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
        var isItemSelected = false

        holder.transactionName.text = myTransaction.transactionName
        holder.transactionAmount.text = UtilitiesFunctions.formatNumber(myTransaction.transactionAmount!!.toFloat())
        holder.transactionIcon.background = ContextCompat.getDrawable(context, category?.categoryIcon!!)

        showDates(holder, myTransaction, position)

        // Used to determine if "select" button was clicked on Transaction List page & accumulate transactions that have been clicked
        if(isSelectButtonClicked){
            holder.transactionContainer.isEnabled = true
            mainActivity.showDeleteButton()
            holder.transactionIcon.background = ContextCompat.getDrawable(context, R.drawable.ic_checkbox_blank_circle_outline)
            holder.transactionContainer.setOnClickListener {
                isItemSelected.let {
                    if(it){
                        itemsSelected--
                        itemsToRemove.remove(position)
                        holder.transactionIcon.background = ContextCompat.getDrawable(context, R.drawable.ic_checkbox_blank_circle_outline)
                    } else{
                        itemsToRemove[position] = myTransaction
                        itemsSelected++
                        holder.transactionIcon.background = ContextCompat.getDrawable(context, R.drawable.ic_check_circle_outline)
                    }
                    isItemSelected = !it
                }
                transactionViewModel.amountOfItemsSelected.postValue(itemsSelected)
            }
        } else{
            holder.transactionContainer.isEnabled = false
            mainActivity.hideDeleteButton()
            itemsSelected = 0
            transactionViewModel.amountOfItemsSelected.value = 0
        }

    }

    override fun getItemCount(): Int {
        return listOfTransactions.size
    }

    private fun showDates(holder: TransactionListAdapter.ViewHolder, transactionData: TransactionData, position: Int){
        val transactionDate = UtilitiesFunctions.timestampToDate(transactionData.timeStamp)
        val sharedPrefTransactionDate = PrefsHelper.readString(StringUtils.TRANSACTION_LATEST_DATE)
        if(position == 0){
            // For the first item in the list, show the date
            holder.dateText.visibility = View.VISIBLE
            holder.borderTop.visibility = View.VISIBLE
            latestDate = transactionDate
        } else {
            // If a value was added
            if(isAdded) {
                //If item transaction date matches the latest transaction date, do not show the date for that item. Instead the date will be pushed to the top
                if(sharedPrefTransactionDate == transactionDate){
                    holder.dateText.visibility = View.GONE
                    holder.borderTop.visibility = View.GONE
                    latestDate = transactionDate
                } else{
                    holder.dateText.visibility = View.VISIBLE
                    holder.borderTop.visibility = View.VISIBLE
                }
            } else{ // For when app first launches, select button is clicked or when transactions are removed from list
                if (latestDate == transactionDate) {
                    holder.dateText.visibility = View.GONE
                    holder.borderTop.visibility = View.GONE
                } else {
                    holder.dateText.visibility = View.VISIBLE
                    holder.borderTop.visibility = View.VISIBLE
                    latestDate = transactionDate
                }
            }
        }
        holder.dateText.text = latestDate
    }

    fun addAllItems(items: MutableCollection<TransactionData>){
        this.listOfTransactions.clear()
        this.listOfTransactions.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: TransactionData, position: Int){
        isAdded = true
        this.listOfTransactions.add(position, item)
        // Will notify item changed of the item that was previously at the top (position+1) to make sure the item does not show the date. Instead the
        // newly added transaction will show the date for the section.
        transactionViewModel.addToTransactionsTotal(item.transactionAmount!!.toFloat(), UtilitiesFunctions.timestampToMonthYear(item.timeStamp))
        transactionViewModel.addToCategoryTransactionsTotal(item.categoryName!!, item.transactionAmount!!.toFloat(),
            UtilitiesFunctions.timestampToMonthYear(item.timeStamp))
        notifyItemInserted(position)
        notifyItemChanged(position+1)
    }

    private fun showSnackbar(view: View){
        val tempList = itemsToRemove.clone() as TreeMap<Int, TransactionData>
        transactionViewModel.numberOfTransactions.postValue(listOfTransactions.size)

        // Removing transactions and transactions total amounts from database
        transactionViewModel.removeTransactions(itemsToRemove.values)
        removeTotalTransactionsAmount()
        removeFromCategoryTransactionTotalAmount()

        itemsToRemove.clear()
        val snackBar = Snackbar.make(view, "${tempList.size} transactions deleted.", Snackbar.LENGTH_LONG)
        val snackbarView = snackBar.view

        val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout
        val customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null)
        val snackIcon = customView.findViewById<ImageView>(R.id.snackbarIcon)
        val snackText = customView.findViewById<TextView>(R.id.snackbarText)
        val snackAction = customView.findViewById<TextView>(R.id.snackbarActionText)

        snackAction.setOnClickListener {
            // Add all transactions back after clicking undo
            tempList.forEach { item ->
                listOfTransactions.add(item.key, item.value)
            }
            transactionViewModel.addAllTransactions(tempList.values)
            addTotalTransactionsAmount(tempList.values)
            addToCategoryTransactionTotalAmount(tempList.values)
            transactionViewModel.numberOfTransactions.postValue(listOfTransactions.size)
            notifyDataSetChanged()
            snackBar.dismiss()
        }

        snackIcon.background = ContextCompat.getDrawable(context, R.drawable.ic_trash_can)
        snackText.text = "${tempList.size} transactions deleted."

        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
        params.setMargins(params.leftMargin, params.topMargin + 70, params.rightMargin, params.bottomMargin)
        params.gravity = Gravity.TOP
        snackbarView.layoutParams = params

        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customView)

        snackBar.show()
    }

    private fun removeTotalTransactionsAmount(){
        var tempMap: MutableMap<String, Float> = mutableMapOf()
        itemsToRemove.forEach { item ->
            val itemMonth = UtilitiesFunctions.timestampToMonthYear(item.value.timeStamp)
            if(tempMap.containsKey(itemMonth)){
                tempMap[itemMonth] = tempMap[itemMonth]!!.plus(item.value.transactionAmount!!.toFloat())
            } else{
                tempMap[itemMonth] = item.value.transactionAmount!!.toFloat()
            }
        }
        tempMap.forEach { item -> transactionViewModel.subtractFromTransactionsTotal(item.value, item.key)}

    }

    private fun addTotalTransactionsAmount(tempList: MutableCollection<TransactionData>){
        var tempMap: MutableMap<String, Float> = mutableMapOf()
        tempList.forEach { item ->
            val itemMonth = UtilitiesFunctions.timestampToMonthYear(item.timeStamp)
            if(tempMap.containsKey(itemMonth)){
                tempMap[itemMonth] = tempMap[itemMonth]!!.plus(item.transactionAmount!!.toFloat())
            } else{
                tempMap[itemMonth] = item.transactionAmount!!.toFloat()
            }
        }
        tempMap.forEach { item -> transactionViewModel.addToTransactionsTotal(item.value, item.key)}

    }

    private fun removeFromCategoryTransactionTotalAmount(){
        val transactionsByDate: MutableMap<String, MutableMap<String, Float>> = mutableMapOf() // Will store the category name and data by the date the item was added
        val transactionsByCategory: MutableMap<String, Float> = mutableMapOf()

        itemsToRemove.forEach { item->
            val categoryName = item.value.categoryName!!
            val itemMonth = UtilitiesFunctions.timestampToMonthYear(item.value.timeStamp)
            if (transactionsByCategory.containsKey(categoryName)){
                transactionsByCategory[categoryName] = transactionsByCategory.getValue(categoryName) + item.value.transactionAmount!!.toFloat()
            } else {
                transactionsByCategory[categoryName] = item.value.transactionAmount!!.toFloat()
            }
            transactionsByDate[itemMonth] = transactionsByCategory // Add category name and category total to the month the item was added
        }
        transactionsByDate.forEach { removeItemDate ->
            removeItemDate.value.forEach { removeItemByCategory ->
                transactionViewModel.subtractFromCategoryTransactionsTotal(removeItemByCategory.key,
                    removeItemByCategory.value, removeItemDate.key)} // Will store category total by month it was added in database
            }

    }

    private fun addToCategoryTransactionTotalAmount(listOfTransactions: MutableCollection<TransactionData>){
        val transactionsByDate: MutableMap<String, MutableMap<String, Float>> = mutableMapOf() // Will store the category name and data by the date the item was added
        val transactionsByCategory: MutableMap<String, Float> = mutableMapOf()

        listOfTransactions.forEach { item->
            val categoryName = item.categoryName!!
            val itemMonth = UtilitiesFunctions.timestampToMonthYear(item.timeStamp)
            if (transactionsByCategory.containsKey(categoryName)){
                transactionsByCategory[categoryName] = transactionsByCategory.getValue(categoryName) + item.transactionAmount!!.toFloat()
            } else {
                transactionsByCategory[categoryName] = item.transactionAmount!!.toFloat()
            }
            transactionsByDate[itemMonth] = transactionsByCategory // Add category name and category total to the month the item was added
        }
        transactionsByDate.forEach { removeItemDate ->
            removeItemDate.value.forEach { removeItemByCategory ->
                transactionViewModel.addToCategoryTransactionsTotal(removeItemByCategory.key,
                    removeItemByCategory.value, removeItemDate.key)} // Will store category total by month it was added in database
        }

    }

}