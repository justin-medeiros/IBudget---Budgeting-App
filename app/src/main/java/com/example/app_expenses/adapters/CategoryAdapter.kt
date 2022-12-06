package com.example.app_expenses.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.utils.UtilitiesFunctions


class CategoryAdapter(private val listCategories: MutableList<CategoryEnum>): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    var rowIndex: Int = -1
    var isCategorySelected: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon = itemView.findViewById<ImageView>(R.id.imageViewCategoriesContainer)
        val categoryTitle = itemView.findViewById<TextView>(R.id.titleCategories)
        val strokeCategoryContainer = itemView.findViewById<RelativeLayout>(R.id.strokeCategoryContainer)
        val categoryContainer = itemView.findViewById<RelativeLayout>(R.id.categoryContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.categories_add_budget, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val category: CategoryEnum = listCategories[position]

        holder.categoryTitle.text = category.categoryName
        holder.categoryIcon.background = ContextCompat.getDrawable(context, category.categoryIcon!!)

        holder.categoryContainer.setOnClickListener{
            isCategorySelected.value = true
            rowIndex = position
            notifyDataSetChanged()
        }
        if (rowIndex === position) {
            holder.strokeCategoryContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, category.categoryColor!!))
            holder.categoryTitle.setTextColor(ContextCompat.getColor(context, category.categoryColor!!))
            holder.categoryIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, category.categoryColor!!))
        } else {
            holder.strokeCategoryContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.background_primary))
            holder.categoryTitle.setTextColor(ContextCompat.getColor(context, R.color.background_primary))
            holder.categoryIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.background_primary))
        }

        val relativeParams = RelativeLayout.LayoutParams(
            UtilitiesFunctions.convertDpToPixel(130.0F, context).toInt(),
            UtilitiesFunctions.convertDpToPixel(130.0F, context).toInt()
        )
        if(position != itemCount-1){
            relativeParams.setMargins(0, 0, 10, 0)

        }
        holder.strokeCategoryContainer.layoutParams = relativeParams
        holder.strokeCategoryContainer.requestLayout()

    }

    override fun getItemCount(): Int {
        return listCategories.size
    }
}