package com.dogvscat.retingall

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import at.grabner.circleprogress.CircleProgressView


class MainActivity : AppCompatActivity() {
    private lateinit var layoutMain: View
    private lateinit var viewLinearCard: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //получаем ссылки на элементы графического интерфейса
        layoutMain = findViewById(R.id.layout_activity_main)
        viewLinearCard = findViewById(R.id.view_linear_card)

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            //создаем CardView пустышку
           // val viewCardItem = CardView(this)
            //val viewTextItem = TextView(this)

            //val attrItem: AttributeSet
//Нужно разобраться с Атрибутами
            //val viewCircleProgressItem = CircleProgressView(this,)



            Snackbar.make(layoutMain, "Test", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun addNewCard() {

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

}
