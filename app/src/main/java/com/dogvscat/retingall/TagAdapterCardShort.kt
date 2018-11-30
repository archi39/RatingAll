package com.dogvscat.retingall

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper

class TagAdapterCardShort(private val viewRecyclerView: RecyclerView,
                          private val tagList: MutableList<Tag>,
                          private val mContext: Context) : RecyclerView.Adapter<TagAdapterCardShort.MyHolderTagCard>() {

    //специальное поле для отлавливания логов
    private val LOGDEBUGTAG: String = "POINT"

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

        //реализуем удаление связи тэга с элеменгом
        holder.cardTagItem.setOnClickListener {
            val database = DBHelper(mContext).writableDatabase

            /*database.delete(DBHelper.TABLE_TAGS, DBHelper.KEY_ID + "=" + tag.item_id, null)
            tagList.remove(tag)
            viewRecyclerView.adapter = TagAdapterCardShort(viewRecyclerView, tagList, mContext)

            Log.d(LOGDEBUGTAG, "Карточка id:${tag.item_id},title:${tag.item_title} удалена")*/
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