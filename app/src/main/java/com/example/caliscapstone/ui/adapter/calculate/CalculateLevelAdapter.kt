package com.example.caliscapstone.ui.adapter.calculate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dashboard.home.calculate.response.Item

class CalculateLevelAdapter(val context: Context, private val items: ArrayList<Item>) :
    RecyclerView.Adapter<CalculateLevelAdapter.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_level_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]
        holder.tvId.text = item.id.toString()
//        holder.tvType.text = item.type
//        holder.tyQuestion.text = item.question
//        holder.tyQuestionImage.text = item.question_img
//        holder.tvAnswer.text = item.answer.toString()
//        holder.tvHint.text = item.hint
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        val tvId = view.findViewById<TextView>(R.id.tv_id)
        val tvType = view.findViewById<TextView>(R.id.tv_type)
        val tyQuestion = view.findViewById<TextView>(R.id.tv_question)
        val tyQuestionImage = view.findViewById<TextView>(R.id.tv_question_image)
        val tvAnswer = view.findViewById<TextView>(R.id.tv_answer)
        val tvHint = view.findViewById<TextView>(R.id.tv_hint)
    }
}