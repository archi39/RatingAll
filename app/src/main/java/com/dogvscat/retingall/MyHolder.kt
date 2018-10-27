package com.dogvscat.retingall

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import at.grabner.circleprogress.CircleProgressView

/**
 * Класс описывающий элемент RecyclerView, в нашем случае это карточки
 */
class MyHolder(itemView: View, private val mContent: Context) : RecyclerView.ViewHolder(itemView){
    private val view_text_card: TextView
    private val circle_view_card: CircleProgressView

    init {
        view_text_card = itemView.findViewById<View>(R.id.view_text_card) as TextView
        circle_view_card = itemView.findViewById<View>(R.id.circle_view_card) as CircleProgressView
    }

    /**
     * Функция усланавливает значения элементов каточки - из данных переданных адаптером
     */
    fun index(str: String, rating: Float){
        view_text_card.text = str
        circle_view_card.setValue(rating)
//        отключаем возможность редактирования прогрессбара
        circle_view_card.isSeekModeEnabled = false
    }
}