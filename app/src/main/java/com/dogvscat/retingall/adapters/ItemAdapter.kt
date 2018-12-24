package com.dogvscat.retingall.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.grabner.circleprogress.CircleProgressView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.dogvscat.retingall.*
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.File


class ItemAdapter(private val viewRecyclerView: RecyclerView,
                  private val itemsList: MutableList<Item>,
                  private val mContext: Context) : RecyclerView.Adapter<ItemAdapter.MyHolder>() {
    private val REQUESTCODEEDIT: Int = 1

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
        holder.index(item.item_title, item.item_rating, item.item_tags)

        //реализуем удаление карточки
        holder.cardDelete.setOnClickListener {
            val database = DBHelper(mContext).writableDatabase

            database.delete(DBHelper.TABLE_ITEMS, DBHelper.KEY_ID + "=" + item.item_id, null)
            //удаляем связки элемента с тэгами
            database.delete(DBHelper.TABLE_ITEMS_TAGS, DBHelper.KEY_ITEM_ID + "=" + item.item_id ,null)

            itemsList.remove(item)
            viewRecyclerView.adapter = ItemAdapter(viewRecyclerView, itemsList, mContext)

            database.close()
            Snackbar.make(viewRecyclerView, "Запись ${item.item_title} удалена", Snackbar.LENGTH_SHORT).show()
        }

        //редактирование карточки
        holder.cardEdit.setOnClickListener {
            val intent = Intent(mContext, EditActivity::class.java)
            intent.putExtra("item_id", item.item_id)
            (mContext as Activity).startActivityForResult(intent, REQUESTCODEEDIT)
        }

        //по нажатию на карточку появляется диалоговое окно
        holder.viewTextCard.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(mContext)
            builder.setTitle("${item.item_title}")
            val view = (mContext as Activity).layoutInflater.inflate(R.layout.dialog_item_detail, null)
            val viewTextItemDetail = view.findViewById<TextView>(R.id.view_text_item_detail)
            val imageItemDetail = view.findViewById<SimpleDraweeView>(R.id.image_item_detail)
            viewTextItemDetail.text = item.item_image

            //Получаем фото
            val file = File(item.item_image)
            val uri = Uri.fromFile(file)

            val height = mContext.resources.getDimensionPixelSize(R.dimen.photo_height)
            val width = mContext.resources.getDimensionPixelSize(R.dimen.photo_width)

            val request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(ResizeOptions(width, height))
                    .build()
            val controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(imageItemDetail?.controller)
                    .setImageRequest(request)
                    .build()
            imageItemDetail?.controller = controller

            builder.setView(view)
            builder.setPositiveButton("Ок") { _, _ ->
                //Ничего не делаем, просто закрываем окно
            }
            builder.show()
        }
    }

    override fun getItemCount(): Int = itemsList.size

    /**
     * Вложенный класс, описывающий элемент RecyclerView, в нашем случае это карточки
     */
    class MyHolder(itemView: View, private val mContent: Context) : RecyclerView.ViewHolder(itemView) {
        var swipeLayout: SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)
        val cardDelete: CardView = itemView.findViewById(R.id.card_delete)
        val cardEdit: CardView = itemView.findViewById(R.id.card_edit)
        val viewTextCard: TextView = itemView.findViewById<View>(R.id.view_text_card) as TextView
        private val circleViewCard: CircleProgressView = itemView.findViewById<View>(R.id.circle_view_card) as CircleProgressView
        private val viewTextTags: TextView = itemView.findViewById<View>(R.id.view_text_tags) as TextView

         //Функция усланавливает значения элементов каточки - из данных переданных адаптером
        fun index(str: String, rating: Float, itemTags: MutableList<Tag>) {
            viewTextCard.text = str
            circleViewCard.setValue(rating)

            //выводим список всех связанных тэгов
            if (itemTags.size > 0) {
                var tags = ""
                for(tag in itemTags){
                    tags += "#${tag.item_title} "
                }
                viewTextTags.text = tags
            }
            //        отключаем возможность редактирования прогрессбара
            circleViewCard.isSeekModeEnabled = false
        }
    }
}