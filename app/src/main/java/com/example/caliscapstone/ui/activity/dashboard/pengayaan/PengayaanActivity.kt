package com.example.caliscapstone.ui.activity.dashboard.pengayaan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeQuestionActivity
import com.example.caliscapstone.data.model.get_lesson.Question

class PengayaanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengayaan)
        val intentChildId = intent.getStringExtra("intent_id")
        val data = intent.getSerializableExtra("intent_question") as ArrayList<Question>
        Log.d("variabel_data", data.toString())
        if (data.isNotEmpty()) {
            startActivity(
                Intent(
                    this@PengayaanActivity,
                    HomeQuestionActivity::class.java
                )
                    .putExtra("intent_id",intentChildId)
                    .putExtra("intent_question", data)
            )
        } else {
            Toast.makeText(this@PengayaanActivity, "Testing ini null", Toast.LENGTH_SHORT).show()
        }
    }

}