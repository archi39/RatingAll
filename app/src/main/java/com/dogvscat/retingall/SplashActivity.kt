package com.dogvscat.retingall

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity


//экран загрузки
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}