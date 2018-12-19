package com.dogvscat.retingall

/**
 * Класс содержит описание объекта списка
 */
class Item(private val id: String,
           private val title: String,
           private val rating: Float,
           private val tags:MutableList<Tag>) {
    val item_id: String = id
    val item_title: String = title
    val item_rating: Float = rating
    val item_tags: MutableList<Tag> = tags
}