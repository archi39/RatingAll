package com.dogvscat.retingall

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists $TABLE_ITEMS")

        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table $TABLE_ITEMS (" +
                "$KEY_ID integer primary key," +
                "$KEY_TITLE text," +
                "$KEY_RATING real," +
                "$KEY_IMAGE text)")
    }

    //аналог final static полей Java
    companion object {
        const val KEY_TITLE = "title"
        //объявляем поля таблицы items
        const val KEY_ID = "_id"
        const val KEY_RATING = "rating"
        const val KEY_IMAGE = "path_to_image"
        const val TABLE_ITEMS = "items"
        //Объявляем константы для базы данных
        const val DATABASE_VERSION: Int = 1
        const val DATABASE_NAME: String = "itemsDb"
    }

}