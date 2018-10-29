package com.dogvscat.retingall

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.app_bar.*

class EditActivity : AppCompatActivity() {
    private val itemId = intent.getStringExtra("item_id")
    private val database = DBHelper(this).writableDatabase
    private lateinit var layoutActivityEdit: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //получаем ссылки на элементы графического интерфейса
        layoutActivityEdit = findViewById(R.id.layout_activity_edit)
        initElement()
    }

    fun initElement() {
        val cursor = database.query(DBHelper.TABLE_ITEMS,
                null,
                "_id = ?",
                arrayOf(itemId),
                null,
                null,
                null)

        //пробегаем по курсору (по базе - построчно)
        if (cursor.moveToFirst()) {
            do {
                //наполняем наш список элементами
                findViewById<EditText>(R.id.edit_text_title)
                        .setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TITLE)),
                                TextView.BufferType.EDITABLE)
                findViewById<EditText>(R.id.edit_text_number)
                        .setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_RATING)),
                                TextView.BufferType.EDITABLE)

            } while (cursor.moveToNext())
        } else {
            Snackbar.make(layoutActivityEdit,
                    R.string.action_empty_db,
                    Snackbar.LENGTH_SHORT).show()
        }

        cursor.close()
    }
}
