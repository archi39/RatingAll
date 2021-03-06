package com.dogvscat.retingall.services

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Класс описывает логику базы данных. Создание и обновление версий БД */
class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /** получить курсор тегов для указанного элемента БД */
    fun getItemsTagCursor(itemId: String) = this.writableDatabase.rawQuery(
            "SELECT TT.$KEY_ID, TT.$KEY_TAG " +
                    "FROM $TABLE_ITEMS_TAGS as TIT " +
                    "JOIN $TABLE_TAGS as TT ON TIT.$KEY_TAG_ID=TT.$KEY_ID " +
                    "JOIN $TABLE_ITEMS as TI ON TIT.$KEY_ITEM_ID=TI.$KEY_ID " +
                    "WHERE TI.$KEY_ID = '${itemId}'", null)


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Обновлемся с версии 1 на версию 2 ( верисия с архитектурой тегов)
        if (oldVersion == 1 && newVersion == 2) {
            //начало транзакции
            db!!.beginTransaction()
            try {
                // создаем таблицу тегов
                db.execSQL("create table $TABLE_TAGS (" +
                        "$KEY_ID integer primary key," +
                        "$KEY_TAG text)")

                // создаем таблицу связей
                db.execSQL("PRAGMA foreign_keys=on")
                db.execSQL("create table $TABLE_ITEMS_TAGS (" +
                        "$KEY_ITEM_ID  INTEGER NOT NULL," +
                        "$KEY_TAG_ID INTEGER NOT NULL," +
                        "FOREIGN KEY ($KEY_ITEM_ID) REFERENCES $TABLE_ITEMS($KEY_ID)" +
                        "FOREIGN KEY ($KEY_TAG_ID) REFERENCES $TABLE_TAGS($KEY_ID))")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table $TABLE_ITEMS (" +
                "$KEY_ID integer primary key," +
                "$KEY_TITLE text," +
                "$KEY_RATING real," +
                "$KEY_IMAGE text)")

        db.execSQL("create table $TABLE_TAGS (" +
                "$KEY_ID integer primary key," +
                "$KEY_TAG text)")


        db.execSQL("PRAGMA foreign_keys=on;")
        db.execSQL("create table $TABLE_ITEMS_TAGS (" +
                "$KEY_ITEM_ID  INTEGER NOT NULL," +
                "$KEY_TAG_ID INTEGER NOT NULL," +
                "FOREIGN KEY ($KEY_ITEM_ID) REFERENCES $TABLE_ITEMS($KEY_ID) " +
                "FOREIGN KEY ($KEY_TAG_ID) REFERENCES $TABLE_TAGS($KEY_ID))")
    }

    //аналог final static полей Java
    companion object {
        const val KEY_TITLE = "title"
        //объявляем поля таблицы items
        const val KEY_ID = "_id"
        const val KEY_RATING = "rating"
        const val KEY_IMAGE = "path_to_image"
        const val TABLE_ITEMS = "items"
        //Добавили новые ссылки на именя таблицы тэгов и ссылочную таблицу items_tags
        const val TABLE_TAGS = "tags"
        const val TABLE_ITEMS_TAGS = "items_tags"
        const val KEY_TAG = "tag"
        const val KEY_ITEM_ID = "item_id"
        const val KEY_TAG_ID = "tag_id"

        //Объявляем константы для базы данных
        const val DATABASE_VERSION: Int = 2
        const val DATABASE_NAME: String = "itemsDb"
    }

}