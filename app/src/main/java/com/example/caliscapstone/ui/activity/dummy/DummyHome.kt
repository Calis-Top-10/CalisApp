package com.example.caliscapstone.ui.activity.dummy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dummy.calculate.HomeCalculateActivity
import com.example.caliscapstone.ui.activity.dummy.read.HomeReadActivity
import com.example.caliscapstone.ui.activity.dummy.write.HomeWriteActivity
import com.example.caliscapstone.ui.activity.learning.read.ReadActivity
import com.example.caliscapstone.ui.activity.learning.report.ReportActivity
import com.example.caliscapstone.ui.activity.learning.write.WriteActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.example.caliscapstone.ui.activity.setting.ApplicationSettingActivity
import com.example.caliscapstone.ui.activity.setting.UserSettingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class DummyHome : AppCompatActivity() {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_home)

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

        //WindowCompat.setDecorFitsSystemWindows(window, false)
        //window.statusBarColor = Color.TRANSPARENT
        val readPage = findViewById<CardView>(R.id.nav_read)
        val writePage = findViewById<CardView>(R.id.nav_write)
        val calculatePage = findViewById<CardView>(R.id.nav_calculate)
        val reportPage = findViewById<CardView>(R.id.nav_report)
        val userPage = findViewById<ImageView>(R.id.userMenu)
        val settingPage = findViewById<ImageView>(R.id.settingsMenu)

        userPage.setOnClickListener {
            intent = Intent(this@DummyHome, UserSettingActivity::class.java)
            startActivity(intent)
        }
        settingPage.setOnClickListener {
            intent = Intent(this@DummyHome, ApplicationSettingActivity::class.java)
            startActivity(intent)
        }
        readPage.setOnClickListener {
            intent = Intent(this@DummyHome, HomeReadActivity::class.java)
            startActivity(intent)
        }
        writePage.setOnClickListener {
            intent = Intent(this@DummyHome, HomeWriteActivity::class.java)
            startActivity(intent)
        }
        calculatePage.setOnClickListener {
            intent = Intent(this@DummyHome, HomeCalculateActivity::class.java)
            startActivity(intent)
        }
        reportPage.setOnClickListener {
            intent = Intent(this@DummyHome, ReportActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}