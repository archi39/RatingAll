package com.dogvscat.retingall

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.R.attr.name
import android.graphics.Color
import android.support.v4.content.ContextCompat.getColor
import android.view.LayoutInflater
import android.widget.*
import java.util.zip.Inflater


class TagAdapterSpinner(private val spinner: Spinner,
                        private val tags: MutableList<Tag>,
                        private val mContext: Context) : BaseAdapter() {


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