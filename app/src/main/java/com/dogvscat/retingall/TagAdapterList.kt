package com.dogvscat.retingall

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper

class TagAdapterList(private val viewRecyclerView: RecyclerView,
                     private val tagList: MutableList<Tag>,
                     private val mContext: Context) : RecyclerView.Adapter<TagAdapterList.MyHolderTag>() {

    //специальное поле для отлавливания логов
    private val LOGDEBUGTAG: String = "POINT"
    private val REQUESTCODEADDTAG: Int = 2

    // добавил код со страницы swypelayout
    // https://github.com/chthai64/SwipeRevealLayout
    private val viewBinderHelper = ViewBinderHelper()

    init {
        // uncomment the line below if you want to open only one row at a time
        viewBinderHelper.setOpenOnlyOne(true)
    }

    //раздули элемент из макета и вернули в адаптер ссылку на элемент
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): MyHolderTag {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.activity_add_tag_tmpl,
                p0, false)
        return MyHolderTag(view, mContext)
    }

    /**
     * адаптер пробегает по списку элементов и вызывает метод указанный ниже для каждого элемента
     */
    override fun onBindViewHolder(holder: MyHolderTag, position: Int) {
        // get your data object first.
        val tag = tagList[position]

        // Save/restore the open/close state.
        // You need to provide a String id which uniquely defines the data object.
        viewBinderHelper.bind(holder.swipeLayout, tag.item_id)

        // Меняем значения элементов шаблона в соответствии с данными для элемента адаптера
        holder.index(tag.item_title)

        //реализуем удаление карточки
        holder.cardDelete.setOnClickListener {
            val database = DBHelper(mContext).writableDatabase

            database.delete(DBHelper.TABLE_TAGS, DBHelper.KEY_ID + "=" + tag.item_id, null)
            tagList.remove(tag)
            viewRecyclerView.adapter = TagAdapterList(viewRecyclerView, tagList, mContext)

            Log.d(LOGDEBUGTAG, "Карточка id:${tag.item_id},title:${tag.item_title} удалена")
            Snackbar.make(viewRecyclerView, "Запись ${tag.item_title} удалена", Snackbar.LENGTH_SHORT).show()
        }

        //редактирование карточки
        holder.cardEdit.setOnClickListener {
            val intent = Intent(mContext,EditActivity::class.java)
            intent.putExtra("item_id", tag.item_id)
            Log.d(LOGDEBUGTAG, "Обработали нажатие на ${tag.item_id}")
            (mContext as Activity).startActivityForResult(intent,REQUESTCODEADDTAG)
        }
    }

    override fun getItemCount(): Int = tagList.size

    /**
     * Вложенный класс, описывающий элемент RecyclerView, в нашем случае это тэги
     */
    class MyHolderTag(itemView: View, private val mContent: Context) : RecyclerView.ViewHolder(itemView) {
        val swipeLayout: SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout_tag)
        val cardDelete: CardView = itemView.findViewById(R.id.card_delete_tag) as CardView
        val cardEdit: CardView = itemView.findViewById(R.id.card_edit_tag)
        private val viewTextCard: TextView = itemView.findViewById<View>(R.id.view_text_card_tag) as TextView

        /**
         * Функция усланавливает значения элементов каточки - из данных переданных адаптером
         */
        fun index(str: String) {
            viewTextCard.text = "#" + str
        }
    }

}