package com.example.app_expenses.enums

import com.example.app_expenses.R

enum class CategoryEnum {
    PERSONAL_SPENDING("Personal Spending", R.color.blackCategory, R.drawable.ic_hand_coin),
    GROCERIES("Groceries", R.color.orangeCategory, R.drawable.ic_cart),
    ENTERTAINMENT("Entertainment", R.color.purpleCategory, R.drawable.ic_filmstrip),
    TRANSPORTATION("Transportation", R.color.yellowCategory, R.drawable.ic_train_car),
    BILLS("Bills", R.color.greenCategory, R.drawable.ic_cash),
    SUBSCRIPTIONS("Subscriptions", R.color.redCategory, R.drawable.ic_playlist_check)

    ;

    var categoryName: String? = null
    var categoryColor : Int? = null
    var categoryIcon: Int? = null

    constructor()

    constructor(
        categoryName: String,
        categoryColor: Int,
        categoryIcon: Int
    ) {
        this.categoryName = categoryName
        this.categoryColor = categoryColor
        this.categoryIcon = categoryIcon
    }
}