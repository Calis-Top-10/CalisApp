package com.example.caliscapstone.ui.activity.dashboard.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.login.RandomUuidValue
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonAdapter
import kotlinx.coroutines.NonCancellable.children

class UserAdapter(private val children: Map<String, RandomUuidValue>, val listener: UserAdapter.OnAdapterListener) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>()   {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.children_name)

        fun bindView(childData: RandomUuidValue) {
            tvName.text = childData.childName
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keyByIndex = children.keys.elementAt(position)
        val valueOfElement = children.getValue(keyByIndex)
        holder.itemView.setOnClickListener {
            listener.onItemClicked(valueOfElement,keyByIndex)
        }
        return holder.bindView(valueOfElement)
    }

    override fun getItemCount(): Int {
        return children.count()
    }

    interface OnAdapterListener {
        fun onItemClicked(childClick: RandomUuidValue, uuid: String)
    }
}