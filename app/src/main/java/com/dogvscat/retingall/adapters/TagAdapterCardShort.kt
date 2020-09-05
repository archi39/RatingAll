package com.dogvscat.retingall.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dogvscat.retingall.R
import com.dogvscat.retingall.dto.Tag
import com.google.android.material.snackbar.Snackbar

class TagAdapterCardShort(private val viewRecyclerView: RecyclerView,
                          private val tagList: MutableList<Tag>,
                          private val mContext: Context) : RecyclerView.Adapter<TagAdapterCardShort.MyHolderTagCard>() {
    //раздули элемент из макета и вернули в адаптер ссылку на элемент
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): MyHolderTagCard {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.activity_add_tag_short_tmpl,
                p0, false)
        return MyHolderTagCard(view, mContext)
    }

    /**
     * адаптер пробегает по списку элементов и вызывает метод указанный ниже для каждого элемента
     */
    override fun onBindViewHolder(holder: MyHolderTagCard, position: Int) {
        // get your data object first.
        val tag = tagList[position]
        // Меняем значения элементов шаблона в соответствии с данными для элемента адаптера
        holder.index(tag.item_title)
        //реализуем удаление тэга с элеменгом
        holder.cardTagItem.setOnClickListener {
            tagList.remove(tag)
            viewRecyclerView.adapter = TagAdapterCardShort(viewRecyclerView, tagList, mContext)
            Snackbar.make(viewRecyclerView, "Тэг ${tag.item_title} откреплен", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = tagList.size

    /**
     * Вложенный класс, описывающий элемент RecyclerView, в нашем случае это тэги
     */
    class MyHolderTagCard(itemView: View, private val mContent: Context) : RecyclerView.ViewHolder(itemView) {
        val cardTagItem: CardView = itemView.findViewById(R.id.card_tag_item) as CardView
        private val viewTextCard: TextView = itemView.findViewById<View>(R.id.view_text_card_tag_item) as TextView

        /**
         * Функция усланавливает значения элементов каточки - из данных переданных адаптером
         */
        fun index(str: String) {
            viewTextCard.text = "#" + str
        }
    }

}