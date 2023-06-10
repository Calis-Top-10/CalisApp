package com.example.caliscapstone.ui.activity.dashboard.home.read.dummy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.read.Lesson
import com.example.caliscapstone.data.model.get_lesson.read.Question
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DummyQuestionReadActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_question_read)
        val tvId: TextView = findViewById(R.id.lesson_id)
        val tvLevel: TextView = findViewById(R.id.lesson_level)
        val tvLesson: TextView = findViewById(R.id.lesson_name)
        tvId.text = intent.getStringExtra("intent_id")
        tvLevel.text = intent.getStringExtra("intent_level")
        tvLesson.text = intent.getStringExtra("intent_type")

        val testQuestion = intent.getStringExtra("intent_question")
        Log.e("Success",testQuestion.toString())
        /*
        intent.getStringExtra("intent_type")?.let { Log.e("Success", it) }


        var data: ArrayList<Question>
        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getQuestion1("Bearer $idToken")
                .enqueue(object : Callback<Lesson> {
                    override fun onResponse(
                        call: Call<Lesson>,
                        response: Response<Lesson>
                    ) {
                        try {
                            val responseBody = response.body()!!
                            Log.e("Success", response.body().toString())
                            data = responseBody.questions
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<Lesson>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
        }
        */
    }
}