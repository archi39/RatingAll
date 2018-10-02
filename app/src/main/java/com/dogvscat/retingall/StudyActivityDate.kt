package com.dogvscat.retingall

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class StudyActivityDate : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_date)

        val dateFormat = SimpleDateFormat("dd:MM:yyyy")
        val string = dateFormat.format(Date(System.currentTimeMillis()))

        findViewById<TextView>(R.id.view_text_date).text = string
    }
}
