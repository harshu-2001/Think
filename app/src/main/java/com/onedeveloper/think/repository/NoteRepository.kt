package com.onedeveloper.think.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.onedeveloper.think.database.NoteDatabase.Companion.getInstance
import com.onedeveloper.think.database.NoteDoa
import com.onedeveloper.think.model.Note

class NoteRepository(application: Application?) {
    private val noteDoa: NoteDoa
    val allNotes: LiveData<List<Note?>?>?

    init {
        val database = getInstance(application!!)
        noteDoa = database!!.noteDoa()
        allNotes = noteDoa.getAllNotes()
    }

    fun insert(note: Note?) {
        InsertNodeAsyncTask(noteDoa).execute(note)
    }

    fun update(note: Note?) {
        UpdateNodeAsyncTask(noteDoa).execute(note)
    }

    fun delete(note: Note?) {
        DeleteNodeAsyncTask(noteDoa).execute(note)
    }

    fun deleteAllNotes() {
        DeleteAllNodeAsyncTask(noteDoa).execute()
    }

    fun getSearchedNotes(NoteText: String?): LiveData<List<Note?>?>? {
        return noteDoa.getSearchedNotes(NoteText)
    }


    private class InsertNodeAsyncTask(private val noteDoa: NoteDoa) :
        AsyncTask<Note, Void?, Void?>() {
        override fun doInBackground(vararg notes: Note): Void? {
            noteDoa.insert(notes[0])
            return null
        }
    }

    private class UpdateNodeAsyncTask(private val noteDoa: NoteDoa) :
        AsyncTask<Note, Void?, Void?>() {
        override fun doInBackground(vararg notes: Note): Void? {
            noteDoa.update(notes[0])
            return null
        }
    }

    private class DeleteNodeAsyncTask(private val noteDoa: NoteDoa) :
        AsyncTask<Note, Void?, Void?>() {
        override fun doInBackground(vararg notes: Note): Void? {
            noteDoa.delete(notes[0])
            return null
        }
    }

    private class DeleteAllNodeAsyncTask(private val noteDoa: NoteDoa) :
        AsyncTask<Void, Void?, Void?>() {
           override fun doInBackground(vararg notes: Void): Void? {
            noteDoa.deleteAllNotes()
            return null        }
    }
}