package com.example.caliscapstone.ui.activity.dashboard.home

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.get_lesson.ResponseRead
import com.example.caliscapstone.data.model.pengayaan.Question
import com.example.caliscapstone.data.model.pengayaan.ResponsePengayaan
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonAdapter
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeQuestionActivity
import com.example.caliscapstone.ui.activity.dashboard.pengayaan.PengayaanActivity
import com.example.caliscapstone.ui.activity.dashboard.report.ReportActivity
import com.example.caliscapstone.ui.activity.dashboard.setting.ApplicationSettingActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.example.caliscapstone.ui.activity.dashboard.setting.UserSettingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val serverClientId = getString(R.string.web_client_id)

        /* Intent Extra */
        val intentId = intent.getStringExtra("intent_uuid")
        val intentName = intent.getStringExtra("intent_name")
        val intentAge = intent.getStringExtra("intent_age")
        if (intentId != null) {
            Log.d("successDebugId", intentId)
        }
        if (intentName != null) {
            Log.d("successDebugName", intentName)
        }
        if (intentAge != null) {
            Log.d("successDebugAge", intentAge)
        }


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

        val readPage = findViewById<FrameLayout>(R.id.nav_read)
        val writePage = findViewById<FrameLayout>(R.id.nav_write)
        val calculatePage = findViewById<FrameLayout>(R.id.nav_calculate)
        val reportPage = findViewById<FrameLayout>(R.id.nav_report)
        val userPage = findViewById<ImageView>(R.id.userMenu)
        val settingPage = findViewById<ImageView>(R.id.settingsMenu)
        val personalLesson = findViewById<ConstraintLayout>(R.id.pengayaan)

        userPage.setOnClickListener {
            intent = Intent(this@HomeActivity, UserSettingActivity::class.java)
            startActivity(intent)
        }
        settingPage.setOnClickListener {
            intent = Intent(this@HomeActivity, ApplicationSettingActivity::class.java)
                .putExtra("intent_uuid", intentId)
                .putExtra("intent_name", intentName)
                .putExtra("intent_age", intentAge)
            startActivity(intent)
        }
        readPage.setOnClickListener {
            intent = Intent(this@HomeActivity, HomeLessonActivity::class.java)
                .putExtra("read_hover", "baca")
            startActivity(intent)
        }
        writePage.setOnClickListener {
            intent = Intent(this@HomeActivity, HomeLessonActivity::class.java)
                .putExtra("write_hover", "tulis")
            startActivity(intent)
        }
        calculatePage.setOnClickListener {
            intent = Intent(this@HomeActivity, HomeLessonActivity::class.java)
                .putExtra("calculate_hover", "hitung")
            startActivity(intent)
        }
        reportPage.setOnClickListener {
            intent = Intent(this@HomeActivity, ReportActivity::class.java)
                .putExtra("intent_uuid", intentId)
                .putExtra("intent_name", intentName)
                .putExtra("intent_age", intentAge)
            startActivity(intent)
        }
        personalLesson.setOnClickListener {
            if (intentId != null) {
                getPersonallesson(intentId)
            }
        }
    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }


    private fun getPersonallesson(childId: String) {

        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getpersonalLesson("Bearer $idToken", childId)
                .enqueue(object : Callback<ResponsePengayaan> {
                    override fun onResponse(
                        call: Call<ResponsePengayaan>,
                        response: Response<ResponsePengayaan>
                    ) {
                        when (response.code()) {
                            401 -> {
                                val builder = AlertDialog.Builder(this@HomeActivity)
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
                            }

                            200 -> {
                                val responseBody = response.body()!!
                                Log.e("Success", response.body().toString())
                                startActivity(
                                    Intent(this@HomeActivity, PengayaanActivity::class.java)
                                        .putExtra("intent_question", responseBody.questions)
                                        .putExtra("intent_id", childId)
                                )
                            }
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<ResponsePengayaan>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }
}