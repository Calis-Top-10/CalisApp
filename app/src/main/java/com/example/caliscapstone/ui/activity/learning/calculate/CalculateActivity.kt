package com.example.caliscapstone.ui.activity.learning.calculate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.caliscapstone.ui.activity.learning.calculate.level.CalculateDasar
import com.example.caliscapstone.ui.activity.learning.calculate.level.CalculateLanjut
import com.example.caliscapstone.ui.activity.learning.calculate.level.CalculateMenengah
import com.example.caliscapstone.ui.activity.home.HomeActivity
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class CalculateActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate)

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

        val backwardPage = findViewById<ImageView>(R.id.backward)
        val levelBasic = findViewById<ImageView>(R.id.imageLevel1)
        val levelAdvanced = findViewById<ImageView>(R.id.imageLevel2)
        val levelSolid = findViewById<ImageView>(R.id.imageLevel3)

        backwardPage.setOnClickListener {
            intent = Intent(this@CalculateActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        levelBasic.setOnClickListener {
            intent = Intent(this@CalculateActivity, CalculateDasar::class.java)
            startActivity(intent)
        }

        levelAdvanced.setOnClickListener {
            intent = Intent(this@CalculateActivity, CalculateMenengah::class.java)
            startActivity(intent)
        }

        levelSolid.setOnClickListener {
            intent = Intent(this@CalculateActivity, CalculateLanjut::class.java)
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