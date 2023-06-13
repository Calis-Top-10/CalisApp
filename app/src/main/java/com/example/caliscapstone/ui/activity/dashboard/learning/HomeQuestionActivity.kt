package com.example.caliscapstone.ui.activity.dashboard.learning

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.Question
import com.example.caliscapstone.ui.activity.dashboard.learning.type.CalculateImageAcitivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.CalculateTextActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.OnGoingActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.ReadVoiceAcitivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.WriteTextActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.WriteVoiceAcitivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.lang.IllegalArgumentException

class HomeQuestionActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    private lateinit var questionList: ArrayList<Question>

    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            launchNextIntent()
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("intent_question")
                result.data?.getStringExtra("currentPosition")
            }
        }

    private var currentQuestionIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_question)
        /*
        val tvId: TextView = findViewById(R.id.lesson_id)
        val tvLevel: TextView = findViewById(R.id.lesson_level)
        val tvLesson: TextView = findViewById(R.id.lesson_name)
        tvId.text = intent.getStringExtra("intent_id")
        tvLevel.text = intent.getStringExtra("intent_level")
        tvLesson.text = intent.getStringExtra("intent_type")
        print(intent.getSerializableExtra("intent_question").toString())
        Log.d("ResponseQuestion", intent.getSerializableExtra("intent_question").toString())
         */

        questionList = intent.getSerializableExtra("intent_question") as ArrayList<Question>
        launchNextIntent()

    }

    private fun launchNextIntent() {
        Log.d("SucessBanget", currentQuestionIndex.toString())
        val question = questionList[currentQuestionIndex]
        val maxIndex = questionList.count()-1
        val isTheLastQuestion = currentQuestionIndex == maxIndex
        if (isTheLastQuestion) {
            finish()
        } else {
            val progressBarValue = (currentQuestionIndex.toFloat()/maxIndex)*100
            intentLauncher.launch(
                Intent(this, getQuestionActivityFromString(question.questionType))
                    .putExtra("intent_question", question)
                    .putExtra("question_details", question.questionDetails)
                    .putExtra("progrees_bar_value", progressBarValue.toInt())
            )

            currentQuestionIndex++
        }
    }

    private fun getQuestionActivityFromString(questionType: String): Class<out AppCompatActivity> {
        val questionActivity = when (questionType) {
            "baca_input_suara" -> ReadVoiceAcitivity::class.java
            "baca_kalimat_input_suara" -> OnGoingActivity::class.java
            "tulis_soal_suara" -> WriteVoiceAcitivity::class.java
            "tulis_soal_teks" -> WriteTextActivity::class.java
            "tulis_soal_baca" -> OnGoingActivity::class.java
            "hitung_soal_gambar" -> CalculateImageAcitivity::class.java
            "hitung_soal_teks" -> CalculateTextActivity::class.java
            else -> {throw IllegalArgumentException("Question Type Is Not Found")}
        }
        return questionActivity
    }
}