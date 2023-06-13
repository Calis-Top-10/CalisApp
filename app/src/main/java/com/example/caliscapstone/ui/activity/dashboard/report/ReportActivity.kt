package com.example.caliscapstone.ui.activity.dashboard.report

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.login.ResponseLogin
import com.example.caliscapstone.data.model.report.UserReport
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
        val serverClientId = getString(R.string.web_client_id)

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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        /* toolbar */
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val checkBoxMonday = findViewById<CheckBox>(R.id.icon_senin)
        val checkBoxTuesday = findViewById<CheckBox>(R.id.icon_selasa)
        val checkBoxWednesday = findViewById<CheckBox>(R.id.icon_rabu)
        val checkBoxThursday = findViewById<CheckBox>(R.id.icon_kamis)
        val checkBoxFriday = findViewById<CheckBox>(R.id.icon_jumat)
        val checkBoxSaturday = findViewById<CheckBox>(R.id.icon_sabtu)
        val checkBoxSunday = findViewById<CheckBox>(R.id.icon_minggu)

        val childId = "29b9c342-0c75-459a-8513-23443aec7283"

        userReport(childId)

    }

    private fun userReport(childId: String) {
        val account: GoogleSignInAccount?= GoogleSignIn
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
                        try {
                            val responseBody = response.body()!!
                            Log.e("User Report", responseBody.toString())
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }

                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onFailure(call: Call<UserReport>, t: Throwable) {
                        Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}")
                    }

                })
        }
    }
    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}