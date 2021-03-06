package com.dogvscat.retingall

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogvscat.retingall.adapters.TagAdapterCardShort
import com.dogvscat.retingall.adapters.TagAdapterListCardShort
import com.dogvscat.retingall.dto.Tag
import com.dogvscat.retingall.services.DBHelper
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.app_bar.*
import java.io.File

/**
 * Добавление нового элемента в базу данных
 *
 * @author EvgenySamarin [GitHub](https://github.com/EvgenySamarin)
 * @since update 2019.12.02 v1
 */
class AddActivity : AppCompatActivity() {
    private val TAKE_PHOTO_REQUEST: Int = 0
    private var mCurrentPhotoPath: String = "none"
    private lateinit var viewRecyclerTagsAdd: RecyclerView
    //список всех тэгов в базе
    private val dbTags: MutableList<Tag> = mutableListOf()
    //список тэгов для возможного добавления в базу
    private val itemTags: MutableList<Tag> = mutableListOf()
    private lateinit var lastItemId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //включаем toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lastItemId = intent.getStringExtra("LASTITEMID").toString()

        //Тэги закрепленные за элементом (изначально при запуске должно быть пусто)
        viewRecyclerTagsAdd = findViewById<View>(R.id.view_recycler_tags_add) as RecyclerView
        viewRecyclerTagsAdd.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //набиваем пепременную тэгами из базы данных
        refreshDbTag()
        initListeners()
    }

    private fun initListeners() {
        //выводим список уже созданных тэгов
        findViewById<View>(R.id.view_tag_add_list).setOnClickListener {
            showListCardDialog()
        }

        //Добавляем тэг из текстового поля
        findViewById<View>(R.id.btn_new_tag).setOnClickListener {
            val tag = Tag("Null", findViewById<EditText>(R.id.view_text_tag_new).text.toString())
            //добавляем новый тэг
            itemTags.add(tag)
            //отображаем добавленный тэг в списке тэгов
            viewRecyclerTagsAdd.adapter = TagAdapterCardShort(viewRecyclerTagsAdd, itemTags, this)
        }

        but_snapshot.setOnClickListener { validatePermissions() }

        //все действия с изменением базы данных нужно вносить именно сюда
        but_submit.setOnClickListener {
            //работаем с базой данных
            val database: SQLiteDatabase = DBHelper(this).writableDatabase
            //создаем новую запись в таблице TABLE_ITEMS
            val contentValuesItem = ContentValues()
            contentValuesItem.put(DBHelper.KEY_TITLE, edit_text_title.text.toString())
            contentValuesItem.put(DBHelper.KEY_RATING, circle_view_add.currentValue)
            contentValuesItem.put(DBHelper.KEY_IMAGE, mCurrentPhotoPath)
            database.insert(DBHelper.TABLE_ITEMS, null, contentValuesItem)

            insertNewTagToDatabase(database)

            database.close()
            val intent = Intent()
            intent.putExtra("title", edit_text_title.text.toString())
            intent.putExtra("respect", circle_view_add.currentValue)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun insertNewTagToDatabase(database: SQLiteDatabase) {
        //Наполняем базу новыми тэгами c проверкой на совпадения
        if (itemTags.size > 0) {
            val contentValuesTags = ContentValues()
            for (tag in itemTags) {
                //если в базе уже есть тэги - проверяем на совпадение
                if (dbTags.size > 0) {
                    var contain = false
                    for (dbTag in dbTags) {
                        if (tag.item_title.equals(dbTag.item_title))
                            contain = true
                    }
                    if (!contain) {
                        contentValuesTags.put(DBHelper.KEY_TAG, tag.item_title)
                        database.insert(DBHelper.TABLE_TAGS, null, contentValuesTags)
                    }
                }
                //если тэгов нет сразу добавляем в таблицу тэгов
                else {
                    contentValuesTags.put(DBHelper.KEY_TAG, tag.item_title)
                    database.insert(DBHelper.TABLE_TAGS, null, contentValuesTags)
                }
            }
            //заполняем базу связкками тэг - элемент
            val contentValuesItemsTags = ContentValues()

            refreshDbTag()
            for (tag in itemTags) {
                //проставляем id для наших тэгов
                for (dbTag in dbTags) {
                    if (tag.item_title == dbTag.item_title) {
                        tag.item_id = dbTag.item_id
                        break
                    }
                }
                if (lastItemId == "Null") {
                    contentValuesItemsTags.put(DBHelper.KEY_ITEM_ID, 1)
                } else {
                    contentValuesItemsTags.put(DBHelper.KEY_ITEM_ID, lastItemId.toInt() + 1)
                }
                contentValuesItemsTags.put(DBHelper.KEY_TAG_ID, tag.item_id)
                database.insert(DBHelper.TABLE_ITEMS_TAGS, null, contentValuesItemsTags)
            }
        }
    }

    /**
     * метод взят с ресурса: https://code.luasoftware.com/tutorials/android/android-text-input-dialog-with-inflated-view-kotlin/
     * создает окно для добавления существующих тэгов из базы
     */
    private fun showListCardDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Тэги")
        // https://stackoverflow.com/questions/10695103/creating-custom-alertdialog-what-is-the-root-view
        // Seems ok to inflate view with null rootView
        val view = (this as Activity).layoutInflater.inflate(R.layout.dialog_add_item_tag_list, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.view_recycler_add_item_tags_list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = TagAdapterListCardShort(recyclerView, itemTags, dbTags, this)
        builder.setView(view)

        builder.setPositiveButton("Ок") { _, _ ->
            //обновляем список тэгов на странице
            viewRecyclerTagsAdd.adapter = TagAdapterCardShort(viewRecyclerTagsAdd, itemTags, this)
        }
        builder.show()
    }

    //выводит все тэги, которые есть в базе
    private fun refreshDbTag() {
        dbTags.clear()
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
                if (cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)) != "Добавить") {
                    //наполняем наш список элементами
                    val tag = Tag(cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_ID)),
                            cursorTag.getString(cursorTag.getColumnIndex(DBHelper.KEY_TAG)))
                    dbTags.add(tag)
                }
            } while (cursorTag.moveToNext())
        }
        cursorTag.close()
        database.close()
    }

    /** Проверка дступных разрешенй на использвание камеры */
    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        launchCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: com.karumi.dexter.listener.PermissionRequest?, token: PermissionToken?) {
                        AlertDialog.Builder(this@AddActivity)
                                .setTitle(R.string.storage_permission_rationale_title)
                                .setMessage(R.string.storage_permition_rationale_message)
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

    /** Ипользуем камеру */
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

    /** Проверяем, получено ли фото */
    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) processCapturedPhoto()
        else super.onActivityResult(requestCode, resultCode, data)
    }

    /** обрабатываем полученное фото */
    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(
                Uri.parse(mCurrentPhotoPath),
                Array(1) { MediaStore.Images.ImageColumns.DATA },
                null,
                null,
                null)
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
