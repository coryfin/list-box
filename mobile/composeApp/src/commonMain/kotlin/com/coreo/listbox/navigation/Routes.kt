package com.coreo.listbox.navigation

object Routes {
    const val HOME = "home"
    const val LIST_DETAIL = "listDetail/{listId}"
    const val ITEM_DETAIL = "itemDetail/{itemId}"
    const val CONFIGURE_FIELDS = "configureFields/{listId}"

    fun listDetail(listId: String) = "listDetail/$listId"
    fun itemDetail(itemId: String) = "itemDetail/$itemId"
    fun configureFields(listId: String) = "configureFields/$listId"
}
