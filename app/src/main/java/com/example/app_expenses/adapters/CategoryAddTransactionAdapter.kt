package com.example.app_expenses.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions

class CategoryAddTransactionAdapter(private val listCategories: MutableList<CategoryEnum>): RecyclerView.Adapter<CategoryAddTransactionAdapter.ViewHolder>() {
    var rowIndex: Int = -1
    var isCategorySelected: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon: ImageView = itemView.findViewById(R.id.imageViewIconAddTransaction)
        val categoryTitle: TextView = itemView.findViewById(R.id.tvTitleAddTransaction)
        val categoryContainer: LinearLayout = itemView.findViewById(R.id.linearLayoutContainerAddTransaction)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAddTransactionAdapter.ViewHolder {
        context = parent.context
        val categoryView =
            LayoutInflater.from(context).inflate(R.layout.categories_add_transaction, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(
        holder: CategoryAddTransactionAdapter.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val category: CategoryEnum = listCategories[position]

        holder.categoryTitle.text = category.categoryName
        holder.categoryIcon.background = ContextCompat.getDrawable(context, category.categoryIcon!!)

        holder.categoryContainer.setOnClickListener(View.OnClickListener {
            isCategorySelected.value = true
            rowIndex = position
            notifyDataSetChanged()
        })
        if (rowIndex === position) {
            holder.categoryContainer.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, category.categoryColor!!))
            holder.categoryTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    category.categoryColor!!
                )
            )
            holder.categoryIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, category.categoryColor!!))
        } else {
            holder.categoryContainer.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.foreground_primary_50))
            holder.categoryTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.foreground_primary_50
                )
            )
            holder.categoryIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.foreground_primary_50))
        }

        val relativeParams = RelativeLayout.LayoutParams(
            UtilitiesFunctions.convertDpToPixel(84.0F, context).toInt(),
            UtilitiesFunctions.convertDpToPixel(84.0F, context).toInt()
        )
        if (position != itemCount - 1) {
            relativeParams.setMargins(0, 0, 14, 0)

        }
        holder.categoryContainer.layoutParams = relativeParams
        holder.categoryContainer.requestLayout()
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }
}
