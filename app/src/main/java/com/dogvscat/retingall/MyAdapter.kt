package com.dogvscat.retingall

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.grabner.circleprogress.CircleProgressView

class MyAdapter(private val title: Array<String>,
                private val rating: Array<Float>,
                private val mContext: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.activity_main_card_tmpl, p0, false)
        return MyHolder(view, mContext)
    }

    override fun onBindViewHolder(p0: MyHolder, p1: Int) {
       p0.index(title[p1],rating[p1])
    }

    override fun getItemCount(): Int {
        return title.size
    }

    /**
     * Вложенный класс, описывающий элемент RecyclerView, в нашем случае это карточки
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
}