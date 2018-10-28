package com.dogvscat.retingall

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.grabner.circleprogress.CircleProgressView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper




class MyAdapter(private val items: MutableList<Item>,
                private val mContext: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    private val itemsList: MutableList<Item> = items

    // добавил код со страницы swypelayout
    // https://github.com/chthai64/SwipeRevealLayout
    private val viewBinderHelper = ViewBinderHelper()

    init {
        // uncomment the line below if you want to open only one row at a time
        viewBinderHelper.setOpenOnlyOne(true);
    }

    //раздули элемент из макета и вернули в адаптер ссылку на элемент
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.activity_main_card_tmpl,
                p0, false)
        return MyHolder(view, mContext)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

//для того чтобы получить список объектов описывающих карточки - нужно создать класс карточек
        // get your data object first.
        val item = itemsList[position]

        // Save/restore the open/close state.
        // You need to provide a String id which uniquely defines the data object.
        // временно в качестве ID использую имя - в будующем нужно предусмотреть программный ID
        viewBinderHelper.bind(holder.swipeLayout, item.item_id)

        // do your regular binding stuff here
        holder.index(item.item_title, item.item_rating)
    }

    override fun getItemCount(): Int = itemsList.size

    /**
     * Вложенный класс, описывающий элемент RecyclerView, в нашем случае это карточки
     */
    class MyHolder(itemView: View, private val mContent: Context) : RecyclerView.ViewHolder(itemView) {
        val swipeLayout: SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)
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