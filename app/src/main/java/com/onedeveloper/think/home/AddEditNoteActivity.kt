package com.onedeveloper.think.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.onedeveloper.think.R
import com.onedeveloper.think.constants.GlobalConstants
import com.onedeveloper.think.databinding.ActivityAddNoteBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class AddEditNoteActivity : AppCompatActivity() {

    var binding: ActivityAddNoteBinding? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        val timeFormat = SimpleDateFormat("hh:mm a")
        val date = dateFormat.format(calendar.time)
        val ntime = timeFormat.format(calendar.time)
        val time = ntime.replace("am", "AM").replace("pm", "PM")

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        val intent: Intent = intent

        if (intent.hasExtra(EXTRA_ID)) {
            binding?.titleNote?.text = "Edit Note"
            binding?.editTextTitle?.setText(intent.getStringExtra(EXTRA_TITLE))
            binding?.editTextDescription?.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            binding?.tvDate?.text = intent.getStringExtra(EXTRA_DATE)
            binding?.tvTime?.text = intent.getStringExtra(EXTRA_TIME)
//            mCardView?.setBackgroundColor(Color.parseColor(intent.getStringExtra(EXTRA_COLOR)))
            val array = arrayOf("High", "Medium", "Low")
            val adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, R.layout.style_spinner, array)
            binding?.spinnerPriority!!.adapter = adapter
        } else {
            binding?.titleNote?.text  = "Add Note"
            binding?.tvDate?.text = date
            binding?.tvTime?.text = time
            val array = arrayOf("High", "Medium", "Low")
            val adapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, R.layout.style_spinner, array)
            binding?.spinnerPriority!!.adapter = adapter
        }

        binding?.saveButton?.setOnClickListener {
            saveNote()
        }
        binding?.closeButton?.setOnClickListener {
            finish()
        }
    }

    private fun saveNote() {
        val title: String = binding?.editTextTitle?.text.toString()
        val description: String = binding?.editTextDescription?.text.toString()
        val priority = binding?.spinnerPriority!!.selectedItem.toString()
        val date: String = binding?.tvDate?.text.toString()
        val time: String = binding?.tvTime?.text.toString()
        val color:String = binding?.cardView?.background.toString()
        if (title.trim { it <= ' ' }.isEmpty() || description.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show()
            return
        }
        val data = Intent()
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_DESCRIPTION, description)
        data.putExtra(EXTRA_PRIORITY, priority)
        data.putExtra(EXTRA_PRIORITY_NUMBER, binding?.spinnerPriority!!.selectedItem.toString())
        data.putExtra(EXTRA_DATE, date)
        data.putExtra(EXTRA_TIME, time)
//        data.putExtra(EXTRA_COLOR,color)
        var priorityNumber = 0
        if (priority == "High") {
            priorityNumber = 3
        } else if (priority == "Medium") {
            priorityNumber = 2
        } else if (priority == "Low") {
            priorityNumber = 1
        }
        data.putExtra(EXTRA_PRIORITY_NUMBER, priorityNumber)
        val id: Int = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ID, id)
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }




    companion object {
        const val EXTRA_ID = "com.example.counterexample.EXTRA_ID"
        const val EXTRA_TITLE = "com.example.counterexample.EXTRA_TITLE"
        const val EXTRA_DESCRIPTION = "com.example.counterexample.EXTRA_DESCRIPTION"
        const val EXTRA_PRIORITY = "com.example.counterexample.EXTRA_PRIORITY"
        const val EXTRA_PRIORITY_NUMBER = "com.example.counterexample.EXTRA_PRIORITY_NUMBER"
        const val EXTRA_DATE = "com.example.counterexample.EXTRA_DATE"
        const val EXTRA_TIME = "com.example.counterexample.EXTRA_TIME"
//        const val EXTRA_COLOR = "com.example.counterexample.EXTRA_COLOR"
    }
}