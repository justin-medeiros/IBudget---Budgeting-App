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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.activities.MainActivity
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.enums.CategoryEnum
import com.example.app_expenses.fragments.BudgetCategoryListFragment
import com.example.app_expenses.utils.UtilitiesFunctions

class BudgetsAdapter(): RecyclerView.Adapter<BudgetsAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listOfCategoryBudgets: MutableList<CategoryData> = mutableListOf()
    private val mainActivity = MainActivity()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryMyBudgetsIcon: ImageView = itemView.findViewById(R.id.ivCategoryIconMyBudgets)
        val categoryMyBudgetsNameTitle: TextView = itemView.findViewById(R.id.tvBudgetNameMyBudgets)
        val categoryMyBudgetsAmount: TextView = itemView.findViewById(R.id.tvBudgetAmountMyBudgets)
        val categoryMyBudgetContainer: RelativeLayout = itemView.findViewById(R.id.containerMyBudgets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetsAdapter.ViewHolder {
        context = parent.context
        val categoryView = LayoutInflater.from(context).inflate(R.layout.categories_budget, parent, false)
        return ViewHolder(categoryView)
    }

    override fun onBindViewHolder(holder: BudgetsAdapter.ViewHolder, position: Int) {
        val categoryBudget: CategoryData = listOfCategoryBudgets[position]
        val category = UtilitiesFunctions.getCategoryEnum(categoryBudget.categoryName!!)
        holder.categoryMyBudgetsNameTitle.text = category?.categoryName
        holder.categoryMyBudgetsAmount.text = "$%.2f".format(categoryBudget.totalAmount!!.toFloat())
        holder.categoryMyBudgetsIcon.background = ContextCompat.getDrawable(context, category?.categoryIcon!!)
        holder.categoryMyBudgetsIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))

        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            UtilitiesFunctions.convertDpToPixel(60.0F, context).toInt()
        )
        if(position != itemCount-1){
            relativeParams.setMargins(0, 0, 0, 10)
        }
        holder.categoryMyBudgetContainer.layoutParams = relativeParams
        holder.categoryMyBudgetContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, category?.categoryColor!!))
        holder.categoryMyBudgetContainer.requestLayout()

        holder.categoryMyBudgetContainer.setOnClickListener { view ->
            loadFragment(view, category)
            mainActivity.hideTabBarVisibility()
        }
    }

    override fun getItemCount(): Int {
        return listOfCategoryBudgets.size
    }

    fun replaceBudgetAmount(position: Int, categoryBudgetAmount: String){
        listOfCategoryBudgets[position].totalAmount = categoryBudgetAmount
        notifyItemChanged(position)
    }

    fun addToBudgetAmount(position: Int, categoryBudgetAmount: String){
        val newTotal = listOfCategoryBudgets[position].totalAmount!!.toFloat() + categoryBudgetAmount.toFloat()
        listOfCategoryBudgets[position].totalAmount = newTotal.toString()
        notifyItemChanged(position)
    }

    fun subtractBudgetAmount(position: Int, categoryBudgetAmount: String){
        val newTotal = listOfCategoryBudgets[position].totalAmount!!.toFloat() - categoryBudgetAmount.toFloat()
        listOfCategoryBudgets[position].totalAmount = newTotal.toString()
        notifyItemChanged(position)
    }

    fun addAll(categoryBudgetList: MutableCollection<CategoryData>){
        listOfCategoryBudgets.addAll(categoryBudgetList)
        notifyDataSetChanged()
    }

    private fun loadFragment(view: View, category: CategoryEnum){
        val fragment = BudgetCategoryListFragment(category, this)
        (view.context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.relativeLayoutMainActivity, fragment).addToBackStack(fragment.javaClass.name)
            .commit()
    }
}