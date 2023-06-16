package com.example.caliscapstone.ui.activity.dashboard.learning.type

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.drawToBitmap
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.Question
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonActivity
import com.example.caliscapstone.tflite.CalisCharacterClassifier
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.example.caliscapstone.utils.draw.DrawView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class WriteTextActivity : AppCompatActivity() {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var paint: DrawView
    private lateinit var sizeDialog: Dialog

    private lateinit var calisCharacterClassifier: CalisCharacterClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_text)
        val saveBtn = findViewById<ImageView>(R.id.saveBtn)
        val undoBtn = findViewById<ImageView>(R.id.undoBtn)
        val clearBtn = findViewById<ImageView>(R.id.clearBtn)
        calisCharacterClassifier = CalisCharacterClassifier(assets)

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
        val backwardPage = findViewById<ImageView>(R.id.backward)
        backwardPage?.setOnClickListener{
            intent = Intent(this@WriteTextActivity, HomeLessonActivity::class.java)
                .putExtra("write_hover", "tulis")
            startActivity(intent)
        }

        /* Progress Bar Value */
        val progressBarValue =  intent.getIntExtra("progrees_bar_value", 0)
        val progressBarHorizontal = findViewById<ProgressBar>(R.id.progressBar)
        progressBarHorizontal.progress = progressBarValue

        /* Qyestion Box */
        val questionList = intent.getSerializableExtra("intent_question") as Question
        val questionBox = findViewById<TextView>(R.id.textQuiz)
        questionBox.text = questionList.questionDetails.question
        Log.d("testIndex", questionList.toString())

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
                Toast.makeText(this@WriteTextActivity, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()
            }
        }

        /* Clear Button */
        clearBtn.setOnClickListener {
            if (paint.isTouch()) {
                paint.clear()
            }else{
                Toast.makeText(this@WriteTextActivity, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()

            }
        }

        /* Save Button */
        saveBtn.setOnClickListener {
            if (paint.isTouch()) {
                val isCorrect = calisCharacterClassifier.isAnswersCorrect(
                    // TODO: Feed the expectedAnswers from the question
                    paint.drawToBitmap(Bitmap.Config.ARGB_8888), arrayListOf<String>("A", "0"))
                if (isCorrect) {
                    Toast.makeText(this@WriteTextActivity, "Jawaban Benar!!", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this@WriteTextActivity, "Jawaban Salah!!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@WriteTextActivity, "Jawaban Kosong!!", Toast.LENGTH_LONG).show()
            }
            finish()
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