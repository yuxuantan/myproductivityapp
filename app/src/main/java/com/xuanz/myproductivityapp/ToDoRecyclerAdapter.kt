package com.xuanz.myproductivityapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.view.*
import java.util.*


class ToDoRecyclerAdapter (var context:Context, private var mToDoList: ArrayList<ToDoListItem>, private val startDragListener: OnStartDragListener) :
    RecyclerView.Adapter<ToDoRecyclerAdapter.ItemViewHolder>(),
    ItemMoveCallbackListener.Listener
{

    fun updateData(itemToDo: ToDoListItem): Boolean{
        val dbHandler = DBOpenHelper(context, null)
        var updated = dbHandler.updateToDoItem(itemToDo.name, itemToDo.isCompleted, itemToDo.pos, itemToDo.id)
        return updated
    }

    fun deleteFromDB(id:Int):Boolean{
        val dbHandler = DBOpenHelper(context, null)
        var deleted = dbHandler.deleteToDoItem(id)
        return deleted
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mToDoList.size
    }
    // Involves populating data into the todo_item through holder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Get the data model based on position
        val itemToDo: ToDoListItem = mToDoList.get(position)
        // populate Values
        holder.bind(itemToDo, position)
        ////
        holder.itemView.swapIcon.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                this.startDragListener.onStartDrag(holder)
            }
            return@setOnTouchListener true
        }
    }
    // Provide a direct reference to each of the views within a data todo_item
    // Used to cache the views within the todo_item layout for fast access

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)
        return ItemViewHolder(itemView)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(itemToDo: ToDoListItem, position: Int) {
            // Set todo_item views based on your views and data model
//            itemView.editIcon.setOnClickListener {
//                val et = EditText(context)
//                et.setText(todo_item.name)
//                val builder = androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogTheme)
//                builder.setTitle("Edit ID#" +  todo_item.id)
//                builder.setMessage("Click save once task has been edited!")
//                builder.setView(et)
//
//                builder.setPositiveButton("SAVE"){ dialog, which ->
//                    var updated = updateData(ListItem(et.text.toString(), todo_item.isCompleted, todo_item.pos, todo_item.id))
//                    if (updated){
//                        todo_item.name = et.text.toString()
//                        Toast.makeText(context,
//                            "SAVED!", Toast.LENGTH_SHORT).show()
//                        notifyItemChanged(todo_item.pos)
//                    }
//
//
//                }
//                builder.setNegativeButton("Cancel") { dialog, which ->
//                    // DO nothing
//                }
//                et.isFocused
//                builder.show()
//            }


            itemView.item_name.text = itemToDo.name
            if(itemToDo.isCompleted){
                itemView.item_name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.item_name.setTextColor(Color.GRAY)
            }
            //Strike through and complete on click
            itemView.item_name.setOnClickListener {
                var updated = updateData(ToDoListItem(itemToDo.name, !itemToDo.isCompleted, itemToDo.pos, itemToDo.id))
                if (updated){
                    itemToDo.isCompleted = !itemToDo.isCompleted
                    if (itemToDo.isCompleted) {
                        onRowMoved(itemToDo.pos, mToDoList.size-1)
                        //Aesthetics
                        itemView.item_name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        itemView.item_name.setTextColor(Color.GRAY)
                    }
                    else{
                        onRowMoved(itemToDo.pos, 0)
                        itemView.item_name.paintFlags = 0
                        itemView.item_name.setTextColor(Color.BLACK)

                    }
                }
                else{
                    Toast.makeText(context, "DB NOT UPDATED", Toast.LENGTH_SHORT).show()
                }


            }
            //edit dialog popup on longclick
            itemView.item_name.setOnLongClickListener {
                Toast.makeText(context, "Long click detected", Toast.LENGTH_SHORT).show()
                val et = EditText(context)
                et.setText(itemToDo.name)
                val builder = androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogTheme)
                builder.setTitle("Edit ID#" +  itemToDo.id)
                builder.setMessage("Click save once task has been edited!")
                builder.setView(et)
                //BUILDER 2
                val builder2 = androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogTheme)
                builder2.setMessage("Are you sure you want to delete \"" +  itemToDo.name + "\"?")

                ///

                builder.setPositiveButton("SAVE"){ dialog, which ->
                    var updated = updateData(ToDoListItem(et.text.toString(), itemToDo.isCompleted, itemToDo.pos, itemToDo.id))
                    if (updated){
                        itemToDo.name = et.text.toString()
                        Toast.makeText(context,
                            "SAVED!", Toast.LENGTH_SHORT).show()
                        notifyItemChanged(itemToDo.pos)
                    }


                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    // DO nothing
                }
                builder.setNeutralButton("Delete"){ dialog, which ->
                    builder2.setPositiveButton("Yes"){dialog, which->
                        var deleted = deleteFromDB(itemToDo.id)
                        if (deleted){
                            mToDoList.remove(itemToDo)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, getItemCount()-position)
                            Toast.makeText(context, "Item: "+itemToDo.name+" deleted successfully", Toast.LENGTH_LONG ).show()
                        }
                        else{
                            Toast.makeText(context, "Item: "+itemToDo.name+" NOT deleted", Toast.LENGTH_LONG ).show()
                        }
                    }
                    builder2.setNegativeButton("No"){dialog, which->

                    }
                    builder2.show()


                }
                et.isFocused
                builder.show()
                return@setOnLongClickListener true
            }
