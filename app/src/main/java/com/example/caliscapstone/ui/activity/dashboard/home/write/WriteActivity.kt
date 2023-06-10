package com.example.caliscapstone.ui.activity.dashboard.home.write

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dashboard.home.write.level.WriteDasar
import com.example.caliscapstone.ui.activity.dashboard.home.write.level.WriteLanjut
import com.example.caliscapstone.ui.activity.dashboard.home.write.level.WriteMenengah
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class WriteActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

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
            intent = Intent(this@WriteActivity, HomeWriteActivity::class.java)
            startActivity(intent)
        }

        levelBasic.setOnClickListener {
            intent = Intent(this@WriteActivity, WriteDasar::class.java)
            startActivity(intent)
        }

        levelAdvanced.setOnClickListener {
            intent = Intent(this@WriteActivity, WriteMenengah::class.java)
            startActivity(intent)
        }

        levelSolid.setOnClickListener {
            intent = Intent(this@WriteActivity, WriteLanjut::class.java)
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