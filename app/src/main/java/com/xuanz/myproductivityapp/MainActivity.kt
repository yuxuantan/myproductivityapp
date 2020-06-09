package com.xuanz.myproductivityapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.DragStartHelper
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity(), OnStartDragListener  {
    lateinit var toDoItems: ArrayList<ListItem>
    lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: ")


        // Create adapter passing in the sample user data
        toDoItems = ArrayList()
        loadDataFromDB()
        val adapter = DragDropRecyclerAdapter(this, toDoItems, this)

        val callback: ItemTouchHelper.Callback = ItemMoveCallbackListener(adapter)
        // Lookup the recyclerview in activity layout
        val rvToDoItems = findViewById<View>(R.id.toDoItems) as RecyclerView
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvToDoItems)
        // Set layout manager to position the items
        rvToDoItems.layoutManager = LinearLayoutManager(this)
        // Attach the adapter to the recyclerview to populate items
        rvToDoItems.adapter = adapter

        addItemBtn.setOnClickListener {
            // add pos of all items by 1 and reorder
            for (item in toDoItems){
                item.pos +=1
                updatePosDB(item)
            }
            var id = addToDB(addItemField.text.toString(), false, 0)
            toDoItems.add(0, ListItem(addItemField.text.toString(), false, 0, id))
            adapter.notifyItemInserted(0)
            addItemField.text.clear()

            toDoItems.forEach{System.out.println(it.name+" "+ it.pos)}
        }
    }

    fun loadDataFromDB(){
        val dbHandler = DBOpenHelper(this, null)
        val cursor = dbHandler.getAllName()
        var thisName = ""
        var thisIsCompleted = true
        var id = 0
        var pos = 0
        if (cursor!=null && cursor!!.moveToFirst()) {
            thisName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_NAME))
            thisIsCompleted = false
            id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID))

            if (cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_ISCOMPLETED)) == "1") {
                thisIsCompleted = true
            }
            pos = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_POS))

            toDoItems.add(ListItem(thisName, thisIsCompleted, pos, id))
            while (cursor.moveToNext()) {
                thisName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_NAME))
                if (cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_ISCOMPLETED)) == "1") {
                    thisIsCompleted = true
                } else {
                    thisIsCompleted = false
                }
                id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID))
                pos = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_POS))
                toDoItems.add(ListItem(thisName, thisIsCompleted, pos, id))
            }
            cursor.close()
        }
    }

    fun addToDB(name: String, isCompleted: Boolean, pos:Int):Int{
        val dbHandler = DBOpenHelper(this, null)
        var id = dbHandler.addItem(name, isCompleted, pos)
        Toast.makeText(this, name.toString()+id + "Added to database", Toast.LENGTH_LONG).show()
        return id
    }

    fun updatePosDB(item :ListItem){
        val dbHandler = DBOpenHelper(this, null)
        var id = dbHandler.updateItem(item.name, item.isCompleted, item.pos, item.id)
        println("UPDATING POS "+ item.pos)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }
}