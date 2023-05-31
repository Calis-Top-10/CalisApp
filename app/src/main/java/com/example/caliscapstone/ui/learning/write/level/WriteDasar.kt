package com.example.caliscapstone.ui.learning.write.level

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.caliscapstone.R
import com.example.caliscapstone.ui.login.LoginActivity
import com.example.caliscapstone.utils.draw.DrawView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.roundToInt

class WriteDasar : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    private lateinit var paint: DrawView
    private lateinit var sizeDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_dasar)

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

        paint = findViewById(R.id.draw_view)
        sizeDialog = Dialog(this)
        val saveBtn = findViewById<ImageView>(R.id.saveBtn)
        val undoBtn = findViewById<ImageView>(R.id.undoBtn)

        val clearBtn = findViewById<ImageView>(R.id.clearBtn)

        val vto = paint.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                paint.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = paint.measuredWidth
                val height = paint.measuredHeight
                paint.init(height, width)
            }
        })

        undoBtn.setOnClickListener {
            if (paint.isTouch()) {
                paint.undo()
            }else{
                Toast.makeText(this@WriteDasar, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()
            }
        }
        clearBtn.setOnClickListener {
            if (paint.isTouch()) {
                paint.clear()
            }else{
                Toast.makeText(this@WriteDasar, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()

            }
        }
        saveBtn.setOnClickListener {
            if (paint.isTouch()) {
                Toast.makeText(this@WriteDasar, "Jawaban Benar!!", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@WriteDasar, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()
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