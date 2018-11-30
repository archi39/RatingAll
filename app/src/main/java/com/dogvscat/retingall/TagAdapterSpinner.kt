package com.dogvscat.retingall

import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.*


class TagAdapterSpinner(private val tags: MutableList<Tag>) : BaseAdapter() {


    override fun getCount(): Int {
        return tags.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Tag {
        return tags[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // используем созданные, но не используемые view
        var view = convertView
            if (view == null) {
                view = LayoutInflater.from(parent!!.context).inflate(R.layout.activity_main_tag_tmpl,
                        parent,
                        false)
            }

        val tag = getItem(position)

        // заполняем View в пункте списка данными из списка тэгов: наименование
        (view!!.findViewById(R.id.view_text_tag) as TextView).setText(tag.item_title)
        if(tag.item_title.equals("Добавить")) (view.findViewById(R.id.view_text_tag) as TextView).setTextColor(Color.YELLOW)

        return view
    }

}