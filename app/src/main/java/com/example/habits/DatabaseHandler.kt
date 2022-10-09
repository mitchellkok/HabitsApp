package com.example.habits

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "EntryDatabase"
        private val TABLE_NAME = "EntryTable"
        private val KEY_ID = "id"
        private val KEY_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE "
                + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_STATUS + " INTEGER"
//                + KEY_EMAIL + " TEXT" +
                + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    //method to insert data
    fun addEntry(dte: DateModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, dte.date)
        contentValues.put(KEY_STATUS, dte.status)
        val success: Long = try {
            db.insertOrThrow(TABLE_NAME, null, contentValues) // Inserting Row
        } catch (e: SQLiteException) {
            -1
        }
        db.close() // Closing database connection
        return success
    }
    //method to read data
    @SuppressLint("Range")
    fun viewEntry():List<DateModelClass>{
        val dteList:ArrayList<DateModelClass> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var storedDate: Int
        var storedStatus: Int
        if (cursor.moveToFirst()) {
            do {
                try {
                    storedDate = cursor.getInt(cursor.getColumnIndex("id"))
                    storedStatus = cursor.getInt(cursor.getColumnIndex("status"))
                }
                catch (e: Exception) {
                    storedDate = -1
                    storedStatus = -1
                }
                val dte= DateModelClass(date = storedDate, status = storedStatus)
                dteList.add(dte)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dteList
    }

    //method to delete data
    fun deleteEntry(dte: DateModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, dte.date) // DateModelClass date
        // Deleting Row
        val success = db.delete(TABLE_NAME,"id="+dte.date,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
        db.close()
    }
}