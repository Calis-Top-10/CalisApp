package com.example.caliscapstone.ui.activity.dashboard.home.read.dummy

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.read.Question

class ReadQuestionAdapter(val question: List<Question>) :
    RecyclerView.Adapter<ReadQuestionAdapter.ViewHolder>()   {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.lesson_id)
        private val tvLesson: TextView = itemView.findViewById(R.id.lesson_name)
        private val tvLevel: TextView = itemView.findViewById(R.id.lesson_level)

        fun bindView(lessons: Question) {
            tvId.text = lessons.question
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}