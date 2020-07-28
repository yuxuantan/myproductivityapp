package com.xuanz.myproductivityapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment(), OnStartDragListener  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    lateinit var toDoItems: ArrayList<ToDoListItem>
    lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_first, container, false)

        // Create adapter passing in the sample user data
        toDoItems = ArrayList()
        loadDataFromDB()
        val adapter = ToDoRecyclerAdapter(requireActivity(), toDoItems, this)

        val callback: ItemTouchHelper.Callback = ItemMoveCallbackListener(adapter)
        // Lookup the recyclerview in activity layout
        val rvToDoItems = rootView.findViewById<View>(R.id.toDoItems) as RecyclerView
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvToDoItems)
        // Set layout manager to position the items
        rvToDoItems.layoutManager = LinearLayoutManager(context)
        // Attach the adapter to the recyclerview to populate items
        rvToDoItems.adapter = adapter

        rootView.findViewById<Button>(R.id.addItemBtn).setOnClickListener {
            // add pos of all items by 1 and reorder
            for (item in toDoItems){
                item.pos +=1
                updatePosDB(item)
            }

            var addItemField = rootView.findViewById<TextView>(R.id.addItemField)
            var id = addToDB(addItemField.text.toString(), false, 0)
            toDoItems.add(0, ToDoListItem(addItemField.text.toString(), false, 0, id))
            adapter.notifyItemInserted(0)
            addItemField.setText("")

            toDoItems.forEach{System.out.println(it.name+" "+ it.pos)}
        }



        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun loadDataFromDB(){
        val dbHandler = DBOpenHelper(requireActivity(), null)
        val cursor = dbHandler.getAllToDo()
        var thisName = ""
        var thisIsCompleted = true
        var id = 0
        var pos = 0
        if (cursor!=null && cursor!!.moveToFirst()) {
            thisName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_NAME))
            thisIsCompleted = false
            id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_ID))

            if (cursor.getString(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_ISCOMPLETED)) == "1") {
                thisIsCompleted = true
            }
            pos = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_POS))

            toDoItems.add(ToDoListItem(thisName, thisIsCompleted, pos, id))
            while (cursor.moveToNext()) {
                thisName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_NAME))
                if (cursor.getString(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_ISCOMPLETED)) == "1") {
                    thisIsCompleted = true
                } else {
                    thisIsCompleted = false
                }
                id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_ID))
                pos = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TODO_COLUMN_POS))
                toDoItems.add(ToDoListItem(thisName, thisIsCompleted, pos, id))
            }
            cursor.close()
        }
    }

    fun addToDB(name: String, isCompleted: Boolean, pos:Int):Int{
        val dbHandler = DBOpenHelper(requireActivity(), null)
        var id = dbHandler.addToDoItem(name, isCompleted, pos)
        Toast.makeText(context, name.toString()+id + "Added to database", Toast.LENGTH_LONG).show()
        return id
    }

    fun updatePosDB(itemToDo :ToDoListItem){
        val dbHandler = DBOpenHelper(requireActivity(), null)
        var id = dbHandler.updateToDoItem(itemToDo.name, itemToDo.isCompleted, itemToDo.pos, itemToDo.id)
        println("UPDATING POS "+ itemToDo.pos)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }
}