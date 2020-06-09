package com.xuanz.myproductivityapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.DragStartHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item.view.*
import java.util.*
import kotlin.collections.ArrayList


class DragDropRecyclerAdapter (var context:Context, private var mList: ArrayList<ListItem>, private val startDragListener: OnStartDragListener) :
    RecyclerView.Adapter<DragDropRecyclerAdapter.ItemViewHolder>(),
    ItemMoveCallbackListener.Listener
{

    fun updateData(item: ListItem): Boolean{
        val dbHandler = DBOpenHelper(context, null)
        var updated = dbHandler.updateItem(item.name, item.isCompleted, item.pos, item.id)
        return updated
    }

    fun deleteFromDB(id:Int):Boolean{
        val dbHandler = DBOpenHelper(context, null)
        var deleted = dbHandler.deleteItem(id)
        return deleted
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mList.size
    }
    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Get the data model based on position
        val item: ListItem = mList.get(position)
        // populate Values
        holder.bind(item, position)
        ////
        holder.itemView.swapIcon.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                this.startDragListener.onStartDrag(holder)
            }
            return@setOnTouchListener true
        }
    }
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ItemViewHolder(itemView)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ListItem, position: Int) {
            // Set item views based on your views and data model
            itemView.item_name.text = item.name

            itemView.delete_btn.setOnClickListener{

                var deleted = deleteFromDB(item.id)
                if (deleted){
                    mList.remove(item)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, getItemCount()-position)
                    Toast.makeText(context, "Item: "+item.name+" deleted successfully", Toast.LENGTH_LONG ).show()
                }
                else{
                    Toast.makeText(context, "Item: "+item.name+" NOT deleted", Toast.LENGTH_LONG ).show()
                }
            }

            itemView.checkBox.setOnCheckedChangeListener(null)
            itemView.checkBox.setChecked(item.isCompleted)
            itemView.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                var updated = updateData(ListItem(item.name, !item.isCompleted, item.pos, item.id))
                if (updated){
                    item.isCompleted = !item.isCompleted
                    if (item.isCompleted) {
                        onRowMoved(item.pos, mList.size-1)
                    }
                    else{
                        onRowMoved(item.pos, 0)
                    }
                }
                else{
                    Toast.makeText(context, "DB NOT UPDATED", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    override fun onRowMoved(fromPosition: Int, toPosition: Int) {

        println("from: "+fromPosition+" to: "+toPosition)

        mList[fromPosition].pos = toPosition
        updateData(mList[fromPosition])
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                //update item and db
                mList[i+1].pos = i
                updateData(mList[i+1])
            }
            for (i in fromPosition until toPosition) {
                // UPdate list position
                Collections.swap(mList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                mList[i-1].pos = i
                updateData(mList[i-1])
            }
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mList, i, i - 1)
            }
        }
        //update ui
        notifyItemMoved(fromPosition, toPosition)
    }
    override fun onRowSelected(itemViewHolder: ItemViewHolder) {
//        itemViewHolder.itemView.item_name.setTextColor(Color.GRAY)
    }
    override fun onRowClear(itemViewHolder: ItemViewHolder) {
    }


}