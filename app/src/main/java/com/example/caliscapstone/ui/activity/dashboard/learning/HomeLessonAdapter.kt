package com.example.caliscapstone.ui.activity.dashboard.learning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.Lesson
import java.lang.IllegalArgumentException
import java.util.logging.Level

class HomeLessonAdapter(val lessons: ArrayList<Lesson>, val listener: OnAdapterListener) :
    RecyclerView.Adapter<HomeLessonAdapter.ViewHolder>()  {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.lesson_id)
        private val tvLesson: TextView = itemView.findViewById(R.id.lesson_name)
        private val tvLevel: TextView = itemView.findViewById(R.id.lesson_level)

        fun bindView(lessons: Lesson) {
            val idConvert: String = when (lessons.lessonLevel) {
                100 -> "Soal dalam materi pembelajaran ini memiliki tingkat kesulitan di level dasar"
                200 -> "Soal dalam materi pembelajaran ini memiliki tingkat kesulitan di level menengah"
                300 -> "Soal dalam materi pembelajaran ini memiliki tingkat kesulitan di level menengah"
                400 -> "Soal dalam materi pembelajaran ini memiliki tingkat kesulitan di level sulit"
                500 -> "Soal dalam materi pembelajaran ini memiliki tingkat kesulitan di level expert"
                else -> {throw IllegalArgumentException("Id Type Is Not Found")
                }
            }
            tvId.text = idConvert

            val levelConvert: String = when (lessons.lessonLevel) {
                100 -> "Level 1"
                200 -> "Level 2"
                300 -> "Level 3"
                400 -> "Level 4"
                500 -> "Level 5"
                else -> {throw IllegalArgumentException("Level Type Is Not Found")
                }
            }
            tvLevel.text = levelConvert
            tvLesson.text = lessons.lessonType
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_lesson_read, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onItemClicked(lessons[position])
        }
        return holder.bindView(lessons[position])
    }

    interface OnAdapterListener {
        fun onItemClicked(data: Lesson)
    }

}