package com.example.caliscapstone.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.testing.UserModelClass

class UserAdapter(val context: Context, private val items: ArrayList<UserModelClass>) :
RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_user_layout,
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
        holder.tvName.text = item.name
        holder.tvEmail.text = item.email
        holder.tvGender.text = item.gender
        holder.tvWeight.text = item.weight.toString()
        holder.tvHeight.text = item.height.toString()
        holder.tvMobileNumber.text = item.mobile
        holder.tvOfficeNumber.text = item.office
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
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        val tvGender = view.findViewById<TextView>(R.id.tv_gender)
        val tvWeight = view.findViewById<TextView>(R.id.tv_weight)
        val tvHeight = view.findViewById<TextView>(R.id.tv_height)
        val tvMobileNumber = view.findViewById<TextView>(R.id.tv_mobile)
        val tvOfficeNumber = view.findViewById<TextView>(R.id.tv_office_number)
    }
}