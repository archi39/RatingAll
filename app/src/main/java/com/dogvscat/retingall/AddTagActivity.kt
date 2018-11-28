package com.dogvscat.retingall

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.widget.LinearLayout

import kotlinx.android.synthetic.main.activity_add_tag.*
import kotlinx.android.synthetic.main.app_bar.*
import android.view.LayoutInflater


class AddTagActivity : AppCompatActivity() {
    lateinit var viewRecyclerTags: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tag)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //резолвим ссылки на шаблон
        viewRecyclerTags = findViewById(R.id.view_recycler_tags)
        fillTag()
    }

    /**
     * Функция наполняет activity списком тэгов из базы
     */
    private fun fillTag() {
        val layoutInflater = this.layoutInflater
        //список содержащий строки таблицы тэгов
        val tags = mutableListOf<Tag>()
        val database = DBHelper(this).writableDatabase

        //создаем курсор для просмотра таблицы тэгов сортируем его по убыванию
        val cursorTag = database.query(DBHelper.TABLE_TAGS,
                null,
                null,
                null,
                null,
                null,
                DBHelper.KEY_ID + " DESC")

        if (cursorTag.moveToFirst()) {
            do {
                if (cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)) != "Добавить") {
                    //наполняем наш список элементами
                    tags.add(Tag(cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)),
                            cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG))))
                }
            } while (cursorTag.moveToNext())
        }

        //устанавливаем адаптер для нашего списка
        viewRecyclerTags.adapter = TagAdapterList(viewRecyclerTags, tags, this)
        cursorTag.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
