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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
import kotlinx.android.synthetic.main.activity_add_tag.*
import kotlinx.android.synthetic.main.app_bar.*
import java.io.File

class AddActivity : AppCompatActivity() {
    private val LOGDEBUGTAG: String = "POINT"
    private val TAKE_PHOTO_REQUEST: Int = 0
    private var mCurrentPhotoPath: String = "none"
    private lateinit var viewRecyclerTagsAdd: RecyclerView
    private val tags: MutableList<Tag> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //resolve элементы формы
        viewRecyclerTagsAdd = findViewById<View>(R.id.view_recycler_tags_add) as RecyclerView
        viewRecyclerTagsAdd.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //указываем способ компоновки данных в списке
        //наполняем список элементами
        refreshTag()
        viewRecyclerTagsAdd.adapter = TagAdapterCardShort(viewRecyclerTagsAdd, tags, this)

        //не особо представляю зачем это взято из примера
        ButterKnife.bind(this)
        //работаем с базой данных
        val database: SQLiteDatabase = DBHelper(this).writableDatabase


        //выводим список уже созданных тэгов
        findViewById<View>(R.id.view_tag_add_list).setOnClickListener() {
            showEditCardDialog()
        }

        //Добавляем тэг из текстового поля
        findViewById<View>(R.id.btn_new_tag).setOnClickListener(){
            val contentValues = ContentValues()
            //проверяем что в базе есть тэги в будующем нужно поменять на запрос последнего ид из базы
            val tag = if(tags.size>0){
                Tag("${(tags[0].item_id).toInt()+1}", findViewById<EditText>(R.id.view_text_tag_new).text.toString())
            }
            else null
            //Log.d(LOGDEBUGTAG,"ADD TAG id: ${tag!!.item_id}, title: ${tag.item_title}")
            //Log.d(LOGDEBUGTAG,"RESOLVE DB:")
            contentValues.put(DBHelper.KEY_TAG, tag!!.item_title)
            database.insert(DBHelper.TABLE_TAGS, null, contentValues)
            refreshTag()
        }

        but_snapshot.setOnClickListener { validatePermissions() }

        but_submit.setOnClickListener {
            val contentValues = ContentValues()
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
    }

    /**
     * метод взят с ресурса: https://code.luasoftware.com/tutorials/android/android-text-input-dialog-with-inflated-view-kotlin/
     * создает собственное диалоговое окно
     */
    fun showEditCardDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Редактирование")

        // https://stackoverflow.com/questions/10695103/creating-custom-alertdialog-what-is-the-root-view
        // Seems ok to inflate view with null rootView
        val view = (this as Activity).layoutInflater.inflate(R.layout.dialog_add_item_tag_list, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.view_recycler_add_item_tags_list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = TagAdapterCardShort(recyclerView, tags, this)
        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton("Ок") { dialog, _ ->

            /* Snackbar.make(viewRecyclerTagsAdd, "Тэг ${tag.item_title} успешно изменен на ${editText.text}", Snackbar.LENGTH_SHORT).show()
             tag.item_title = editText.text.toString()
             val database = DBHelper(this).writableDatabase
             val contentValues = ContentValues()
             contentValues.put(DBHelper.KEY_TAG,editText.text.toString())
             // обновляем по id
             database.update(DBHelper.TABLE_TAGS, contentValues, "_id = ?", arrayOf<String>(tag.item_id))
             viewRecyclerTagsAdd.adapter = TagAdapterList(viewRecyclerTagsAdd, tags, this)*/

        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    //сейчас выводит все тэги, которые есть в базе
    private fun refreshTag() {
        tags.clear()

        val database = DBHelper(this).writableDatabase

        //создаем курсор для просмотра таблицы тэгов сортируем его по убыванию
        val cursorTag = database.query(DBHelper.TABLE_TAGS,
                null,
                null,
                null,
                null,
                null,
                DBHelper.KEY_ID + " DESC")

        if (cursorTag.moveToFirst()) {
            do {
                if (cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)).equals("Добавить"))
                else {
                    //наполняем наш список элементами
                    val tag = Tag(cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)),
                            cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)))
                    tags.add(tag)

                    //смотрим в лог
                    Log.d(LOGDEBUGTAG,"id: ${tag.item_id}, title: ${tag.item_title}")
                }
            } while (cursorTag.moveToNext())
        }
        cursorTag.close()
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
