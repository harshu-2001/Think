package com.onedeveloper.think.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.onedeveloper.think.R
import java.text.SimpleDateFormat
import java.util.Calendar

class AddEditNoteActivity : AppCompatActivity() {
    private var tvDate: TextView? = null
    private var tvTime: TextView? = null
    private var editTextTitle: TextInputEditText? = null
    private var editTextDescription: TextInputEditText? = null
    private var spinnerPriority: Spinner? = null

    //private String currentDate;
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        val calendar = Calendar.getInstance()
        //currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        val timeFormat = SimpleDateFormat("hh:mm a")
        val date = dateFormat.format(calendar.time)
        val ntime = timeFormat.format(calendar.time)
        val time = ntime.replace("am", "AM").replace("pm", "PM")
        editTextTitle = findViewById<TextInputEditText>(R.id.edit_text_title)
        editTextDescription = findViewById<TextInputEditText>(R.id.edit_text_Description)
        spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        tvDate = findViewById<TextView>(R.id.tv_date)
        tvTime = findViewById<TextView>(R.id.tv_time)
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_close)
        val intent: Intent = getIntent()
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Note")
            editTextTitle?.setText(intent.getStringExtra(EXTRA_TITLE))
            editTextDescription?.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            tvDate?.setText(intent.getStringExtra(EXTRA_DATE))
            tvTime?.setText(intent.getStringExtra(EXTRA_TIME))
            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.priorityList,R.layout.style_spinner);
            val array = arrayOf("High", "Medium", "Low")
            val adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, R.layout.style_spinner, array)
            spinnerPriority!!.adapter = adapter
            //spinnerPriority.setSelection(intent.getIntExtra(EXTRA_PRIORITY_NUMBER,1));
        } else {
            setTitle("Add Note")
            tvDate?.setText(date)
            tvTime?.setText(time)
            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.priorityList,R.layout.style_spinner);
            val array = arrayOf("High", "Medium", "Low")
            val adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, R.layout.style_spinner, array)
            spinnerPriority!!.adapter = adapter
            //spinnerPriority.setSelection(intent.getIntExtra(EXTRA_PRIORITY_NUMBER,1));
        }
    }

    private fun saveNote() {
        val title: String = editTextTitle?.getText().toString()
        val description: String = editTextDescription?.getText().toString()
        val priority = spinnerPriority!!.selectedItem.toString()
        val date: String = tvDate?.getText().toString()
        val time: String = tvTime?.getText().toString()
        if (title.trim { it <= ' ' }.isEmpty() || description.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show()
            return
        }
        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_DESCRIPTION, description)
        data.putExtra(EXTRA_PRIORITY, priority)
        data.putExtra(EXTRA_PRIORITY_NUMBER, spinnerPriority!!.selectedItem.toString())
        data.putExtra(EXTRA_DATE, date)
        data.putExtra(EXTRA_TIME, time)
        var priorityNumber = 0
        if (priority == "High") {
            priorityNumber = 3
        } else if (priority == "Medium") {
            priorityNumber = 2
        } else if (priority == "Low") {
            priorityNumber = 1
        }
        data.putExtra(EXTRA_PRIORITY_NUMBER, priorityNumber)
        val id: Int = getIntent().getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ID, id)
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater: MenuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_note -> {
                saveNote()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ID = "com.example.achitectureexample.EXTRA_ID"
        const val EXTRA_TITLE = "com.example.achitectureexample.EXTRA_TITLE"
        const val EXTRA_DESCRIPTION = "com.example.achitectureexample.EXTRA_DESCRIPTION"
        const val EXTRA_PRIORITY = "com.example.achitectureexample.EXTRA_PRIORITY"
        const val EXTRA_PRIORITY_NUMBER = "com.example.achitectureexample.EXTRA_PRIORITY_NUMBER"
        const val EXTRA_DATE = "com.example.achitectureexample.EXTRA_DATE"
        const val EXTRA_TIME = "com.example.achitectureexample.EXTRA_TIME"
    }
}