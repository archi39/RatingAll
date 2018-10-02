package com.dogvscat.retingall

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import at.grabner.circleprogress.CircleProgressView
import com.dogvscat.retingall.R.id.toolbar
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar.*


class MainActivity : AppCompatActivity() {
    //специальное поле для отлавливания логов
    private val LOGDEBUGTAG:String = "POINT"

    //получаем ссылки на элементы формы
    private lateinit var layoutMain: View
    private lateinit var viewLinearCard: LinearLayout

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //add toolbar to the activity
        setSupportActionBar(toolbar)

        //получаем ссылки на элементы графического интерфейса
        layoutMain = findViewById(R.id.layout_activity_main)
        viewLinearCard = findViewById(R.id.view_linear_card)

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            //создаем CardView пустышку
            val viewCardItem = CardView(this)
            viewCardItem.radius = 15F
            viewCardItem.setCardBackgroundColor(Color.RED)

            //Создаем текстовое поле с названием позиции
            val viewTextItem = TextView(this)
            viewTextItem.text = R.string.string_test_short_rus.toString()

            //Создаем красивый прогресс бар
            val attr : AttributeSet? = null
            val viewCircleProgressItem = CircleProgressView(this,attr)
            viewCircleProgressItem.isAutoTextSize = true
            viewCircleProgressItem.setBarColor(Color.RED)
            viewCircleProgressItem.barWidth = 4

            //создаем параметры вставки для CardView
            val paramsViewWC: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            paramsViewWC.setMargins(15,15,15,15)

            //Наполняем CardView
            viewCardItem.addView(viewTextItem,ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT))
            viewCardItem.addView(viewCircleProgressItem,ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT))

            //выводим на экран cardView
            viewLinearCard.addView(viewCardItem,paramsViewWC)

            Log.d(LOGDEBUGTAG,"Добавили новую карточку")
        }

        findViewById<FloatingActionButton>(R.id.fab_learn).setOnClickListener {
            startActivity(Intent(this,StudyActivity::class.java))
            Log.d(LOGDEBUGTAG,"Перешли на страницу для тестов")
        }
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
                Snackbar.make(layoutMain, getString(R.string.menu_settings), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //функция приеобразования Px to Dp
    fun Int.toDp():Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    //функция преобразования Dp to Px
    fun Int.toPx():Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
