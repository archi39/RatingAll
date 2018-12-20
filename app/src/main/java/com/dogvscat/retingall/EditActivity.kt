package com.dogvscat.retingall

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.dogvscat.retingall.adapters.TagAdapterCardShort
import com.dogvscat.retingall.adapters.TagAdapterListCardShort
import kotlinx.android.synthetic.main.app_bar.*

class EditActivity : AppCompatActivity() {
    private val LOGDEBUGTAG: String = "POINT"
    private lateinit var itemId: String
    private lateinit var database: SQLiteDatabase
    private lateinit var layoutActivityEdit: View
    private lateinit var editTextTitle: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var viewRecyclerTagsEdit: RecyclerView
    private val dbTags: MutableList<Tag> = mutableListOf()
    //список тэгов для возможного добавления в базу
    private val itemTags: MutableList<Tag> = mutableListOf()


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
        editTextTitle = findViewById(R.id.edit_text_title)
        editTextNumber = findViewById(R.id.edit_text_number)
        //список тэгов на экране - наполнение происходит в блоке инициализации
        viewRecyclerTagsEdit = findViewById<View>(R.id.view_recycler_tags_edit) as RecyclerView
        viewRecyclerTagsEdit.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        initElement()
        //набиваем пепременную тэгами из базы данных
        refreshDbTag()

        //выводим список уже созданных тэгов
        findViewById<View>(R.id.view_edit_tag_add_list).setOnClickListener {
            showListCardDialog()
        }

        //Добавляем тэг из текстового поля
        findViewById<View>(R.id.card_set_tag_edit).setOnClickListener {
            //проверяем что в базе есть тэги в будующем нужно поменять на запрос последнего ид из базы
            //оставил - возможно проверка не потребуется, потому что добавление в базу SQLite
            // может быть без явного указания ID
            /*  val tag = Tag("${if (dbTags.size > 0) {
                  (dbTags[0].item_id).toInt() + 1
              } else 1}", findViewById<EditText>(R.id.view_text_tag_new).text.toString())*/

            val tag = Tag("Null", findViewById<EditText>(R.id.view_text_edit_tag_new).text.toString())
            //добавляем новый тэг
            if (tag != null) {
                itemTags.add(tag)
            }

            //отображаем добавленный тэг в списке тэгов
            viewRecyclerTagsEdit.adapter = TagAdapterCardShort(viewRecyclerTagsEdit, itemTags, this)
        }

        //нужно записать изменения в базу данных и завершить активити
        findViewById<Button>(R.id.btn_end).setOnClickListener {
            // создаем объект для данных
            val contentValues = ContentValues()
            Log.d(LOGDEBUGTAG, "--- Update ${DBHelper.TABLE_ITEMS}: ---")
            // подготовим значения для обновления
            contentValues.put(DBHelper.KEY_TITLE, editTextTitle.text.toString())
            contentValues.put(DBHelper.KEY_RATING, editTextNumber.text.toString().toFloat())
            // обновляем по id
            database.update(DBHelper.TABLE_ITEMS,
                    contentValues,
                    DBHelper.KEY_ID + " = ? ",
                    arrayOf(itemId))
            database.close()
            //закрываем активити возвращаемся в главное окно
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    //выводит все тэги, которые есть в базе
    private fun refreshDbTag() {
        dbTags.clear()

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
                if (cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)).equals("Добавить"))
                else {
                    //наполняем наш список элементами
                    val tag = Tag(cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)),
                            cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)))
                    dbTags.add(tag)
                }
            } while (cursorTag.moveToNext())
        }
        cursorTag.close()
    }

    /**
     * метод взят с ресурса: https://code.luasoftware.com/tutorials/android/android-text-input-dialog-with-inflated-view-kotlin/
     * создает окно для добавления существующих тэгов из базы
     */
    fun showListCardDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Тэги")
        // https://stackoverflow.com/questions/10695103/creating-custom-alertdialog-what-is-the-root-view
        // Seems ok to inflate view with null rootView
        val view = (this as Activity).layoutInflater.inflate(R.layout.dialog_add_item_tag_list, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.view_recycler_add_item_tags_list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = TagAdapterListCardShort(recyclerView,itemTags, dbTags, this)
        builder.setView(view)

        builder.setPositiveButton("Ок") { dialog, _ ->
            //обновляем список тэгов на странице
            viewRecyclerTagsEdit.adapter = TagAdapterCardShort(viewRecyclerTagsEdit, itemTags, this)
        }
        builder.show()
    }

    private fun initElement() {
        itemTags.clear()

        val cursor = database.query(DBHelper.TABLE_ITEMS,
                null,
                DBHelper.KEY_ID + " = ? ",
                arrayOf(itemId),
                null,
                null,
                null)

        var itemTitle = ""
        //пробегаем по курсору (по базе - построчно)
        if (cursor.moveToFirst()) {
            do {
                itemTitle = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TITLE))
                val rating = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_RATING))

                //наполняем наш список элементами
                editTextTitle.setText(itemTitle, TextView.BufferType.EDITABLE)
                editTextNumber.setText(rating, TextView.BufferType.EDITABLE)
            } while (cursor.moveToNext())
        } else {
        }

        //Ниже наполнение связанными тэгами
        val cursorItemsTag = database.rawQuery(
                "SELECT TT.${DBHelper.KEY_ID}, TT.${DBHelper.KEY_TAG} " +
                        "FROM ${DBHelper.TABLE_ITEMS_TAGS} as TIT " +
                        "JOIN ${DBHelper.TABLE_TAGS} as TT ON TIT.${DBHelper.KEY_TAG_ID}=TT.${DBHelper.KEY_ID} " +
                        "JOIN ${DBHelper.TABLE_ITEMS} as TI ON TIT.${DBHelper.KEY_ITEM_ID}=TI.${DBHelper.KEY_ID} " +
                        "WHERE TI.${DBHelper.KEY_ID} = '${itemId}'", null)
        if (cursorItemsTag.moveToFirst()) {
            Log.d(LOGDEBUGTAG,"Элемент ${itemTitle} обладает следующими тэгами:")
            do {
                val tag = Tag(cursorItemsTag.getString(cursorItemsTag.getColumnIndex(DBHelper.KEY_ID)),
                        cursorItemsTag.getString(cursorItemsTag.getColumnIndex(DBHelper.KEY_TAG)))
                itemTags.add(tag)
                Log.d(LOGDEBUGTAG,"ID: ${tag.item_id}; TITLE: ${tag.item_title}")
            } while (cursorItemsTag.moveToNext())
        } else {
            Log.d(LOGDEBUGTAG,"У ${itemTitle} нет связанных тэгов")
        }
        cursorItemsTag.close()
        cursor.close()

        viewRecyclerTagsEdit.adapter = TagAdapterCardShort(viewRecyclerTagsEdit, itemTags, this)
    }
}
