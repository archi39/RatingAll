package com.dogvscat.retingall

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Switch
import com.dogvscat.retingall.adapters.ItemAdapter
import com.dogvscat.retingall.adapters.TagAdapterSpinner
import com.dogvscat.retingall.dto.Item
import com.dogvscat.retingall.dto.Tag
import com.dogvscat.retingall.services.DBHelper
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_content_main.*
import kotlinx.android.synthetic.main.app_bar.*

/**
 * Главное окно программы, точка входа в приложение
 *
 * @author EvgenySamarin
 */
class MainActivity : AppCompatActivity() {
    private val LOGDEBUGTAG: String = "POINT"
    private val REQUESTCODEADD: Int = 0
    private val REQUESTCODEADDTAG: Int = 2

    //объявляем ссылки на элементы формы
    private lateinit var layoutMain: View
    private lateinit var viewRecyclerView: RecyclerView
    private lateinit var viewSpinner: Spinner
    private lateinit var database: SQLiteDatabase
    private lateinit var switcher: Switch

    //список элементов и тэгов
    val dbTags = mutableListOf<Tag>()
    val items = mutableListOf<Item>()
    val filterItems = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeField()
        setSupportActionBar(toolbar)

        setListeners()

        refreshBD() //наполняем экран данными из базы
    }

    private fun setListeners() {
        //вызываем новое активити для добавления нового тэга
        view_text_tag_btn.setOnClickListener {
            startActivityForResult(Intent(this, AddTagActivity::class.java), REQUESTCODEADDTAG)
        }

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            if (items.size > 0) {
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("LASTITEMID", items[0].item_id)
                startActivityForResult(intent, REQUESTCODEADD)
            } else {
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("LASTITEMID", "Null")
                startActivityForResult(intent, REQUESTCODEADD)
            }
        }

        switcher.setOnCheckedChangeListener { _, _ ->
            refreshBD()
        }

        //переопределяем обработчик выбора элемента выбора тэгов
        viewSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (switcher.isChecked) {
                    //если список элементов базы не пустой, проходим по нему, в каждом элементе для списка
                    //его тэгов проверяем наличие в списке выбранного тэга,
                    //если присутствует - добавляем элемент в список отфильтрованных элементов
                    filterItems.clear()
                    if (items.size > 0) {
                        for (item in items) {
                            if (item.item_tags.isNotEmpty())
                                for (itemTag in item.item_tags) {
                                    if (itemTag.item_title == dbTags[position].item_title) {
                                        filterItems.add(item)
                                        break
                                    }
                                }
                        }
                        viewRecyclerView.adapter = ItemAdapter(viewRecyclerView, filterItems, layoutMain.context)
                        Snackbar.make(layoutMain, "Отфильтровано по тэгу [${dbTags[position].item_title}]", Snackbar.LENGTH_SHORT).show()
                    } else Snackbar.make(layoutMain, "Фильтровать нечего", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializeField() {
        //Инициализируем библотеку для работы с фото
        Fresco.initialize(this)

        //получаем ссылки на элементы графического интерфейса
        layoutMain = findViewById(R.id.layout_activity_main)
        viewRecyclerView = findViewById<View>(R.id.view_recycler) as RecyclerView
        viewSpinner = findViewById<View>(R.id.spinner) as Spinner
        switcher = findViewById<View>(R.id.switch_filter) as Switch

        //поидее должна заработать анимация, тока чёт не работает - пример брал с ресурса
        //https://android-tools.ru/coding/dobavlyaem-knopki-pri-svajpe-v-recyclerview/
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 500
        itemAnimator.removeDuration = 500
        viewRecyclerView.itemAnimator = itemAnimator
        viewRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    /** Функиция перечитывает БД и присваивает новый адаптер для recyclerView */
    private fun refreshBD() {
        fun getCursor(nameDB: String) = database.query(nameDB, null, null,
                null, null, null, DBHelper.KEY_ID + " DESC")

        //очищаем список элементов и тэгов
        items.clear()
        dbTags.clear()
        database = DBHelper(this).writableDatabase

        //создаем курсор для просмотра таблицы записей
        val cursorItem = getCursor(DBHelper.TABLE_ITEMS)

        //пробегаем по курсору (по базе - построчно)
        if (cursorItem.moveToFirst()) {
            do {
                //Добавить выборку из базы по внешним ключам и сформировать список тэгов
                //наполняем наш список элементами
                val itemId: String = cursorItem.getDBhelperField(DBHelper.KEY_ID)
                val itemTite = cursorItem.getDBhelperField(DBHelper.KEY_TITLE)
                val tags: MutableList<Tag> = mutableListOf()

                val cursorItemsTag = DBHelper(this).getItemsTagCursor(itemId)
                if (cursorItemsTag.moveToFirst()) {
                    do {
                        val tag = Tag(cursorItemsTag.getDBhelperField(DBHelper.KEY_ID),
                                cursorItemsTag.getDBhelperField(DBHelper.KEY_TAG))
                        tags.add(tag)
                    } while (cursorItemsTag.moveToNext())
                }
                cursorItemsTag.close()

                items.add(Item(itemId,
                        itemTite,
                        cursorItem.getDBhelperField(DBHelper.KEY_RATING).toFloat(),
                        cursorItem.getDBhelperField(DBHelper.KEY_IMAGE),
                        tags))
            } while (cursorItem.moveToNext())
        } else Snackbar.make(layoutMain, getString(R.string.action_empty_db), Snackbar.LENGTH_SHORT).show()

        if (switcher.isChecked) {
            //создаем курсор для просмотра таблицы тэгов сортируем его по убыванию
            val cursorTag = getCursor(DBHelper.TABLE_TAGS)
            if (cursorTag.moveToFirst()) do { //наполняем наш список элементами
                dbTags.add(Tag(cursorTag.getDBhelperField(DBHelper.KEY_ID),
                        cursorTag.getDBhelperField(DBHelper.KEY_TAG)))
            } while (cursorTag.moveToNext())
            //устанавливаем адаптер для спиннера
            viewSpinner.adapter = TagAdapterSpinner(dbTags)
            cursorTag.close()
        }
        //устанавливаем адаптер для RecyclerView с значениями из базы данных
        viewRecyclerView.adapter = ItemAdapter(viewRecyclerView, items, this)

        cursorItem.close()
        database.close()
    }

    /** Функция выводит в лог всю базу данных */
    private fun printDB() {
        val databaseItemLog = DBHelper(this).writableDatabase
        val cursorItemLog = databaseItemLog.query(DBHelper.TABLE_ITEMS,
                null, null, null, null, null, null)
        printLog("TABLE_ITEMS:")
        if (cursorItemLog.moveToFirst()) {
            do {
                printLog(
                        "id[${cursorItemLog.getDBhelperField(DBHelper.KEY_ID)}], " +
                                "title[${cursorItemLog.getDBhelperField(DBHelper.KEY_TITLE)}], " +
                                "rating[${cursorItemLog.getDBhelperField(DBHelper.KEY_RATING)}], " +
                                "image[${cursorItemLog.getDBhelperField(DBHelper.KEY_IMAGE)}]")
            } while (cursorItemLog.moveToNext())
        } else printLog("EMPTY")
        cursorItemLog.close()
        databaseItemLog.close()

        val databaseTagLog = DBHelper(this).writableDatabase
        val cursorTagLog = databaseTagLog.query(DBHelper.TABLE_TAGS,
                null, null, null, null, null, null)
        printLog("TABLE_TAGS:")
        if (cursorTagLog.moveToFirst()) do printLog(
                "id[${cursorTagLog.getString(cursorTagLog.getColumnIndex(DBHelper.KEY_ID))}], " +
                        "tag[${cursorTagLog.getString(cursorTagLog.getColumnIndex(DBHelper.KEY_TAG))}]")
        while (cursorTagLog.moveToNext()) else printLog("EMPTY")
        cursorTagLog.close()
        databaseTagLog.close()

        val databaseItemTagLog = DBHelper(this).writableDatabase
        val cursorItemTagLog = databaseItemTagLog.query(DBHelper.TABLE_ITEMS_TAGS,
                null, null, null, null, null, null)
        printLog("TABLE_ITEMS_TAGS:")
        if (cursorItemTagLog.moveToFirst()) {
            do printLog("item_id[${cursorItemTagLog.getDBhelperField(DBHelper.KEY_ITEM_ID)}], " +
                    "tag_id[${cursorItemTagLog.getDBhelperField(DBHelper.KEY_TAG_ID)}]")
            while (cursorItemTagLog.moveToNext())
        } else printLog("EMPTY")
        cursorItemTagLog.close()
        databaseItemTagLog.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear_db -> {
                database = DBHelper(this).writableDatabase
                database.delete(DBHelper.TABLE_ITEMS, null, null)
                database.delete(DBHelper.TABLE_ITEMS_TAGS, null, null)
                database.delete(DBHelper.TABLE_TAGS, null, null)
                //подобным образом происходит перерисовка view т.к. adapter = null -соответственно
                //на экране ничего не отобразится
                viewRecyclerView.adapter = null
                viewSpinner.adapter = null

                Snackbar.make(layoutMain, getString(R.string.action_clear_db), Snackbar.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        refreshBD()
        printDB()
    }

    private fun printLog(message: String) {
        Log.d(LOGDEBUGTAG, message)
    }
}

fun Cursor.getDBhelperField(columnTitle: String) = this.getString(this.getColumnIndex(columnTitle)) ?: ""
