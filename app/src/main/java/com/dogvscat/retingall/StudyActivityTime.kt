package com.dogvscat.retingall

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class StudyActivityTime : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_time)

        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        val string = dateFormat.format(Date(System.currentTimeMillis()))

        findViewById<TextView>(R.id.view_text_time).text = string
    }
}