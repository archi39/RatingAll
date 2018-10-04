package com.dogvscat.retingall

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.webkit.PermissionRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
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

        but_submit.setOnClickListener {
            val intent = Intent()
            intent.putExtra("title", edit_text_title.text.toString())
            intent.putExtra("respect", edit_text_number.text.toString().toFloat())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        but_snapshot.setOnClickListener { validatePermissions() }
    }

    /**
     * Проверка дступных разрешенй на использвание камеры
     */
    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(
                            response: PermissionGrantedResponse?) {
                        launchCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?) {
                        AlertDialog.Builder(this@MainActivity)
                                .setTitle(
                                        R.string.storage_permission_rationale_title)
                                .setMessage(
                                        R.string.storage_permition_rationale_message)
                                .setNegativeButton(
                                        android.R.string.cancel,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.cancelPermissionRequest()
                                        })
                                .setPositiveButton(android.R.string.ok,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.continuePermissionRequest()
                                        })
                                .setOnDismissListener({
                                    token?.cancelPermissionRequest() })
                                .show()
                    }

                    override fun onPermissionDenied(
                            response: PermissionDeniedResponse?) {
                        Snackbar.make(mainContainer!!,
                                R.string.storage_permission_denied_message,
                                Snackbar.LENGTH_LONG)
                                .show()
                    }
                })
                .check()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
