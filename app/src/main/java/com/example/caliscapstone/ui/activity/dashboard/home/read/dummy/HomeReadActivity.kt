package com.example.caliscapstone.ui.activity.dashboard.home.read.dummy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.read.Lesson
import com.example.caliscapstone.data.model.get_lesson.read.Question
import com.example.caliscapstone.data.model.get_lesson.read.ResponseRead
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


class HomeReadActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var rvLessonList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_read)
        val serverClientId = getString(R.string.web_client_id)
        rvLessonList = findViewById(R.id.rv_lesson)
        rvLessonList.setHasFixedSize(true)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)

        if (account==null) {
            goSignOut()
        }

        testToken()
        testApiCalling()
    }
    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun testApiCalling() {
        var data: ArrayList<Lesson>
        val list: List<Question> = ArrayList<Question>()


        val recyclerView = findViewById<RecyclerView>(R.id.rv_lesson)
        recyclerView.layoutManager = LinearLayoutManager( this@HomeReadActivity)
        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getLessons("Bearer $idToken")
                .enqueue(object : Callback<ResponseRead> {
                    override fun onResponse(
                        call: Call<ResponseRead>,
                        response: Response<ResponseRead>
                    ) {
                        try {
                            val responseBody = response.body()!!
                            Log.e("Success", response.body().toString())
                            data = responseBody.lessons
                            val adapter = ReadLessonAdapter(data, object : ReadLessonAdapter.OnAdapterListener {
                                override fun onItemClicked(data: Lesson) {
                                    // Toast.makeText(applicationContext, data.lessonType, Toast.LENGTH_SHORT).show()
                                    val question: Question
                                    startActivity(Intent(this@HomeReadActivity, DummyQuestionReadActivity::class.java)
                                        .putExtra("intent_id", data.lessonId)
                                        .putExtra("intent_level", data.lessonLevel.toString())
                                        .putExtra("intent_type", data.lessonType)
                                        .putExtra("intent_question", data.questions)
                                    )
                                    finish()
                                }
                            })
                            recyclerView.adapter = adapter
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ResponseRead>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }


    private fun testToken() {
        val serverClientId = getString(R.string.web_client_id)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(baseContext)
        val idToken = account?.idToken
        val msg = "idToken: $idToken"
        Log.d("one tap", msg)
    }


    /*
    if (idToken != null) {
        ApiConfig.getApiService()
            .getLessons("Bearer $idToken")
            .enqueue(object : Callback<MutableList<Lesson>> {
                override fun onResponse(
                    call: Call<MutableList<Lesson>>,
                    response: Response<MutableList<Lesson>>
                ) {
                    if (response.isSuccessful) {
                        Log.e("Success", response.body().toString())
                        recyclerView.apply {
                            layoutManager = LinearLayoutManager( this@HomeReadActivity)
                            adapter = ReadLessonAdapter(response.body()!!)
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<Lesson>>, t: Throwable) {
                    Log.e("error", t.message.toString())
                }

            })
    }
    */
}