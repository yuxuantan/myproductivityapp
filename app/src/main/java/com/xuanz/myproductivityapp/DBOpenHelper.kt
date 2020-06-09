package com.xuanz.myproductivityapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

class DBOpenHelper(context: Context,
                   factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, DATABASE_NAME,
            factory, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                    TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME
                    + " TEXT,"+
                    COLUMN_ISCOMPLETED
                    + " TEXT,"+
                    COLUMN_POS
                    + " TEXT"
                    + ")")
            db.execSQL(CREATE_PRODUCTS_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
            onCreate(db)
        }

        fun addItem(name:String, isCompleted:Boolean, pos: Int):Int {
            val values = ContentValues()
            values.put(COLUMN_NAME, name)
            values.put(COLUMN_ISCOMPLETED, isCompleted) // 0 false, 1 true
            values.put(COLUMN_POS, pos)
            val db = this.writableDatabase
            db.insert(TABLE_NAME, null, values)

            val cursor = this.readableDatabase.rawQuery("SELECT last_insert_rowid()", null)
            var id = 0
            if (cursor!=null && cursor!!.moveToFirst()){
                id = cursor.getInt(0)
            }

            db.close()
            return id
        }

        fun deleteItem(id: Int): Boolean {
            val db = this.writableDatabase
            return db.delete(TABLE_NAME, COLUMN_ID +"="+id, null)>0

            db.close()
        }
        // UPDATE DB POS FIELD BETWEEN STARTPOS AND ENDPOS with order in orderlist`
        fun updateItem(name: String, isCompleted: Boolean, pos:Int, id: Int): Boolean {
            val values = ContentValues()
            values.put(COLUMN_NAME, name)
            values.put(COLUMN_ISCOMPLETED, isCompleted)
            values.put(COLUMN_POS, pos)
            val db = this.writableDatabase
            return db.update(TABLE_NAME, values, "_id="+id, null)>0

            db.close()
        }
        fun getAllName(): Cursor? {
            val db = this.readableDatabase
            return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_POS ASC", null)
        }
        companion object {
            private val DATABASE_VERSION = 1
            private val DATABASE_NAME = "ProductiveAppListItems.db"
            val TABLE_NAME = "listitems"
            val COLUMN_ID = "_id"
            val COLUMN_NAME = "name"
            val COLUMN_ISCOMPLETED = "isCompleted"
            val COLUMN_POS = "pos"
        }

}