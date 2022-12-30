package com.example.app_expenses.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.app_expenses.R
import com.example.app_expenses.data.CategoryData
import com.example.app_expenses.enums.CategoryEnum
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import java.text.SimpleDateFormat
import java.util.*


object UtilitiesFunctions{
    fun replaceFragment(activity: FragmentActivity, fragment: Fragment, id: Int, addToBackStack: Boolean){
        var backStackName = fragment.javaClass.name
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(id, fragment)
        if(!addToBackStack){
            backStackName = null
        }
        transaction?.addToBackStack(backStackName)
        transaction?.commit()
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun getCategoryEnum(budgetCategory: String): CategoryEnum?{
        var category: CategoryEnum? = null
        when (budgetCategory){
            CategoryEnum.GROCERIES.categoryName -> category = CategoryEnum.GROCERIES
            CategoryEnum.ENTERTAINMENT.categoryName -> category = CategoryEnum.ENTERTAINMENT
            CategoryEnum.TRANSPORTATION.categoryName -> category = CategoryEnum.TRANSPORTATION
            CategoryEnum.SUBSCRIPTIONS.categoryName -> category = CategoryEnum.SUBSCRIPTIONS
            CategoryEnum.BILLS.categoryName -> category = CategoryEnum.BILLS
            CategoryEnum.PERSONAL_SPENDING.categoryName -> category = CategoryEnum.PERSONAL_SPENDING
        }
        return category
    }

    fun createCategories(): MutableList<CategoryEnum>{
        val categories = mutableListOf<CategoryEnum>()
        categories.add(CategoryEnum.GROCERIES)
        categories.add(CategoryEnum.ENTERTAINMENT)
        categories.add(CategoryEnum.TRANSPORTATION)
        categories.add(CategoryEnum.SUBSCRIPTIONS)
        categories.add(CategoryEnum.BILLS)
        categories.add(CategoryEnum.PERSONAL_SPENDING)
        return categories
    }

    fun createCategoriesBudgets(): MutableList<CategoryData>{
        val categories = mutableListOf<CategoryData>()
        categories.add(CategoryData(CategoryEnum.GROCERIES.categoryName, "0"))
        categories.add(CategoryData(CategoryEnum.ENTERTAINMENT.categoryName, "0"))
        categories.add(CategoryData(CategoryEnum.TRANSPORTATION.categoryName, "0"))
        categories.add(CategoryData(CategoryEnum.SUBSCRIPTIONS.categoryName, "0"))
        categories.add(CategoryData(CategoryEnum.BILLS.categoryName, "0"))
        categories.add(CategoryData(CategoryEnum.PERSONAL_SPENDING.categoryName, "0"))
        return categories
    }

    // To get the position of the categories in the "My Budgets" section of the budget page
    fun getCategoryBudgetsPosition(myBudgetCategory: String): Int{
        var position = 0
        when (myBudgetCategory){
            CategoryEnum.GROCERIES.categoryName -> position = 0
            CategoryEnum.ENTERTAINMENT.categoryName -> position = 1
            CategoryEnum.TRANSPORTATION.categoryName -> position = 2
            CategoryEnum.SUBSCRIPTIONS.categoryName -> position = 3
            CategoryEnum.BILLS.categoryName -> position = 4
            CategoryEnum.PERSONAL_SPENDING.categoryName -> position = 5
        }
        return position
    }

    // To validate money amount that is typed in. Has to be of form -> 200.00
    fun validateAmount(amount: String?): Boolean{
        amount?.let {
            val amountPattern = "^[0-9]+[.][0-9][0-9]\$"
            val amountMatcher = Regex(amountPattern)
            return amountMatcher.find(it) != null
        } ?: return false
    }

    fun timestampToDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("MMMM dd, yyyy")
        return formatter.format(Date(timestamp))
    }

    fun timestampToMonthYear(timestamp: Long): String{
        val formatter = SimpleDateFormat("MMMM, yyyy")
        return formatter.format(Date(timestamp))
    }

