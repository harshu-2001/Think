package com.onedeveloper.think.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.onedeveloper.think.R
import com.onedeveloper.think.adapter.NoteAdapter
import com.onedeveloper.think.adapter.NoteAdapter.onItemClickListener
import com.onedeveloper.think.model.Note
import com.onedeveloper.think.utilities.SwipeToDeleteCallback
import com.onedeveloper.think.viewModel.NoteViewModel
import com.onedeveloper.think.viewModel.ViewModelFactory


class MainActivity : AppCompatActivity() {
    private var noteViewModel: NoteViewModel? = null
    private var completeList: List<Note?>? = null
    var recyclerView: RecyclerView? = null
    var adapter: NoteAdapter? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var isDarkModeOn = false
    var builder: MaterialAlertDialogBuilder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonAddNote = findViewById<FloatingActionButton>(R.id.button_add_note)
        buttonAddNote.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivityForResult(intent, Add_Note_Request)
        }
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        adapter = NoteAdapter()
        recyclerView?.adapter = adapter
        builder = MaterialAlertDialogBuilder(
            ContextThemeWrapper(this, R.style.AlertDialogCustom)
        )
//        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        noteViewModel = ViewModelProvider(this, ViewModelFactory(this.application))[NoteViewModel::class.java]


        noteViewModel?.getAllNotes()?.observe(this,
            Observer { notes ->
                if(notes?.isEmpty()?.not() == true){
                    adapter!!.submitList(notes)
                    completeList?.toMutableList()?.addAll(ArrayList(notes))
                }
            })
        ItemTouchHelper(object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                builder!!.background = resources.getDrawable(R.drawable.alert_shape)
                builder!!.setMessage("Do you want to delete this note ?")
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        val adapterPosition = viewHolder.adapterPosition
                        val mNote = adapter!!.getNoteAt(adapterPosition)
                        val viewPos = findViewById<View>(R.id.myCoordinatorLayout)
                        val snackbar = Snackbar
                            .make(recyclerView!!, "Note Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                val mAdapterPosition = viewHolder.adapterPosition
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
        adapter!!.setOnItemClickListener(object : onItemClickListener {
            override fun onItemClick(note: Note?) {
                val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
                val title = note?.title
                val description = note?.description
                val priority = note?.priority
                val date = note?.date
                val time = note?.time
                var priorityNumber = 0
                if (priority == "High") {
                    priorityNumber = 3
                } else if (priority == "Medium") {
                    priorityNumber = 2
                } else if (priority == "Low") {
                    priorityNumber = 1
                }
                val id = note?.id
                intent.putExtra(AddEditNoteActivity.Companion.EXTRA_TITLE, title)
                intent.putExtra(AddEditNoteActivity.Companion.EXTRA_DESCRIPTION, description)
                intent.putExtra(AddEditNoteActivity.Companion.EXTRA_PRIORITY, priority)
                intent.putExtra(AddEditNoteActivity.Companion.EXTRA_PRIORITY_NUMBER, priorityNumber)
                intent.putExtra(AddEditNoteActivity.Companion.EXTRA_ID, id)
                intent.putExtra(AddEditNoteActivity.Companion.EXTRA_DATE, date)
                intent.putExtra(AddEditNoteActivity.Companion.EXTRA_TIME, time)
                startActivityForResult(intent, Edit_Note_Request)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Add_Note_Request && resultCode == RESULT_OK) {
            val title = data!!.getStringExtra(AddEditNoteActivity.Companion.EXTRA_TITLE)
            val description = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_DESCRIPTION)
            val priority = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_PRIORITY)
            val date = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_DATE)
            val time = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_TIME)
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
        } else if (requestCode == Edit_Note_Request && resultCode == RESULT_OK) {
            val id = data!!.getIntExtra(AddEditNoteActivity.Companion.EXTRA_ID, -1)
            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show()
                return
            }
            val title = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_TITLE)
            val description = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_DESCRIPTION)
            val priority = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_PRIORITY)
            val date = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_DATE)
            val time = data.getStringExtra(AddEditNoteActivity.Companion.EXTRA_TIME)
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
        } else {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        sharedPreferences = getSharedPreferences(
            "sharedPrefs", MODE_PRIVATE
        )
        editor = sharedPreferences!!.edit()
        isDarkModeOn = sharedPreferences!!
            .getBoolean(
                "isDarkModeOn", false
            )
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView?
        val nightMode = menu.findItem(R.id.night_mode)
        val dayMode = menu.findItem(R.id.day_mode)
        if (isDarkModeOn) {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            dayMode.isVisible = true
            nightMode.isVisible = false
        } else {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            dayMode.isVisible = false
            nightMode.isVisible = true
        }
        nightMode.setOnMenuItemClickListener {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            editor!!.putBoolean(
                "isDarkModeOn", true
            )
            editor!!.apply()
            Toast.makeText(applicationContext, "Dark Mode On ", Toast.LENGTH_SHORT).show()
            dayMode.isVisible = true
            nightMode.isVisible = false
            true
        }
        dayMode.setOnMenuItemClickListener {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            editor!!.putBoolean(
                "isDarkModeOn", false
            )
            editor!!.apply()
            Toast.makeText(applicationContext, "Dark Mode Off", Toast.LENGTH_SHORT).show()
            dayMode.isVisible = false
            nightMode.isVisible = true
            true
        }
        searchView!!.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getNotesFromDb(query)
                return false
            }

            private fun getNotesFromDb(searchText: String) {
                var searchText = searchText
                searchText = "%$searchText%"
                noteViewModel!!.getSearchedNotes(searchText)!!
                    .observe(this@MainActivity, object : Observer<List<Note?>?> {
                        override fun onChanged(notes: List<Note?>?) {
                            if (notes == null) {
                                return
                            }
                            //                        NoteAdapter adapter = new NoteAdapter();
                            adapter!!.submitList(notes)
                            //                        completeList = notes;
//                        recyclerView.setAdapter(adapter);
                        }
                    })
            }

            override fun onQueryTextChange(newText: String): Boolean {
                getNotesFromDb(newText)
                return false
            }
        })
        return true
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

    companion object {
        const val Add_Note_Request = 1
        const val Edit_Note_Request = 2
    }
}