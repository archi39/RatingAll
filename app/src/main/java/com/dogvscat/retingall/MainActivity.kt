package com.dogvscat.retingall

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import at.grabner.circleprogress.CircleProgressView
import at.grabner.circleprogress.UnitPosition
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.app_bar.*


class MainActivity : AppCompatActivity() {
    //специальное поле для отлавливания логов
    private val LOGDEBUGTAG: String = "POINT"
    private val REQUESTCODEADD: Int = 0

    //получаем ссылки на элементы формы
    private lateinit var layoutMain: View
    private lateinit var viewLinearCard: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Инициализируем библотеку для работы с фото
        Fresco.initialize(this);
        setContentView(R.layout.activity_main)
        //add toolbar to the activity
        setSupportActionBar(toolbar)

        //получаем ссылки на элементы графического интерфейса
        layoutMain = findViewById(R.id.layout_activity_main)
        viewLinearCard = findViewById(R.id.view_linear_card)



        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            startActivityForResult(Intent(this, AddActivity::class.java), REQUESTCODEADD)
            Log.d(LOGDEBUGTAG, "Перешли на страницу для добавления элемента")
        }
    }

    /**
     * Функиця для создания новой карточки с указанием названия и текущего уровня респекта
     * @author Самарин Е
     * @return Возвращяет созданную карточку CardView
     * @param title указывает наименование добавляемой позиции
     * @param respect указывает текущий уровень рейтинга
     */
    private fun createCardViewItem(title: String, respect: Float): CardView {

        //создаем CardView пустышку
        val viewCardItem = CardView(this)
        // Initialize a new LayoutParams instance, CardView width and height
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // CardView width
                LinearLayout.LayoutParams.WRAP_CONTENT // CardView height
        )
        layoutParams.setMargins(5.toPx(), 5.toPx(), 5.toPx(), 5.toPx())
        viewCardItem.layoutParams = layoutParams
        viewCardItem.radius = 5.toPx().toFloat()
        // Set the card view content padding
        viewCardItem.setContentPadding(5.toPx(), 5.toPx(), 5.toPx(), 5.toPx())
        // Set the card view background color
        viewCardItem.setCardBackgroundColor(getColor(R.color.colorListItemBG))

        //Делаем Layout для корректного добавления элементов
        val lineralLayout = LinearLayout(this)
        lineralLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                LinearLayout.LayoutParams.WRAP_CONTENT // height
        )
        lineralLayout.orientation = LinearLayout.HORIZONTAL

        //Создаем текстовое поле с названием позиции и добавляем его на карточку
        val viewTextItem = TextView(this)
        val layoutParamsText = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                LinearLayout.LayoutParams.MATCH_PARENT, // height
                1F
        )
        viewTextItem.layoutParams = layoutParamsText
        viewTextItem.gravity = Gravity.START or Gravity.CENTER
        viewTextItem.text = title

        //Создаем красивый прогресс бар и добавляем его на карточку
        val viewCircleProgressItem = CircleProgressView(this, null)
        // Initialize a new LayoutParams instance, CircleProgressView width and height
        val layoutParamsCircle = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // width
                LinearLayout.LayoutParams.WRAP_CONTENT // height
        )
        layoutParamsCircle.height = 50.toPx()
        viewCircleProgressItem.layoutParams = layoutParamsCircle

        //специфичные стили
        viewCircleProgressItem.isAutoTextSize = true
        viewCircleProgressItem.setBarColor(getColor(R.color.colorPrimary))
        viewCircleProgressItem.barWidth = 4.toPx()
        viewCircleProgressItem.innerContourSize = 0F
        viewCircleProgressItem.maxValue = 100F
        viewCircleProgressItem.outerContourSize = 0F
        viewCircleProgressItem.rimColor = getColor(R.color.colorAccent)
        viewCircleProgressItem.rimWidth = 4.toPx()
        viewCircleProgressItem.isSeekModeEnabled = false
        viewCircleProgressItem.setSpinBarColor(getColor(R.color.colorPrimary))
        viewCircleProgressItem.setTextColor(getColor(R.color.colorProgressBarText))
        viewCircleProgressItem.textScale = 1F
        viewCircleProgressItem.unit = "rp"
        viewCircleProgressItem.setUnitColor(getColor(R.color.colorProgressBarText))
        viewCircleProgressItem.setUnitPosition(UnitPosition.RIGHT_TOP)
        viewCircleProgressItem.unitScale = 1F
        viewCircleProgressItem.isUnitVisible = true
        viewCircleProgressItem.setValue(respect)

        //набрасываем созданное на наши view
        lineralLayout.addView(viewTextItem)
        lineralLayout.addView(viewCircleProgressItem)
        viewCardItem.addView(lineralLayout)

        return viewCardItem
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

        //выводим на экран cardView
        viewLinearCard.addView(createCardViewItem(
                data!!.getStringExtra("title"),
                data.getFloatExtra("respect", 0F)
        ))
        Log.d(LOGDEBUGTAG, "Добавили новую карточку")
    }

    //функция приеобразования Px to Dp
    fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    //функция преобразования Dp to Px
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}