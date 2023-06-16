package com.example.caliscapstone.ui.activity.dashboard.learning

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.get_lesson.ResponseRead
import com.example.caliscapstone.data.model.login.RandomUuidValue
import com.example.caliscapstone.ui.activity.dashboard.home.HomeActivity
import com.example.caliscapstone.ui.activity.dashboard.setting.UserAdapter
import com.example.caliscapstone.ui.activity.dashboard.setting.UserSettingActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import org.json.JSONObject
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
        setLoadingState(false)
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

        /* toolbar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val type: String = (intent.getStringExtra("read_hover")
            ?: (intent.getStringExtra("write_hover")
                ?: (intent.getStringExtra("calculate_hover")))) as String

        getLessonsByType(type)
    }

    private fun setLoadingState(loading: Boolean) {
        when (loading) {
            true -> {
                findViewById<LottieAnimationView>(R.id.loading).visibility = View.VISIBLE
            }

            false -> {
                findViewById<LottieAnimationView>(R.id.loading).visibility = View.GONE
            }
        }
    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getLessonsByType(type: String) {
        setLoadingState(true)
        var data: ArrayList<Lesson>
        val error = MutableLiveData("")
        val recyclerView = findViewById<RecyclerView>(R.id.rv_lesson)
        recyclerView.layoutManager = LinearLayoutManager(this@HomeLessonActivity)
        val account: GoogleSignInAccount? = GoogleSignIn
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
                        when (response.code()) {
                            401 -> {
                                val builder = AlertDialog.Builder(this@HomeLessonActivity)
                                builder.setTitle(R.string.signout)
                                builder.setMessage(R.string.API_error_header_token)
                                builder.setIcon(android.R.drawable.ic_dialog_alert)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    goSignOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Anda berhasil logout dari aplikasi",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.setCancelable(false)
                                alertDialog.show()

                                setLoadingState(false)
                            }

                            413 -> {
                                setLoadingState(false)
                                error.postValue(getString(R.string.API_error_large_payload))
                            }

                            200 -> {
                                val responseBody = response.body()!!
                                Log.e("Success", response.body().toString())
                                data = responseBody.lessons
                                val adapter = HomeLessonAdapter(
                                    data,
                                    object : HomeLessonAdapter.OnAdapterListener {
                                        override fun onItemClicked(data: Lesson) {
                                            // Toast.makeText(applicationContext, data.lessonType, Toast.LENGTH_SHORT).show()
                                            startActivity(
                                                Intent(
                                                    this@HomeLessonActivity,
                                                    HomeQuestionActivity::class.java
                                                )
                                                    .putExtra("intent_id", data.lessonId)
                                                    .putExtra(
                                                        "intent_level",
                                                        data.lessonLevel.toString()
                                                    )
                                                    .putExtra("intent_type", data.lessonType)
                                                    .putExtra("intent_question", data.questions)
                                            )
                                            finish()
                                        }
                                    })
                                recyclerView.adapter = adapter
                                setLoadingState(false)
                            }
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<ResponseRead>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                        setLoadingState(false)
                    }

                })
        }
    }
}