package com.example.caliscapstone.ui.activity.dashboard.home.calculate.level

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.caliscapstone.R
import com.example.caliscapstone.data.api.ApiConfig
import com.example.caliscapstone.data.model.whoami.Whoami
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

class CalculateDasar : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_dasar)

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

        val account: GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)
        val idToken = account?.idToken

        if (idToken != null) {
            ApiConfig.getApiService()
                .getWhoami("Bearer $idToken")
                .enqueue(object : Callback<Whoami> {
                    override fun onResponse(
                        call: Call<Whoami>,
                        response: Response<Whoami>
                    ) {
                        val text_user = findViewById<TextView>(R.id.tv_user)
                        val text_name = findViewById<TextView>(R.id.tv_name)
                        text_user.text = response.body()?.user
                        text_name.text = response.body()?.name
                    }

                    override fun onFailure(call: Call<Whoami>, t: Throwable) {
                        Log.e("Failed", t.message.toString())
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
}