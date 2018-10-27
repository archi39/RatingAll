package com.dogvscat.retingall

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.app_bar.*


class MainActivity : AppCompatActivity() {
    //специальное поле для отлавливания логов
    private val LOGDEBUGTAG: String = "POINT"
    private val REQUESTCODEADD: Int = 0

    //объявляем ссылки на элементы формы
    private lateinit var layoutMain: View
    private lateinit var viewRecyclerView: RecyclerView

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
        val linerlLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false )
        viewRecyclerView.layoutManager = linerlLayoutManager

        //наполняем экран данными из базы
        refreshBD()

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            startActivityForResult(Intent(this, AddActivity::class.java), REQUESTCODEADD)
            Log.d(LOGDEBUGTAG, "Перешли на страницу для добавления элемента")
        }
    }

    /**
     * Функиция перечитывает БД и присваивает новый адаптер для recyclerView
     */
    private fun refreshBD() {
        //Создаем изменяемый список в который будем помещать элементы из базы
        val itemsTitle = mutableListOf<String>()
        val itemsRating = mutableListOf<Float>()

        //создаем курсор для просмотра БД
        val cursor = DBHelper(this).writableDatabase.query(DBHelper.TABLE_ITEMS,
                null,
                null,
                null,
                null,
                null,
                null)

        //пробегаем по курсору (по базе - построчно)
        if (cursor.moveToFirst()) {
            do {
                //наполняем наш список элементами
                itemsTitle.add(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TITLE)))
                itemsRating.add(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_RATING)).toFloat())

            } while (cursor.moveToNext())
        } else {
            Snackbar.make(layoutMain, "В таблице нет строк", Snackbar.LENGTH_SHORT).show()
        }

        //устанвливаем адаптер для RecyclerView с значениями из базы данных
        viewRecyclerView.adapter = MyAdapter(itemsTitle.toTypedArray(), itemsRating.toTypedArray(),this)

        cursor.close()
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
            R.id.menu_item_settings -> {
                Snackbar.make(layoutMain, getString(R.string.title_menu_settings), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
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