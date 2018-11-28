package com.dogvscat.retingall

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.widget.LinearLayout

import kotlinx.android.synthetic.main.activity_add_tag.*
import kotlinx.android.synthetic.main.app_bar.*
import android.view.LayoutInflater



class AddTagActivity : AppCompatActivity() {
    lateinit var linearLayoutTagsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tag)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //резолвим ссылки на шаблон
        linearLayoutTagsContainer = findViewById(R.id.linearLayoutTagsContainer)
        fillTag()
    }

    /**
     * Функция наполняет activity списком тэгов из базы
     */
    private fun fillTag() {
        val layoutInflater = this.layoutInflater

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
                //наполняем наш список элементами
                tags.add(Tag(cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)),
                        cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG))))

                Log.d(LOGDEBUGTAG, "Tag: id - " +
                        cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)) +
                        "title - " + cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)))
            } while (cursorItem.moveToNext())
        } else {}

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
