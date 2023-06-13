package com.example.caliscapstone.ui.activity.dashboard.learning

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.get_lesson.ResponseRead
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeLessonActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var rvLessonList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_lesson)
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

        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)

        if (account == null) {
            goSignOut()
        }

        val type: String = (intent.getStringExtra("read_hover")
            ?: (intent.getStringExtra("write_hover")
                ?: (intent.getStringExtra("calculate_hover")))) as String

        getLessonsByType(type)
    }
    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getLessonsByType(type: String) {
        var data: ArrayList<Lesson>

        val recyclerView = findViewById<RecyclerView>(R.id.rv_lesson)
        recyclerView.layoutManager = LinearLayoutManager( this@HomeLessonActivity)
        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getLessons("Bearer $idToken", type)
                .enqueue(object : Callback<ResponseRead> {
                    override fun onResponse(
                        call: Call<ResponseRead>,
                        response: Response<ResponseRead>
                    ) {
                        try {
                            val responseBody = response.body()!!
                            Log.e("Success", response.body().toString())
                            data = responseBody.lessons
                            val adapter = HomeLessonAdapter(data, object : HomeLessonAdapter.OnAdapterListener {
                                override fun onItemClicked(data: Lesson) {
                                    // Toast.makeText(applicationContext, data.lessonType, Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@HomeLessonActivity, HomeQuestionActivity::class.java)
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

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<ResponseRead>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }
}