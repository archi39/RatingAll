package com.dogvscat.retingall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


//экран загрузки
/**
 * Экран предзагрузки
 *
 * @author EvgenySamarin
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}