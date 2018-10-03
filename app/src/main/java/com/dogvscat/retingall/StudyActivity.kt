package com.dogvscat.retingall

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import kotlinx.android.synthetic.main.activity_custom_toast.*
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.app_bar.*

//Добавим комментарий для тестирования новой созданной ветки
class StudyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        but_time.setOnClickListener {
            startActivity(Intent("info.fandroid.intent.action.time"))
        }
        but_date.setOnClickListener {
            startActivity(Intent("info.fandroid.intent.action.date"))
        }

        but_submit.setOnClickListener {
            //startActivity(Intent(this,StudyActivityInfo::class.java).putExtra("data",edit_text.text.toString()))

            startActivityForResult(Intent(this,StudyActivityInfo::class.java).putExtra("data",edit_text.text.toString()),1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val name = data!!.getStringExtra("data") + "!!!"
        view_text.text = name

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
