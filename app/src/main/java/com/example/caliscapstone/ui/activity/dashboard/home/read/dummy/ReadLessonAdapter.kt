package com.example.caliscapstone.ui.activity.dashboard.home.read.dummy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnAdapterChangeListener
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.read.Lesson

class ReadLessonAdapter(val lessons: List<Lesson>, val listener: OnAdapterListener) :
    RecyclerView.Adapter<ReadLessonAdapter.ViewHolder>()  {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.lesson_id)
        private val tvLesson: TextView = itemView.findViewById(R.id.lesson_name)
        private val tvLevel: TextView = itemView.findViewById(R.id.lesson_level)

        fun bindView(lessons: Lesson) {
            tvId.text = lessons.lessonId
            tvLevel.text = lessons.lessonLevel.toString()
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