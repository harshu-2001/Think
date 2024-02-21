package com.onedeveloper.think.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.onedeveloper.think.R
import com.onedeveloper.think.constants.GlobalConstants
import com.onedeveloper.think.model.Note


class NoteAdapter(private var mNotes:List<Note?>?) :  RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView
        val textViewDescription: TextView
        val textViewPriority: TextView
        val textViewDateTime: TextView
//        val cardView: MaterialCardView

        init {
            textViewTitle = itemView.findViewById<TextView>(R.id.text_view_title)
            textViewDescription = itemView.findViewById<TextView>(R.id.text_view_description)
            textViewPriority = itemView.findViewById<TextView>(R.id.text_view_priority)
//            cardView = itemView.findViewById<MaterialCardView>(R.id.cardView)
            textViewDateTime = itemView.findViewById<TextView>(R.id.text_view_date_time)
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(mNotes?.get(position))
                }
            }
        }
    }

    private var listener: onItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mNotes?.size?: 0
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val currentNode = mNotes?.get(position)
        holder.textViewTitle.text = currentNode!!.title
        holder.textViewDescription.text = currentNode.description
        holder.textViewPriority.text = currentNode.priority.toString()
        holder.textViewDateTime.text = currentNode.date
    }

    fun getNoteAt(position: Int): Note? {
        return mNotes?.get(position)
    }


    interface onItemClickListener {
        fun onItemClick(note: Note?)
    }

    fun setOnItemClickListener(listener: onItemClickListener?) {
        this.listener = listener
    }

//    companion object {
//        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Note> =
//            object : DiffUtil.ItemCallback<Note>() {
//                override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
//                    return oldItem.id == newItem.id
//                }
//
//                override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
//                    return oldItem.title == newItem.title && oldItem.description == newItem.description && oldItem.priority == newItem.priority
//                }
//            }
//    }
}