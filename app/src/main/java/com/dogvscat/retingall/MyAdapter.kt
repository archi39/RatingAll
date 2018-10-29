package com.dogvscat.retingall

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
import at.grabner.circleprogress.CircleProgressView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper



class MyAdapter(private val viewRecyclerView: RecyclerView,
                items: MutableList<Item>,
                private val mContext: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
    //специальное поле для отлавливания логов
    private val LOGDEBUGTAG: String = "POINT"
    private val REQUESTCODEEDIT: Int = 1
    private val itemsList: MutableList<Item> = items

    // добавил код со страницы swypelayout
    // https://github.com/chthai64/SwipeRevealLayout
    private val viewBinderHelper = ViewBinderHelper()

    init {
        // uncomment the line below if you want to open only one row at a time
        viewBinderHelper.setOpenOnlyOne(true)
    }

    //раздули элемент из макета и вернули в адаптер ссылку на элемент
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): MyHolder {

        val view = LayoutInflater.from(p0.context).inflate(R.layout.activity_main_card_tmpl,
                p0, false)
        return MyHolder(view, mContext)
    }

    /**
     * адаптер пробегает по списку элементов и вызывает метод указанный ниже для каждого элемента
     */
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        // get your data object first.
        val item = itemsList[position]

        // Save/restore the open/close state.
        // You need to provide a String id which uniquely defines the data object.
        viewBinderHelper.bind(holder.swipeLayout, item.item_id)

        // Меняем значения элементов шаблона в соответствии с данными для элемента адаптера
        holder.index(item.item_title, item.item_rating)

        //реализуем удаление карточки
        holder.cardDelete.setOnClickListener {
            val database = DBHelper(mContext).writableDatabase

            database.delete(DBHelper.TABLE_ITEMS, DBHelper.KEY_ID + "=" + item.item_id, null)
            itemsList.remove(item)
            viewRecyclerView.adapter = MyAdapter(viewRecyclerView, itemsList, mContext)

            Log.d(LOGDEBUGTAG, "Карточка id:${item.item_id},title:${item.item_title} удалена")
            Snackbar.make(viewRecyclerView, "Запись ${item.item_title} удалена", Snackbar.LENGTH_SHORT).show()
        }

        //редактирование карточки
        holder.cardEdit.setOnClickListener {
            val intent = Intent(mContext,EditActivity::class.java)
            intent.putExtra("item_id", item.item_id)
            Log.d(LOGDEBUGTAG, "Обработали нажатие")
            mContext.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = itemsList.size

    /**
     * Вложенный класс, описывающий элемент RecyclerView, в нашем случае это карточки
     */
    class MyHolder(itemView: View, private val mContent: Context) : RecyclerView.ViewHolder(itemView) {
        val swipeLayout: SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)
        val cardDelete: CardView = itemView.findViewById(R.id.card_delete)
        val cardEdit: CardView = itemView.findViewById(R.id.card_edit)
        private val viewTextCard: TextView = itemView.findViewById<View>(R.id.view_text_card) as TextView
        private val circleViewCard: CircleProgressView = itemView.findViewById<View>(R.id.circle_view_card) as CircleProgressView

        /**
         * Функция усланавливает значения элементов каточки - из данных переданных адаптером
         */
        fun index(str: String, rating: Float) {
            viewTextCard.text = str
            circleViewCard.setValue(rating)
//        отключаем возможность редактирования прогрессбара
            circleViewCard.isSeekModeEnabled = false
        }
    }
}