    fun timestampToDay(timestamp: Long): String{
        val formatter = SimpleDateFormat("dd")
        return formatter.format(Date(timestamp))
    }

    fun setDeleteIcon(c: Canvas, viewHolder: RecyclerView.ViewHolder,
                                  dX: Float, context: Context){
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val background = GradientDrawable()
        val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_trash_can)
        val itemView = viewHolder.itemView

        if(dX == 0F){
            c.drawRoundRect(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), 10F, 10F, clearPaint)
            return
        }

        background.color = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.red_bright))
        background.cornerRadius = UtilitiesFunctions.convertDpToPixel(10F, context)

        if(dX <= -540.0F){
            background.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
        } else{
            background.setBounds(itemView.right + dX.toInt() - 20, itemView.top, itemView.right, itemView.bottom)
        }

        background.draw(c)

        val deleteIconTop = itemView.top + (itemView.height - deleteDrawable!!.intrinsicHeight) / 2
        val deleteIconMargin = (itemView.height - deleteDrawable!!.intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - deleteDrawable!!.intrinsicHeight
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + deleteDrawable!!.intrinsicHeight

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)
    }

    fun calculateTotalPercentage(amount: Float, totalAmount: Float): Float{
        val total = amount/totalAmount
        return if(total > 1.0f && total < 1.01f){
            1.01f
        } else{
            "%.2f".format(amount/totalAmount).toFloat()
        }
    }

    fun createSimpleSnackbar(myView: View, myText: String, myIcon: Drawable, duration: Int, context: Context, onTop: Boolean): Snackbar{
        val snackBar = Snackbar.make(myView, myText, duration)
        val snackbarView = snackBar.view
        val snackbarLayout = snackBar.view as SnackbarLayout

        val customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null)

        val snackIcon = customView.findViewById<ImageView>(R.id.snackbarIcon)
        val snackText = customView.findViewById<TextView>(R.id.snackbarText)
        val snackAction = customView.findViewById<TextView>(R.id.snackbarActionText)

        snackAction.visibility = View.GONE
        snackIcon.background = myIcon
        snackText.text = myText

        if(onTop){
            val params = snackbarView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackbarView.layoutParams = params
        }

        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customView)

        return snackBar
    }

    fun createSuccessSnackbar(myView: View, myText: String, myIcon: Drawable, duration: Int, context: Context, onTop: Boolean, isSuccess: Boolean): Snackbar{
        val snackBar = Snackbar.make(myView, myText, duration)
        val snackbarView = snackBar.view
        val snackbarLayout = snackBar.view as SnackbarLayout

        val customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar, null)

        val snackIcon = customView.findViewById<ImageView>(R.id.snackbarIcon)
        val snackText = customView.findViewById<TextView>(R.id.snackbarText)
        val snackContainer = customView.findViewById<RelativeLayout>(R.id.snackbarContainer)
        val snackAction = customView.findViewById<TextView>(R.id.snackbarActionText)

        snackAction.visibility = View.GONE
        snackIcon.background = myIcon
        snackText.text = myText

        if(onTop){
            val params = snackbarView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackbarView.layoutParams = params
        }

        if(isSuccess){
            snackBar.setBackgroundTint(ContextCompat.getColor(context, R.color.greenSuccessBackground))
            snackIcon.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.greenSuccessForeground))
            snackText.setTextColor(ContextCompat.getColor(context, R.color.greenSuccessForeground))
            snackContainer.backgroundTintList =  ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.greenSuccessBackground))
        } else{
            snackBar.setBackgroundTint(ContextCompat.getColor(context, R.color.redUnsuccessBackground))
            snackIcon.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.redUnsuccessForeground))
            snackText.setTextColor(ContextCompat.getColor(context, R.color.redUnsuccessForeground))
            snackContainer.backgroundTintList =  ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.redUnsuccessBackground))
        }

        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customView)

        return snackBar
    }
}