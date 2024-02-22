@file:Suppress("DEPRECATION")

package com.onedeveloper.think.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.onedeveloper.think.R
import com.onedeveloper.think.adapter.NoteAdapter
import com.onedeveloper.think.constants.Requests
import com.onedeveloper.think.databinding.ActivityMainBinding
import com.onedeveloper.think.model.Note
import com.onedeveloper.think.utilities.SwipeToDeleteCallback
import com.onedeveloper.think.viewModel.NoteViewModel
import com.onedeveloper.think.viewModel.ViewModelFactory


class MainActivity : AppCompatActivity() {
    private var noteViewModel: NoteViewModel? = null
    private var completeList: List<Note?>? = null
    var recyclerView: RecyclerView? = null
    var adapter: NoteAdapter? = null
    var builder: MaterialAlertDialogBuilder? = null
    var binding:ActivityMainBinding ? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        binding?.buttonAddNote?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivityForResult(intent, Requests.ADD_NOTE.requestCode)
        }
        recyclerView = binding?.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)

        builder = MaterialAlertDialogBuilder(
            ContextThemeWrapper(this, R.style.AlertDialogCustom)
        )
        noteViewModel = ViewModelProvider(this, ViewModelFactory(this.application))[NoteViewModel::class.java]

        noteViewModel?.getAllNotes()?.observe(this
        ) { notes ->
            adapter = NoteAdapter(notes)
            recyclerView?.adapter = adapter
            if (notes.isNullOrEmpty().not()) {
                completeList?.toMutableList()?.addAll(ArrayList(notes))
            }
            adapter!!.setOnItemClickListener(object : NoteAdapter.onItemClickListener {
                override fun onItemClick(note: Note?) {
                    val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
                    val title = note?.title
                    val description = note?.description
                    val priority = note?.priority
                    val date = note?.date
                    val time = note?.time
//                val color = note?.color
                    var priorityNumber = 0
                    if (priority == "High") {
                        priorityNumber = 3
                    } else if (priority == "Medium") {
                        priorityNumber = 2
                    } else if (priority == "Low") {
                        priorityNumber = 1
                    }
                    val id = note?.id
                    intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, title)
                    intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, description)
                    intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, priority)
                    intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY_NUMBER, priorityNumber)
                    intent.putExtra(AddEditNoteActivity.EXTRA_ID, id)
                    intent.putExtra(AddEditNoteActivity.EXTRA_DATE, date)
                    intent.putExtra(AddEditNoteActivity.EXTRA_TIME, time)
//                intent.putExtra(AddEditNoteActivity.EXTRA_COLOR,color)
                    startActivityForResult(intent, Requests.EDIT_NOTE.requestCode)
                }
            })

        }

        binding?.searchBar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(query.isEmpty().not()){
                    binding?.searchText?.visibility = View.GONE
                }
                else{
                    binding?.searchText?.visibility = View.VISIBLE
                }
                getNotesFromDb(query)
                return false
            }

            private fun getNotesFromDb(searchText: String) {
                var searchText = searchText
                searchText = "%$searchText%"
                noteViewModel!!.getSearchedNotes(searchText)!!
                    .observe(this@MainActivity, object : Observer<List<Note?>?> {
                        override fun onChanged(value: List<Note?>?) {
                            if (value == null) {
                                return
                            }
                            val adapter = NoteAdapter(value)
                            completeList = value
                            recyclerView?.adapter = adapter
                        }
                    })
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.isEmpty().not()){
                    binding?.searchText?.visibility = View.GONE
                }
                else{
                    binding?.searchText?.visibility = View.VISIBLE
                }
                getNotesFromDb(newText)
                return false
            }
        })

        ItemTouchHelper(object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                builder!!.background = resources.getDrawable(R.drawable.alert_shape,theme)
                builder!!.setMessage("Do you want to delete this note ?")
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        val adapterPosition = viewHolder.adapterPosition
                        val mNote = adapter!!.getNoteAt(adapterPosition)
                        val snackbar = Snackbar
                            .make(recyclerView!!, "Note Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                noteViewModel!!.insert(mNote)
                            }
                        snackbar.setActionTextColor(resources.getColor(R.color.primaryLightColor))
                        val snackBarView = snackbar.view
                        val snackbarTextId = com.google.android.material.R.id.snackbar_text
                        val textView = snackBarView.findViewById<TextView>(snackbarTextId)
                        textView.setTextColor(resources.getColor(R.color.primaryTextColor))
                        snackBarView.background = resources.getDrawable(R.drawable.snackbar_shape)
                        snackbar.show()
                        noteViewModel!!.delete(adapter!!.getNoteAt(viewHolder.adapterPosition))
                        dialog.cancel()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        adapter!!.notifyDataSetChanged()
                        dialog.cancel()
                    }
                val alert = builder!!.create()
                alert.show()
            }
        }).attachToRecyclerView(recyclerView)

        binding?.toolbar?.setOnMenuItemClickListener{
            if(it.itemId == R.id.delete_all_notes){
                noteViewModel!!.deleteAllNotes()
                Toast.makeText(this@MainActivity, "All Notes Deleted!", Toast.LENGTH_SHORT).show()
            }
            false
        }


        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Requests.ADD_NOTE.requestCode && resultCode == RESULT_OK) {
            val title = data!!.getStringExtra(AddEditNoteActivity.EXTRA_TITLE)
            val description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION)
            val priority = data.getStringExtra(AddEditNoteActivity.EXTRA_PRIORITY)
            val date = data.getStringExtra(AddEditNoteActivity.EXTRA_DATE)
            val time = data.getStringExtra(AddEditNoteActivity.EXTRA_TIME)
//            val color = data.getStringExtra(AddEditNoteActivity.EXTRA_COLOR)
            var priorityNumber = 0
            if (priority == "High") {
                priorityNumber = 3
            } else if (priority == "Medium") {
                priorityNumber = 2
            } else if (priority == "Low") {
                priorityNumber = 1
            }
            val note = Note(title, description, priority, priorityNumber, date, time)
            noteViewModel!!.insert(note)
            Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_SHORT).show()
        } else if (requestCode == Requests.EDIT_NOTE.requestCode && resultCode == RESULT_OK) {
            val id = data!!.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1)
            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show()
                return
            }
            val title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE)
            val description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION)
            val priority = data.getStringExtra(AddEditNoteActivity.EXTRA_PRIORITY)
            val date = data.getStringExtra(AddEditNoteActivity.EXTRA_DATE)
            val time = data.getStringExtra(AddEditNoteActivity.EXTRA_TIME)
            var priorityNumber = 0
            if (priority == "High") {
                priorityNumber = 3
            } else if (priority == "Medium") {
                priorityNumber = 2
            } else if (priority == "Low") {
                priorityNumber = 1
            }
            val note = Note(title, description, priority, priorityNumber, date, time)
            note.id = id
            noteViewModel!!.update(note)
            Toast.makeText(this, "Note updated Successfully", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_notes -> {
                noteViewModel!!.deleteAllNotes()
                Toast.makeText(this, "All Notes Deleted!", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}