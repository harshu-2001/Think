package com.onedeveloper.think.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.onedeveloper.think.model.Note

@Dao
interface NoteDoa {
    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM NOTE_TABLE")
    fun deleteAllNotes()

    @Query("SELECT * FROM NOTE_TABLE WHERE title like :NoteText ORDER BY PRIORITYNUMBER DESC")
    fun getSearchedNotes(NoteText: String?): LiveData<List<Note?>?>?

    @Query("SELECT * FROM note_table ORDER BY PRIORITYNUMBER DESC")
    fun getAllNotes(): LiveData<List<Note?>?>?
}