package com.xuanz.myproductivityapp

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.journal_item.view.*
import java.util.*


class JournalRecyclerAdapter (var context:Context, private var mJournalList: ArrayList<JournalItem>) :
    RecyclerView.Adapter<JournalRecyclerAdapter.ItemViewHolder>()
{


    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mJournalList.size
    }
    // Involves populating data into the todo_item through holder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Get the data model based on position
        val journalItem: JournalItem = mJournalList.get(position)
        // populate Values
        holder.bind(journalItem, position)

    }
    // Provide a direct reference to each of the views within a data todo_item
    // Used to cache the views within the todo_item layout for fast access

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.journal_item, parent, false)
        return ItemViewHolder(itemView)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(journalItem: JournalItem, position: Int) {


            itemView.journal_title.text = journalItem.title

            //Strike through and complete on click
            itemView.journal_title.setOnClickListener {
                // open new fragment
                val myFragment: Fragment = EditJournalFragment()
                itemView.getContext().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, myFragment).addToBackStack(null).commit()


            }

        }
    }

}