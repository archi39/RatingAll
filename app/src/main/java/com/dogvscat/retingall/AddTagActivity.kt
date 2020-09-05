package com.dogvscat.retingall

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogvscat.retingall.adapters.TagAdapterList
import com.dogvscat.retingall.dto.Tag
import com.dogvscat.retingall.services.DBHelper
import kotlinx.android.synthetic.main.activity_add_tag.*
import kotlinx.android.synthetic.main.app_bar.*


class AddTagActivity : AppCompatActivity() {
    lateinit var viewRecyclerTags: RecyclerView
    lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tag)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //резолвим ссылки на шаблон
        viewRecyclerTags = findViewById(R.id.view_recycler_tags)
        viewRecyclerTags.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        fillTag()

        but_submit_tag.setOnClickListener {
            val contentValues = ContentValues()
            contentValues.put(DBHelper.KEY_TAG, edit_text_title_tag.text.toString())
            database.insert(DBHelper.TABLE_TAGS, null, contentValues)
            fillTag()
        }
    }

    /**
     * Функция наполняет activity списком тэгов из базы
     */
    private fun fillTag() {
        //список содержащий строки таблицы тэгов
        val tags = mutableListOf<Tag>()
        database = DBHelper(this).writableDatabase

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
                if (cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)).equals("Добавить"))
                else {
                    //наполняем наш список элементами
                    val tag = Tag(cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)),
                            cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)))
                    tags.add(tag)
                }
            } while (cursorTag.moveToNext())
        }


        //устанавливаем адаптер для нашего списка
        viewRecyclerTags.adapter = TagAdapterList(viewRecyclerTags, tags, this)
        cursorTag.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
