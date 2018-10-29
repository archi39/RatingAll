package com.dogvscat.retingall

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.app_bar.*

class EditActivity : AppCompatActivity() {
    private lateinit var itemId: String
    private lateinit var database: SQLiteDatabase
    private lateinit var layoutActivityEdit: View
    private lateinit var editTextTitle: EditText
    private lateinit var editTextNumber: EditText
    private val LOGDEBUGTAG: String = "POINT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        itemId = intent.getStringExtra("item_id")
        database = DBHelper(this).writableDatabase

        //получаем ссылки на элементы графического интерфейса
        layoutActivityEdit = findViewById(R.id.layout_activity_edit)
        editTextTitle =  findViewById(R.id.edit_text_title)
        editTextNumber =  findViewById(R.id.edit_text_number)
        initElement()
    }

    private fun initElement() {
        val cursor = database.query(DBHelper.TABLE_ITEMS,
                null,
                DBHelper.KEY_ID + " = ? ",
                arrayOf(itemId),
                null,
                null,
                null)

        //пробегаем по курсору (по базе - построчно)
        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TITLE))
                val rating = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_RATING))

                //наполняем наш список элементами
                editTextTitle.setText(title, TextView.BufferType.EDITABLE)
                editTextNumber.setText(rating, TextView.BufferType.EDITABLE)

            } while (cursor.moveToNext())
        } else {
            Snackbar.make(layoutActivityEdit, R.string.action_empty_db, Snackbar.LENGTH_SHORT)
                    .show()
        }

        cursor.close()
    }
}