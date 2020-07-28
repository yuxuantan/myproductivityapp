package com.xuanz.myproductivityapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var journalItems: ArrayList<JournalItem>

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
        val rootView = inflater.inflate(R.layout.fragment_second, container, false)

        journalItems = ArrayList()
        loadDataFromDB()

        val adapter = JournalRecyclerAdapter(requireActivity(), journalItems)

        val rvJournalItems = rootView.findViewById<View>(R.id.journalItems) as RecyclerView
        rvJournalItems.layoutManager = LinearLayoutManager(context)
        rvJournalItems.adapter = adapter
        rootView.findViewById<Button>(R.id.addJournalBtn).setOnClickListener{
            Toast.makeText(context, "add journal popup", Toast.LENGTH_SHORT).show()
            val et = EditText(context)
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            builder.setTitle("Add new entry")
            builder.setMessage("Click save once task has been edited!")
            builder.setView(et)

            ///

            builder.setPositiveButton("SAVE"){ dialog, which ->
                var id = addToDB(et.text.toString(), "BODY", "BLANK DATE")
                if (id!=null){
                    Toast.makeText(context,
                        et.text.toString()+"SAVED!", Toast.LENGTH_SHORT).show()
                    journalItems.add(JournalItem(et.text.toString(), "BODY", "BLANK DATE", id))
                    adapter.notifyItemInserted(journalItems.size-1)
                }


            }


            builder.show()

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
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun loadDataFromDB(){
        val dbHandler = DBOpenHelper(requireActivity(), null)
        val cursor = dbHandler.getAllJournal()

        var thisTitle = ""
        var thisBody = ""
        var thisId = 0
        var thisDate = ""
        if (cursor!=null && cursor!!.moveToFirst()) {
            thisTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_TITLE))
            thisBody = cursor.getString(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_BODY))
            thisDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_DATE))
            thisId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_ID))

            journalItems.add(JournalItem(thisTitle, thisBody, thisDate, thisId))

            while (cursor.moveToNext()) {
                thisTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_TITLE))
                thisBody = cursor.getString(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_BODY))
                thisDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_DATE))
                thisId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.JOURNAL_COLUMN_ID))

                journalItems.add(JournalItem(thisTitle, thisBody, thisDate, thisId))
            }
            cursor.close()
        }
    }
    fun addToDB(title: String, body:String, date:String):Int{
        val dbHandler = DBOpenHelper(requireActivity(), null)
        var id = dbHandler.addJournalItem(title, body, date)
        Toast.makeText(context, title.toString()+id + "Added to database", Toast.LENGTH_LONG).show()
        return id
    }
}