package com.example.localdatabase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

// Menggunakan DatabaseContract secara langsung untuk konstanta tabel
internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "dbhomework"
        private const val DATABASE_VERSION = 1

        // Membuat query SQL untuk membuat tabel homework
        private val SQL_CREATE_TABLE_NOTE = "CREATE TABLE ${DatabaseContract.HomeworkColumns.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${DatabaseContract.HomeworkColumns.TITLE} TEXT NOT NULL, " +
                "${DatabaseContract.HomeworkColumns.DESCRIPTION} TEXT NOT NULL, " +
                "${DatabaseContract.HomeworkColumns.DATE} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)  // Menjalankan perintah untuk membuat tabel
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.HomeworkColumns.TABLE_NAME}")
        onCreate(db)  // Membuat ulang tabel saat versi database diperbarui
    }
}
