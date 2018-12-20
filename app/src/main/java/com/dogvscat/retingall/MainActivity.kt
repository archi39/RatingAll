package com.dogvscat.retingall

import android.content.Intent
import android.content.res.Resources
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
import android.widget.Spinner
import com.dogvscat.retingall.adapters.ItemAdapter
import com.dogvscat.retingall.adapters.TagAdapterSpinner
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_content_main.*
import kotlinx.android.synthetic.main.app_bar.*


class MainActivity : AppCompatActivity() {
    //специальное поле для отлавливания логов
    private val LOGDEBUGTAG: String = "POINT"
    private val REQUESTCODEADD: Int = 0
    private val REQUESTCODEEDIT: Int = 1
    private val REQUESTCODEADDTAG: Int = 2

    //объявляем ссылки на элементы формы
    private lateinit var layoutMain: View
    private lateinit var viewRecyclerView: RecyclerView
    private lateinit var viewSpinner: Spinner
    private lateinit var database: SQLiteDatabase

    val items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Инициализируем библотеку для работы с фото
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)
        //add toolbar to the activity
        setSupportActionBar(toolbar)

        //получаем ссылки на элементы графического интерфейса
        layoutMain = findViewById(R.id.layout_activity_main)
        viewRecyclerView = findViewById<View>(R.id.view_recycler) as RecyclerView
        viewSpinner = findViewById<View>(R.id.spinner) as Spinner

        //поидее должна заработать анимация, тока чёт не работает - пример брал с ресурса
        //https://android-tools.ru/coding/dobavlyaem-knopki-pri-svajpe-v-recyclerview/
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 500
        itemAnimator.removeDuration = 500
        viewRecyclerView.itemAnimator = itemAnimator

        viewRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //вызываем новое активити для добавления нового тэга
        view_text_tag_btn.setOnClickListener {
            Log.d(LOGDEBUGTAG, "Переходим на страницу для добавления тэга")
            startActivityForResult(Intent(this, AddTagActivity::class.java), REQUESTCODEADDTAG)
        }
        //наполняем экран данными из базы
        refreshBD()

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            if (items.size > 0) {
                Log.d(LOGDEBUGTAG, "Переходим на страницу для добавления элемента, последний ID: ${items[0].item_id}")
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("LASTITEMID", items[0].item_id)
                startActivityForResult(intent, REQUESTCODEADD)
            }else{
                Log.d(LOGDEBUGTAG, "Переходим на страницу для добавления элемента, база пустая, последний ID: Null")
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("LASTITEMID", "Null")
                startActivityForResult(intent, REQUESTCODEADD)
            }

        }

    }

    /**
     * Функиция перечитывает БД и присваивает новый адаптер для recyclerView
     */
    private fun refreshBD() {
        //очищаем список элементов
        items.clear()
        val tags = mutableListOf<Tag>()
        database = DBHelper(this).writableDatabase

        //создаем курсор для просмотра таблицы записей
        val cursorItem = database.query(DBHelper.TABLE_ITEMS,
                null,
                null,
                null,
                null,
                null,
                DBHelper.KEY_ID + " DESC")

        //создаем курсор для просмотра таблицы тэгов сортируем его по убыванию
        val cursorTag = database.query(DBHelper.TABLE_TAGS,
                null,
                null,
                null,
                null,
                null,
                DBHelper.KEY_ID + " DESC")

        //пробегаем по курсору (по базе - построчно)
        if (cursorItem.moveToFirst()) {
            do {
                //Добавить выборку из базы по внешним ключам и сформировать список тэгов
                //наполняем наш список элементами
                val itemId: String = cursorItem.getString(cursorItem.getColumnIndex(DBHelper.KEY_ID))
                val itemTite = cursorItem.getString(cursorItem.getColumnIndex(DBHelper.KEY_TITLE))
                val tags: MutableList<Tag> = mutableListOf()

                val cursorItemsTag = database.rawQuery(
                        "SELECT TT.${DBHelper.KEY_ID}, TT.${DBHelper.KEY_TAG} " +
                                "FROM ${DBHelper.TABLE_ITEMS_TAGS} as TIT " +
                                "JOIN ${DBHelper.TABLE_TAGS} as TT ON TIT.${DBHelper.KEY_TAG_ID}=TT.${DBHelper.KEY_ID} " +
                                "JOIN ${DBHelper.TABLE_ITEMS} as TI ON TIT.${DBHelper.KEY_ITEM_ID}=TI.${DBHelper.KEY_ID} " +
                                "WHERE TI.${DBHelper.KEY_ID} = '${itemId}'", null)
                if (cursorItemsTag.moveToFirst()) {
                    Log.d(LOGDEBUGTAG, "Элемент ${itemTite} обладает следующими тэгами:")
                    do {
                        val tag = Tag(cursorItemsTag.getString(cursorItemsTag.getColumnIndex(DBHelper.KEY_ID)),
                                cursorItemsTag.getString(cursorItemsTag.getColumnIndex(DBHelper.KEY_TAG)))
                        tags.add(tag)
                        Log.d(LOGDEBUGTAG, "ID: ${tag.item_id}; TITLE: ${tag.item_title}")
                    } while (cursorItemsTag.moveToNext())
                } else {
                    Log.d(LOGDEBUGTAG, "У ${itemTite} нет связанных тэгов")
                }
                cursorItemsTag.close()

                items.add(Item(itemId,
                        itemTite,
                        cursorItem.getString(cursorItem.getColumnIndex(DBHelper.KEY_RATING)).toFloat(),
                        tags))
            } while (cursorItem.moveToNext())
        } else {
            Snackbar.make(layoutMain, getString(R.string.action_empty_db), Snackbar.LENGTH_SHORT).show()
        }

        if (cursorTag.moveToFirst()) {
            do {
                //наполняем наш список элементами
                tags.add(Tag(cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)),
                        cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG))))
            } while (cursorTag.moveToNext())
        } else {
        }

        //устанавливаем адаптер для спиннера
        viewSpinner.adapter = TagAdapterSpinner(tags)
        //устанавливаем адаптер для RecyclerView с значениями из базы данных
        viewRecyclerView.adapter = ItemAdapter(viewRecyclerView, items, this)

        cursorItem.close()
        cursorTag.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_item_clear_db -> {
                database = DBHelper(this).writableDatabase
                database.delete(DBHelper.TABLE_ITEMS, null, null)
                //подобным образом происходит перерисовка view т.к. adapter = null -соответственно
                //на экране ничего не отобразится
                viewRecyclerView.adapter = null

                Snackbar.make(layoutMain, getString(R.string.action_clear_db), Snackbar.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(LOGDEBUGTAG, "Вернулись с результатом")
        super.onActivityResult(requestCode, resultCode, data)

        refreshBD()
        Log.d(LOGDEBUGTAG, "Добавили новую карточку")
    }

    //функция приеобразования Px to Dp
    fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    //функция преобразования Dp to Px
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}