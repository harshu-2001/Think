package com.onedeveloper.think.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.onedeveloper.think.model.Note
import java.text.SimpleDateFormat
import java.util.Calendar


@Database(entities = [Note::class], version = 3)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDoa(): NoteDoa


    class PopulateDbAsyncTask constructor(db: NoteDatabase?) :
        AsyncTask<Void?, Void?, Void?>() {
        private val noteDao: NoteDoa

        init {
            noteDao = db!!.noteDoa()
        }

        override fun doInBackground(vararg voids: Void?): Void? {

            //Calendar calendar = Calendar.getInstance();
            //String currentDate  = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

//            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//            String result = currentDate+", "+sdf.format(Calendar.getInstance().getTime());
//            System.out.println(result);
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd MMMM yyyy")
            val timeFormat = SimpleDateFormat("hh:mm a")
            val date = dateFormat.format(calendar.time)
            val ntime = timeFormat.format(calendar.time)
            val time = ntime.replace("am", "AM").replace("pm", "PM")
            noteDao.insert(Note("Title 1", "Description 1", "High", 3, date, time))
            noteDao.insert(Note("Title 2", "Description 2", "Medium", 2, date, time))
            noteDao.insert(Note("Title 3", "Description 3", "Low", 1, date, time))
            return null
        }

    }

    companion object {
        private var instance: NoteDatabase? = null
        @Synchronized
        fun getInstance(context: Context): NoteDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext, NoteDatabase::class.java, "notes_database.db"
                )
                    .fallbackToDestructiveMigration() // this is only for testing,
                    .addCallback(roomCallback)
                    .build()
            }
            return instance
        }

        private val roomCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance).execute()
            }
        }
    }
}