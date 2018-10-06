package com.dogvscat.retingall

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper : SQLiteOpenHelper{
    //Объявляем константы для базы данных
    val DATABASE_VERSION: Int = 1
    val DATABASE_NAME: String = "itemsDb"
    val TABLE_CONTACTS: String = "items"

    //объявляем поля таблицы items
    val KEY_ID = "_id"
    val KEY_TITLE = "title"
    val KEY_RATING = 0F
    val KEY_IMAGE = "path to image"




    //так как у DBHelper нет первичного конструктора нужно вызвать конструктор суперкласа
    constructor(context: Context?, DATABASE_NAME: String?, DATABASE_VERSION: Int): super(context, DATABASE_NAME,null, DATABASE_VERSION)

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}