//            itemView.delete_btn.setOnClickListener{
//
//                var deleted = deleteFromDB(todo_item.id)
//                if (deleted){
//                    mList.remove(todo_item)
//                    notifyItemRemoved(position)
//                    notifyItemRangeChanged(position, getItemCount()-position)
//                    Toast.makeText(context, "Item: "+todo_item.name+" deleted successfully", Toast.LENGTH_LONG ).show()
//                }
//                else{
//                    Toast.makeText(context, "Item: "+todo_item.name+" NOT deleted", Toast.LENGTH_LONG ).show()
//                }
//            }

//            itemView.checkBox.setOnCheckedChangeListener(null)
//            itemView.checkBox.setChecked(todo_item.isCompleted)
//            itemView.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
//                var updated = updateData(ListItem(todo_item.name, !todo_item.isCompleted, todo_item.pos, todo_item.id))
//                if (updated){
//                    todo_item.isCompleted = !todo_item.isCompleted
//                    if (todo_item.isCompleted) {
//                        onRowMoved(todo_item.pos, mList.size-1)
//                    }
//                    else{
//                        onRowMoved(todo_item.pos, 0)
//                    }
//                }
//                else{
//                    Toast.makeText(context, "DB NOT UPDATED", Toast.LENGTH_SHORT).show()
//                }
//            })
        }
    }


    override fun onRowMoved(fromPosition: Int, toPosition: Int) {

        println("from: "+fromPosition+" to: "+toPosition)

        mToDoList[fromPosition].pos = toPosition
        updateData(mToDoList[fromPosition])
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                //update todo_item and db
                mToDoList[i+1].pos = i
                updateData(mToDoList[i+1])
            }
            for (i in fromPosition until toPosition) {
                // UPdate list position
                Collections.swap(mToDoList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                mToDoList[i-1].pos = i
                updateData(mToDoList[i-1])
            }
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mToDoList, i, i - 1)
            }
        }
        //update ui
        notifyItemMoved(fromPosition, toPosition)
    }
    override fun onRowSelected(itemViewHolder: ItemViewHolder) {
        itemViewHolder.itemView.item_name.setTextColor(Color.LTGRAY)
    }
    override fun onRowClear(itemViewHolder: ItemViewHolder) {
        itemViewHolder.itemView.item_name.setTextColor(Color.BLACK)
    }


}