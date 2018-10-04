package com.dogvscat.retingall

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.app_bar.*

class AddActivity : AppCompatActivity() {
    private val LOGDEBUGTAG: String = "POINT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        but_submit.setOnClickListener{
            val intent = Intent()
            intent.putExtra("title",edit_text_title.text.toString())
            intent.putExtra("respect",edit_text_number.text.toString().toFloat())
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

        but_snapshot.setOnClickListener{

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
