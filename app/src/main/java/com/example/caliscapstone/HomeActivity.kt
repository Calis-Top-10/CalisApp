package com.example.caliscapstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.caliscapstone.ui.learning.calculate.CalculateActivity
import com.example.caliscapstone.ui.learning.read.ReadActivity
import com.example.caliscapstone.ui.learning.report.ReportActivity
import com.example.caliscapstone.ui.learning.write.WriteActivity
import com.example.caliscapstone.ui.login.LoginActivity
import com.example.caliscapstone.ui.setting.ApplicationSettingActivity
import com.example.caliscapstone.ui.setting.UserSettingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class HomeActivity : AppCompatActivity() {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
            intent = Intent(this@HomeActivity, UserSettingActivity::class.java)
            startActivity(intent)
        }
        settingPage.setOnClickListener {
            intent = Intent(this@HomeActivity, ApplicationSettingActivity::class.java)
            startActivity(intent)
        }
        readPage.setOnClickListener {
            intent = Intent(this@HomeActivity, ReadActivity::class.java)
            startActivity(intent)
        }
        writePage.setOnClickListener {
            intent = Intent(this@HomeActivity, WriteActivity::class.java)
            startActivity(intent)
        }
        calculatePage.setOnClickListener {
            intent = Intent(this@HomeActivity, CalculateActivity::class.java)
            startActivity(intent)
        }
        reportPage.setOnClickListener {
            intent = Intent(this@HomeActivity, ReportActivity::class.java)
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