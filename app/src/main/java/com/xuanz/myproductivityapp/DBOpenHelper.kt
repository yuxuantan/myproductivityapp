package com.xuanz.myproductivityapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBOpenHelper(context: Context,
                   factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, DATABASE_NAME,
            factory, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val CREATE_TODO_TABLE = ("CREATE TABLE " +
                    TODO_TABLE_NAME + "("
                    + TODO_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    TODO_COLUMN_NAME
                    + " TEXT,"+
                    TODO_COLUMN_ISCOMPLETED
                    + " TEXT,"+
                    TODO_COLUMN_POS
                    + " TEXT"
                    + ")")
            db.execSQL(CREATE_TODO_TABLE)

            val CREATE_JOURNAL_TABLE = ("CREATE TABLE " +
                    JOURNAL_TABLE_NAME + "("
                    + JOURNAL_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    JOURNAL_COLUMN_TITLE
                    + " TEXT,"+
                    JOURNAL_COLUMN_BODY
                    + " TEXT,"+
                    JOURNAL_COLUMN_DATE
                    + " TEXT"
                    + ")")
            db.execSQL(CREATE_JOURNAL_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE_NAME)
            onCreate(db)
        }

        fun addToDoItem(name:String, isCompleted:Boolean, pos: Int):Int {
            val values = ContentValues()
            values.put(TODO_COLUMN_NAME, name)
            values.put(TODO_COLUMN_ISCOMPLETED, isCompleted) // 0 false, 1 true
            values.put(TODO_COLUMN_POS, pos)
            val db = this.writableDatabase
            db.insert(TODO_TABLE_NAME, null, values)

            val cursor = this.readableDatabase.rawQuery("SELECT last_insert_rowid()", null)
            var id = 0
            if (cursor!=null && cursor!!.moveToFirst()){
                id = cursor.getInt(0)
            }

            db.close()
            return id
        }

        fun deleteToDoItem(id: Int): Boolean {
            val db = this.writableDatabase
            return db.delete(TODO_TABLE_NAME, TODO_COLUMN_ID +"="+id, null)>0

            db.close()
        }
        // UPDATE DB POS FIELD BETWEEN STARTPOS AND ENDPOS with order in orderlist`
        fun updateToDoItem(name: String, isCompleted: Boolean, pos:Int, id: Int): Boolean {
            val values = ContentValues()
            values.put(TODO_COLUMN_NAME, name)
            values.put(TODO_COLUMN_ISCOMPLETED, isCompleted)
            values.put(TODO_COLUMN_POS, pos)
            val db = this.writableDatabase
            return db.update(TODO_TABLE_NAME, values, "_id="+id, null)>0

            db.close()
        }
        fun getAllToDo(): Cursor? {
            val db = this.readableDatabase
            return db.rawQuery("SELECT * FROM $TODO_TABLE_NAME ORDER BY $TODO_COLUMN_POS ASC", null)
        }

    ////////////////////
        fun getAllJournal(): Cursor? {
            val db = this.readableDatabase
            return db.rawQuery("SELECT * FROM $JOURNAL_TABLE_NAME", null)
        }

        fun addJournalItem(title:String, body:String, date:String):Int {
            val values = ContentValues()
            values.put(JOURNAL_COLUMN_TITLE, title)
            values.put(JOURNAL_COLUMN_BODY, body) // 0 false, 1 true
            values.put(JOURNAL_COLUMN_DATE, date)

            val db = this.writableDatabase
            db.insert(JOURNAL_TABLE_NAME, null, values)

            val cursor = this.readableDatabase.rawQuery("SELECT last_insert_rowid()", null)
            var id = 0
            if (cursor!=null && cursor!!.moveToFirst()){
                id = cursor.getInt(0)
            }

            db.close()
            return id
        }
        companion object {
            private val DATABASE_VERSION = 1
            private val DATABASE_NAME = "ProductiveAppListItems.db"
            val TODO_TABLE_NAME = "todoitems"
            val TODO_COLUMN_ID = "_id"
            val TODO_COLUMN_NAME = "name"
            val TODO_COLUMN_ISCOMPLETED = "isCompleted"
            val TODO_COLUMN_POS = "pos"


            val JOURNAL_TABLE_NAME = "journalItems"
            val JOURNAL_COLUMN_ID = "_id"
            val JOURNAL_COLUMN_TITLE = "title"
            val JOURNAL_COLUMN_BODY = "body"
            val JOURNAL_COLUMN_DATE = "date"
        }



}