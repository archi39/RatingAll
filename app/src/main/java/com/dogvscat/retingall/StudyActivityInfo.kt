package com.dogvscat.retingall

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_study_info.*
import java.lang.String.format
import java.text.SimpleDateFormat
import java.util.*

class StudyActivityInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_info)


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
    }
}
