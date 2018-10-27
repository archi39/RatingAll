package com.dogvscat.retingall

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class MyAdapter(private val title: Array<String>,
                private val rating: Array<Float>,
                private val mContext: Context) : RecyclerView.Adapter<MyHolder>() {

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
}