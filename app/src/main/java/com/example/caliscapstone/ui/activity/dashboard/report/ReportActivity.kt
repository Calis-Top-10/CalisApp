package com.example.caliscapstone.ui.activity.dashboard.report

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.airbnb.lottie.LottieAnimationView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.get_lesson.Lesson
import com.example.caliscapstone.data.model.login.ResponseLogin
import com.example.caliscapstone.data.model.report.UserReport
import com.example.caliscapstone.ui.activity.dashboard.home.HomeActivity
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonAdapter
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeQuestionActivity
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

class ReportActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setLoadingState(false)
        val serverClientId = getString(R.string.web_client_id)

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

        if (intentId != null) {
            userReport(intentId)
        }

        /* toolbar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@ReportActivity,
                    HomeActivity::class.java
                )
                    .putExtra(
                        "intent_name", intentName
                    )
                    .putExtra(
                        "intent_age", intentAge
                    )
                    .putExtra(
                        "intent_uuid", intentId
                    )
            )
            finish()

        }

        val nameReport = findViewById<TextView>(R.id.nameReport)
        nameReport.text = intentName

    }

    private fun userReport(childId: String) {

        setLoadingState(true)
        val checkBoxMonday = findViewById<CheckBox>(R.id.icon_senin)
        val checkBoxTuesday = findViewById<CheckBox>(R.id.icon_selasa)
        val checkBoxWednesday = findViewById<CheckBox>(R.id.icon_rabu)
        val checkBoxThursday = findViewById<CheckBox>(R.id.icon_kamis)
        val checkBoxFriday = findViewById<CheckBox>(R.id.icon_jumat)
        val checkBoxSaturday = findViewById<CheckBox>(R.id.icon_sabtu)
        val checkBoxSunday = findViewById<CheckBox>(R.id.icon_minggu)
        val learningTag = findViewById<TextView>(R.id.learningTag)

        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken
        if (idToken != null) {
            ApiConfig.getApiService()
                .getUserReport("Bearer $idToken", childId)
                .enqueue(object : Callback<UserReport> {
                    override fun onResponse(
                        call: Call<UserReport>,
                        response: Response<UserReport>
                    ) {
                        when (response.code()) {
                            401 -> {
                                val builder = AlertDialog.Builder(this@ReportActivity)
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

                            200 -> {
                                val responseBody = response.body()!!
                                val tags = responseBody.tag
                                val mondayResponse = responseBody.learningProgress.monday
                                val tuesdayResponse = responseBody.learningProgress.tuesday
                                val wednesdayResponse = responseBody.learningProgress.wednesday
                                val thursdayResponse = responseBody.learningProgress.thursday
                                val fridayResponse = responseBody.learningProgress.friday
                                val saturdayResponse = responseBody.learningProgress.saturday
                                val sundayResponse = responseBody.learningProgress.sunday

                                learningTag.text = tags.toString()
                                checkBoxMonday.isChecked = mondayResponse
                                checkBoxTuesday.isChecked = tuesdayResponse
                                checkBoxWednesday.isChecked = wednesdayResponse
                                checkBoxThursday.isChecked = thursdayResponse
                                checkBoxFriday.isChecked = fridayResponse
                                checkBoxSaturday.isChecked = saturdayResponse
                                checkBoxSunday.isChecked = sundayResponse
                                setLoadingState(false)
                            }
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<UserReport>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
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
}