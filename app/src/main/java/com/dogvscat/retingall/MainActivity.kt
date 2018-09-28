package com.dogvscat.retingall

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var layoutMain: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        layoutMain = findViewById(R.id.layout_activity_main)

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
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
