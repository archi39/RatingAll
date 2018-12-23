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
    private lateinit var itemParentId: String
    private lateinit var database: SQLiteDatabase
    private lateinit var layoutActivityEdit: View
    private lateinit var editTextTitle: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var viewRecyclerTagsEdit: RecyclerView
    //список всех тэгов в базе
    private val dbTags: MutableList<Tag> = mutableListOf()
    //список тэгов редактируемых в процессе
    private val morfedTags: MutableList<Tag> = mutableListOf()
    //список тэгов изначально закрепленных за элементом
    private val itemTags: MutableList<Tag> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        itemParentId = intent.getStringExtra("item_id")
        database = DBHelper(this).writableDatabase

        //получаем ссылки на элементы графического интерфейса
        layoutActivityEdit = findViewById(R.id.layout_activity_edit)
        editTextTitle = findViewById(R.id.edit_text_title)
        editTextNumber = findViewById(R.id.edit_text_number)
        //список тэгов на экране - наполнение происходит в блоке инициализации
        viewRecyclerTagsEdit = findViewById<View>(R.id.view_recycler_tags_edit) as RecyclerView
        viewRecyclerTagsEdit.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        Log.d(LOGDEBUGTAG, "Размер пустого списка [${itemTags.size}]")
        initElement()
        //набиваем пепременную тэгами из базы данных
        refreshDbTag()

        //выводим список уже созданных тэгов
        findViewById<View>(R.id.view_edit_tag_add_list).setOnClickListener {
            showListCardDialog()
        }

        //Добавляем тэг из текстового поля
        findViewById<View>(R.id.card_set_tag_edit).setOnClickListener {
            val tag = Tag("Null", findViewById<EditText>(R.id.view_text_edit_tag_new).text.toString())
            //добавляем новый тэг
            morfedTags.add(tag)
            //отображаем добавленный тэг в списке тэгов
            viewRecyclerTagsEdit.adapter = TagAdapterCardShort(viewRecyclerTagsEdit, morfedTags, this)
        }

        //нужно записать изменения в базу данных и завершить активити
        findViewById<Button>(R.id.btn_end).setOnClickListener {
            val contentValues = ContentValues()
            Log.d(LOGDEBUGTAG, "--- Update ${DBHelper.TABLE_ITEMS}: ---")
            // подготовим значения для обновления
            contentValues.put(DBHelper.KEY_TITLE, editTextTitle.text.toString())
            contentValues.put(DBHelper.KEY_RATING, editTextNumber.text.toString().toFloat())
            // обновляем по id
            database.update(DBHelper.TABLE_ITEMS,
                    contentValues,
                    DBHelper.KEY_ID + " = ? ",
                    arrayOf(itemParentId))

            //обновляем записи тэгов
            if (itemTags.size > 0) {
                //либо ничего не поменялось, либо добавились тэги либо часть была удалена
                if (morfedTags.size > 0) {
                    //получаем список удаленных тэгов и выпиливаем их из базы
                    val droppedTags: List<Tag> = itemTags.minus(morfedTags)
                    if (droppedTags.isNotEmpty()) {
                        for (dTag in droppedTags) {
                            database.delete(DBHelper.TABLE_ITEMS_TAGS, "${DBHelper.KEY_TAG_ID} = ${dTag.item_id}", null)
                            Log.d(LOGDEBUGTAG, "связка с тэгом ${dTag.item_title} удалена")
                        }
                    }

                    //теперь обработаем список редактированных тэгов, и сделаем при необходимости insert
                    val contentValuesNewTags = ContentValues()
                    val contentValuesItemsTags = ContentValues()
                    //подготовим данные по id текущего элемента для добавления в таблицу связей
                    contentValuesItemsTags.put(DBHelper.KEY_ITEM_ID, itemParentId)

                    for (mTag in morfedTags) {
                        //если тэг был в списке уже добавленных, то проходим мимо
                        if (itemTags.contains(mTag)) {
                            Log.d(LOGDEBUGTAG, "Тэг [${mTag.item_title}] не изменился")
                        } else {
                            //два варианта или это новый тэг или тэг добавлен из базы
                            //если в базе уже есть тэги - проверяем на совпадение
                            if (dbTags.size > 0) {
                                var contain = false
                                for (dbTag in dbTags) {
                                    //поочередно сравниваем тэг с каждым тэгом из базы
                                    //находим первое совпадение и завергаем цикл
                                    if (mTag.item_title == dbTag.item_title) {
                                        mTag.item_id = dbTag.item_id
                                        contain = true
                                        break
                                    }
                                }
                                if (contain) {
                                    Log.d(LOGDEBUGTAG, "Тэг [${mTag.item_title}] уже есть в базе тэгов")
                                    //связку всё равно нужно добавить в базу
                                    contentValuesItemsTags.put(DBHelper.KEY_TAG_ID, mTag.item_id)
                                    database.insert(DBHelper.TABLE_ITEMS_TAGS, null, contentValuesItemsTags)

                                } else {
                                    contentValuesNewTags.put(DBHelper.KEY_TAG, mTag.item_title)
                                    database.insert(DBHelper.TABLE_TAGS, null, contentValuesNewTags)
                                    //обновляем список тэгов в базе, наш новый тэг получил свой ID и встал в первую позицию списка
                                    refreshDbTag()
                                    //добавляем связку элемента на новый тэг
                                    contentValuesItemsTags.put(DBHelper.KEY_TAG_ID, dbTags[0].item_id)
                                    database.insert(DBHelper.TABLE_ITEMS_TAGS, null, contentValuesItemsTags)
                                }
                            }
                            //если тэгов в БД нет сразу добавляем новый тэг в таблицу тэгов и связку на него для элемента
                            else {
                                contentValuesNewTags.put(DBHelper.KEY_TAG, mTag.item_title)
                                database.insert(DBHelper.TABLE_TAGS, null, contentValuesNewTags)
                                //обновляем список тэгов в базе, наш новый тэг получил свой ID и встал в первую позицию списка
                                refreshDbTag()
                                //добавляем связку элемента на новый тэг
                                contentValuesItemsTags.put(DBHelper.KEY_TAG_ID, dbTags[0].item_id)
                                database.insert(DBHelper.TABLE_ITEMS_TAGS, null, contentValuesItemsTags)
                            }
                        }
                    }
                } else {
                    //все тэги были удалены - delete всех связей для itemTags
                    for (iTag in itemTags) {
                        database.delete(DBHelper.TABLE_ITEMS_TAGS, "${DBHelper.KEY_TAG_ID} = ${iTag.item_id}", null)
                        Log.d(LOGDEBUGTAG, "связка с тэгом ${iTag.item_title} удалена")
                    }
                }
            } else {
                //тэги были добавлены - нужно сделать insert в таблицу тэгов и таблицу связей
                if (morfedTags.size > 0) {
                    val contentValuesNewTags = ContentValues()
                    val contentValuesItemsTags = ContentValues()
                    //подготовим данные по id текущего элемента для добавления в таблицу связей
                    contentValuesItemsTags.put(DBHelper.KEY_ITEM_ID, itemParentId)
                    //пробегаем по списку отредактированных тэгов
                    for (mTag in morfedTags) {
                        //если в базе уже есть тэги - проверяем на совпадение
                        if (dbTags.size > 0) {
                            var contain = false
                            for (dbTag in dbTags) {
                                //поочередно сравниваем тэг с каждым тэгом из базы
                                //находим первое совпадение и завергаем цикл
                                if (mTag.item_title == dbTag.item_title) {
                                    mTag.item_id = dbTag.item_id
                                    contain = true
                                    break
                                }
                            }
                            if (contain) {
                                Log.d(LOGDEBUGTAG, "Тэг [${mTag.item_title}] уже есть в базе тэгов")
                                //связку всё равно нужно добавить в базу
                                contentValuesItemsTags.put(DBHelper.KEY_TAG_ID, mTag.item_id)
                                database.insert(DBHelper.TABLE_ITEMS_TAGS, null, contentValuesItemsTags)

                            } else {
                                contentValuesNewTags.put(DBHelper.KEY_TAG, mTag.item_title)
                                database.insert(DBHelper.TABLE_TAGS, null, contentValuesNewTags)
                                //обновляем список тэгов в базе, наш новый тэг получил свой ID и встал в первую позицию списка
                                refreshDbTag()
                                //добавляем связку элемента на новый тэг
                                contentValuesItemsTags.put(DBHelper.KEY_TAG_ID, dbTags[0].item_id)
                                database.insert(DBHelper.TABLE_ITEMS_TAGS, null, contentValuesItemsTags)
                            }
                        }
                        //если тэгов в БД нет сразу добавляем новый тэг в таблицу тэгов и связку на него для элемента
                        else {
                            contentValuesNewTags.put(DBHelper.KEY_TAG, mTag.item_title)
                            database.insert(DBHelper.TABLE_TAGS, null, contentValuesNewTags)
                            //обновляем список тэгов в базе, наш новый тэг получил свой ID и встал в первую позицию списка
                            refreshDbTag()
                            //добавляем связку элемента на новый тэг
                            contentValuesItemsTags.put(DBHelper.KEY_TAG_ID, dbTags[0].item_id)
                            database.insert(DBHelper.TABLE_ITEMS_TAGS, null, contentValuesItemsTags)
                        }
                    }
                }
                //если изначально тэгов не было и мы ничего не добавляли
                //очевидно ничего делать не нужно
            }
            //закрываем соединение с базой
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
        recyclerView.adapter = TagAdapterListCardShort(recyclerView, morfedTags, dbTags, this)
        builder.setView(view)

        builder.setPositiveButton("Ок") { _, _ ->
            //обновляем список тэгов на странице
            viewRecyclerTagsEdit.adapter = TagAdapterCardShort(viewRecyclerTagsEdit, morfedTags, this)
        }
        builder.show()
    }

    private fun initElement() {
        morfedTags.clear()
        itemTags.clear()

        val cursor = database.query(DBHelper.TABLE_ITEMS,
                null,
                DBHelper.KEY_ID + " = ? ",
                arrayOf(itemParentId),
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
        cursor.close()

        //Ниже наполнение связанными тэгами
        val cursorItemsTag = database.rawQuery(
                "SELECT TT.${DBHelper.KEY_ID}, TT.${DBHelper.KEY_TAG} " +
                        "FROM ${DBHelper.TABLE_ITEMS_TAGS} as TIT " +
                        "JOIN ${DBHelper.TABLE_TAGS} as TT ON TIT.${DBHelper.KEY_TAG_ID}=TT.${DBHelper.KEY_ID} " +
                        "JOIN ${DBHelper.TABLE_ITEMS} as TI ON TIT.${DBHelper.KEY_ITEM_ID}=TI.${DBHelper.KEY_ID} " +
                        "WHERE TI.${DBHelper.KEY_ID} = '${itemParentId}'", null)
        if (cursorItemsTag.moveToFirst()) {
            Log.d(LOGDEBUGTAG, "Элемент ${itemTitle} обладает следующими тэгами:")
            do {
                val tag = Tag(cursorItemsTag.getString(cursorItemsTag.getColumnIndex(DBHelper.KEY_ID)),
                        cursorItemsTag.getString(cursorItemsTag.getColumnIndex(DBHelper.KEY_TAG)))
                morfedTags.add(tag)
                itemTags.add(tag)
                Log.d(LOGDEBUGTAG, "ID: ${tag.item_id}; TITLE: ${tag.item_title}")
            } while (cursorItemsTag.moveToNext())
        } else {
            Log.d(LOGDEBUGTAG, "У ${itemTitle} нет связанных тэгов")
        }
        cursorItemsTag.close()

        viewRecyclerTagsEdit.adapter = TagAdapterCardShort(viewRecyclerTagsEdit, morfedTags, this)
    }
}
