package com.example.caliscapstone.ui.activity.dashboard.learning

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.add_children.AddChildren
import com.example.caliscapstone.data.model.get_lesson.Question
import com.example.caliscapstone.data.model.learningProgress.AddUserLearning
import com.example.caliscapstone.data.model.learningProgress.QuestionAttempt
import com.example.caliscapstone.ui.activity.dashboard.learning.type.CalculateImageAcitivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.CalculateTextActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.OnGoingActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.ReadVoiceAcitivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.WriteTextActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.type.WriteVoiceAcitivity
import com.example.caliscapstone.utils.Constanta
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeQuestionActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    private lateinit var questionList: ArrayList<Question>
    private var questionAttempts: ArrayList<QuestionAttempt> = arrayListOf()

    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            launchNextIntent()
            if (result.resultCode == Activity.RESULT_OK) {
                val isCorrects =  result.data!!.getBooleanArrayExtra("isCorrect_array")
                val questionId = result.data!!.getStringExtra("questionId")!!
                isCorrects!!.forEach { isCorrect ->
                    questionAttempts.add(QuestionAttempt(questionId,  isCorrect))
                }
            }
        }

    private var currentQuestionIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_question)
        questionList = intent.getSerializableExtra("intent_question") as ArrayList<Question>
        launchNextIntent()
    }

    private fun launchNextIntent() {
        val question = questionList[currentQuestionIndex]
        val maxIndex = questionList.count()-1
        val isTheLastQuestion = currentQuestionIndex == maxIndex
        if (isTheLastQuestion) {
            finish()
            addUserLearningData()
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

    private fun addUserLearningData() {
        val postModel = AddUserLearning(
            intent.getStringExtra("child_id")!!,
            intent.getStringExtra("lesson_id")!!,
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            questionAttempts
        )

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        val addResult = MutableLiveData<AddUserLearning>()
        val error = MutableLiveData("")

        val client = ApiConfig.getApiService().doAddLearningProgress("Bearer $idToken",postModel)
        client.enqueue(object : Callback<AddUserLearning> {
            override fun onResponse(call: Call<AddUserLearning>, response: Response<AddUserLearning>) {
                if (response.isSuccessful) {
                    Log.d("ADDUSERLEARNING", "SUCCESS")
                    addResult.postValue(response.body())
                } else {
                    response.errorBody()?.let {
                        val errorResponse = JSONObject(it.string())
                        val errorMessages = errorResponse.getString("message")
                        error.postValue("ADD USER LEARNING ERROR : $errorMessages")
                    }
                }
            }

            override fun onFailure(call: Call<AddUserLearning>, t: Throwable) {
                Log.e(Constanta.TAG_AUTH, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }
}