package com.dogvscat.retingall

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_study_info.*
import kotlinx.android.synthetic.main.app_bar.*
import java.lang.String.format
import java.text.SimpleDateFormat
import java.util.*

class StudyActivityInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_info)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        /*из урока про обработку action
        val action: String? = intent.action
        var formatT = ""
        var textinfo = ""

        if (action.equals("info.fandroid.intent.action.time")) {
            formatT = "HH:mm:ss"
            textinfo = "time: "
        } else if (action.equals("info.fandroid.intent.action.date")) {
            formatT = "dd.MM.yyyy"
            textinfo = "Date: "
        }

        val dateTime = SimpleDateFormat(formatT).format(Date(System.currentTimeMillis()))
        val displayText: String = textinfo + dateTime
        view_text_info.text = displayText*/

        val str = intent.getStringExtra("data") + "!!!"
        view_text_info.text = str

        but_ok.setOnClickListener {
            setResult(Activity.RESULT_OK,Intent().putExtra("data",edit_text.text.toString()))
            finish()
            //Intent().putExtra("data",edit_text.text.toString())
        }
    }
}
