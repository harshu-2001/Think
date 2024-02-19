package com.onedeveloper.think.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.onedeveloper.think.model.Note
import com.onedeveloper.think.repository.NoteRepository

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: NoteRepository
    private var allNotes: LiveData<List<Note?>?>?

    init {
        repository = NoteRepository(application)
        allNotes = repository.allNotes
    }

    fun insert(note: Note?) {
        repository.insert(note)
    }

    fun update(note: Note?) {
        repository.update(note)
    }

    fun delete(note: Note?) {
        repository.delete(note)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun getSearchedNotes(NoteText: String?): LiveData<List<Note?>?>? {
        return repository.getSearchedNotes(NoteText)
    }
    fun getAllNotes(): LiveData<List<Note?>?>? {
        return allNotes
    }



}