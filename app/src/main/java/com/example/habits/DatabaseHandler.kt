package com.example.habits

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "EntryDatabase"
        private val TABLE_CONTACTS = "EntryTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }


    //method to insert data
    fun addEntry(dte: DateModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, dte.dte)
        var success: Long
        try {
            // Inserting Row
            success = db.insertOrThrow(TABLE_CONTACTS, null, contentValues)
        }
        catch (e: SQLiteException) {
            success = -1
        }
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    fun viewEntry():List<DateModelClass>{
        val empList:ArrayList<DateModelClass> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        val cursor: Cursor
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Int
        if (cursor.moveToFirst()) {
            do {
                try {
                    userId = cursor.getInt(cursor.getColumnIndex("id"))
                }
                catch (e: Exception) {
                    userId = 0
                }
                val dte= DateModelClass(dte = userId)
                empList.add(dte)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return empList
    }
    //method to update data
    fun updateEntry(dte: DateModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, dte.dte)


        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"id="+dte.dte, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
    fun deleteEntry(dte: DateModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, dte.dte) // DateModelClass date
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS,"id="+dte.dte,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}