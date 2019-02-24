package com.dogvscat.retingall.dto

/**
 * Класс содержит описание объекта списка
 */
class Item(id: String,
           title: String,
           rating: Float,
           image: String,
           tags:MutableList<Tag>) {
    val item_id: String = id
    val item_title: String = title
    val item_image: String = image
    val item_rating: Float = rating
    val item_tags: MutableList<Tag> = tags
}