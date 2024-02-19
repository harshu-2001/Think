package com.onedeveloper.think.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
class Note(
    @JvmField var title: String?,
    @JvmField var description: String?,
    @JvmField var priority: String?,
    @JvmField var priorityNumber: Int,
    @JvmField var date: String?,
    @JvmField var time: String?
) {
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id = 0

}