package com.dogvscat.retingall

import android.os.Bundle
import android.app.Activity

import kotlinx.android.synthetic.main.activity_study.*

class StudyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
