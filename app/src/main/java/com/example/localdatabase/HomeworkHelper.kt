package com.example.localdatabase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class HomeworkHelper(context: Context) {
    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = DatabaseContract.HomeworkColumns.TABLE_NAME
        private var INSTANCE: HomeworkHelper? = null

        fun getInstance(context: Context): HomeworkHelper? {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = HomeworkHelper(context)
                }
            }
            return INSTANCE
        }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()
        if (database.isOpen) {
            database.close()
        }
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "${BaseColumns._ID} ASC",
            null
        )
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        return database.update(
            DATABASE_TABLE,
            values,
            "${BaseColumns._ID} = ?",
            arrayOf(id)
        )
    }

    fun deleteById(id: String): Int {
        return database.delete(
            DATABASE_TABLE,
            "${BaseColumns._ID} = '$id'",
            null
        )
    }
}
