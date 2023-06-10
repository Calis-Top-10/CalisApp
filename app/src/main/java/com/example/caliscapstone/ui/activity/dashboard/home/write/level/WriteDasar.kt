package com.example.caliscapstone.ui.activity.dashboard.home.write.level

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.activity.dashboard.home.write.WriteActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.example.caliscapstone.utils.draw.DrawView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class WriteDasar : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var paint: DrawView
    private lateinit var sizeDialog: Dialog
    private var backwardPage: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_dasar)
        val saveBtn = findViewById<ImageView>(R.id.saveBtn)
        val undoBtn = findViewById<ImageView>(R.id.undoBtn)
        val clearBtn = findViewById<ImageView>(R.id.clearBtn)

        /* serverClientId */
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

        /* Backward Navigation */
        backwardPage?.setOnClickListener{
            intent = Intent(this@WriteDasar, WriteActivity::class.java)
            startActivity(intent)
        }

        /* Writing Box */
        paint = findViewById(R.id.draw_view)
        sizeDialog = Dialog(this)

        val vto = paint.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                paint.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = paint.measuredWidth
                val height = paint.measuredHeight
                paint.init(height, width)
            }
        })

        /* Undo Button */
        undoBtn.setOnClickListener {
            if (paint.isTouch()) {
                paint.undo()
            }else{
                Toast.makeText(this@WriteDasar, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()
            }
        }

        /* Clear Button */
        clearBtn.setOnClickListener {
            if (paint.isTouch()) {
                paint.clear()
            }else{
                Toast.makeText(this@WriteDasar, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()

            }
        }

        /* Save Button */
        saveBtn.setOnClickListener {
            if (paint.isTouch()) {
                Toast.makeText(this@WriteDasar, "Jawaban Benar!!", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@WriteDasar, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    /* Sign out if account==null */
    private fun goSignOut() {
        gsc.signOut().addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}