package com.example.caliscapstone.ui.activity.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class UserSettingActivity : AppCompatActivity() {

    private lateinit var gImage: ImageView

    private lateinit var gName: TextView
    private lateinit var gEmail: TextView
    private lateinit var gId: TextView
    private lateinit var gSignOut: Button
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)

        gImage = findViewById(R.id.g_image)
        gName = findViewById(R.id.g_name)
        gId = findViewById(R.id.g_id)
        gEmail = findViewById(R.id.g_email)
        gSignOut = findViewById(R.id.g_sign_out)

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

        if (account!=null) {
            gName.text=account.displayName
            gEmail.text=account.email
            gId.text=account.id

            Glide.with(applicationContext).load(account.photoUrl).into(gImage)
        }

        else {
            goSignOut()
        }

        gSignOut.setOnClickListener {
            goSignOut()
        }

    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}