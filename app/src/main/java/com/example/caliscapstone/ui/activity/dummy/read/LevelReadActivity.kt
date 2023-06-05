package com.example.caliscapstone.ui.activity.dummy.read

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dummy.read.response.Item
import com.example.caliscapstone.ui.activity.dummy.read.response.Lesson

class LevelReadActivity : AppCompatActivity() {
    private lateinit var rvUsersList: RecyclerView
    private val itemList: ArrayList<Item> = ArrayList()
    private val answerList: List<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_read)

        val data = intent.getParcelableExtra<Lesson>("DATA")
        Log.d("Detail Data", data?.id.toString())
        Log.d("Detail Data", data?.items.toString())

    }

}