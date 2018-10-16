package com.dogvscat.retingall

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import butterknife.ButterKnife
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.app_bar.*
import java.io.File

class AddActivity : AppCompatActivity() {
    private val LOGDEBUGTAG: String = "POINT"
    private val TAKE_PHOTO_REQUEST: Int = 0
    private var mCurrentPhotoPath: String = "none"
    //lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //не особо представляю зачем это взято из примера
        ButterKnife.bind(this)
        //работаем с базой данных
       // dbHelper = DBHelper(this)
        val database: SQLiteDatabase = DBHelper(this).writableDatabase
        val contentValues = ContentValues()

        but_snapshot.setOnClickListener { validatePermissions() }

        but_submit.setOnClickListener {

            contentValues.put(DBHelper.KEY_TITLE, edit_text_title.text.toString())
            contentValues.put(DBHelper.KEY_RATING, edit_text_number.text.toString().toFloat())
            contentValues.put(DBHelper.KEY_IMAGE, mCurrentPhotoPath)
            database.insert(DBHelper.TABLE_ITEMS, null, contentValues)

            val intent = Intent()
            intent.putExtra("title", edit_text_title.text.toString())
            intent.putExtra("respect", edit_text_number.text.toString().toFloat())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        but_read.setOnClickListener {
            val cursor = database.query(DBHelper.TABLE_ITEMS,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)

            if (cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndex(DBHelper.KEY_ID)
                val titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE)
                val ratingIndex = cursor.getColumnIndex(DBHelper.KEY_RATING)
                val imageIndex = cursor.getColumnIndex(DBHelper.KEY_IMAGE)
                do {
                    Log.d(LOGDEBUGTAG, "ID = ${cursor.getInt(idIndex)}," +
                            "title = ${cursor.getString(titleIndex)}," +
                            "rating = ${cursor.getFloat(ratingIndex)}," +
                            "imagePath = ${cursor.getString(imageIndex)}")
                } while (cursor.moveToNext())
            } else Log.d(LOGDEBUGTAG, "в таблице нет строк")
            cursor.close()
        }

        btn_clear.setOnClickListener {
            database.delete(DBHelper.TABLE_ITEMS,null,null)
            Snackbar.make(layout_add_main,"База очищена", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Проверка дступных разрешенй на использвание камеры
     */
    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        launchCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: com.karumi.dexter.listener.PermissionRequest?, token: PermissionToken?) {
                        AlertDialog.Builder(this@AddActivity)
                                .setTitle(
                                        R.string.storage_permission_rationale_title)
                                .setMessage(
                                        R.string.storage_permition_rationale_message)
                                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                                    dialog.dismiss()
                                    token?.cancelPermissionRequest()
                                }
                                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                                    dialog.dismiss()
                                    token?.continuePermissionRequest()
                                }
                                .setOnDismissListener {
                                    token?.cancelPermissionRequest()
                                }
                                .show()
                    }

                    override fun onPermissionDenied(
                            response: PermissionDeniedResponse?) {
                        Snackbar.make(layout_add_main!!,
                                R.string.storage_permission_denied_message,
                                Snackbar.LENGTH_LONG)
                                .show()
                    }
                })
                .check()
    }

    /**
     * Ипользуем камеру
     */
    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            mCurrentPhotoPath = fileUri!!.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    /**
     * Проверяем, получено ли фото
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * обрабатываем полученное фото
     */
    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                Array(1) { android.provider.MediaStore.Images.ImageColumns.DATA },
                null, null, null)
        cursor!!.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)

        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)

        val request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(ResizeOptions(width, height))
                .build()
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(view_simple_image?.controller)
                .setImageRequest(request)
                .build()
        view_simple_image?.controller = controller
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
