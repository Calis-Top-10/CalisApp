package com.example.caliscapstone.ui.activity.dashboard.setting

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.login.RandomUuidValue
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonAdapter

class UserAdapter(val children: RandomUuidValue) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>()   {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.lesson_id)
        private val tvLesson: TextView = itemView.findViewById(R.id.lesson_name)
        private val tvLevel: TextView = itemView.findViewById(R.id.lesson_level)

        /*
        fun bindView(lessons: UserAdapter) {
            tvId.text = lessons.lessonId
            tvLevel.text = lessons.lessonLevel.toString()
            tvLesson.text = lessons.lessonType
        }

         */
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}