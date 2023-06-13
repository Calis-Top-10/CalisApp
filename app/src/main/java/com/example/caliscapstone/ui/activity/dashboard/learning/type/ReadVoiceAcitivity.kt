package com.example.caliscapstone.ui.activity.dashboard.learning.type

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.caliscapstone.R
import com.example.caliscapstone.data.model.get_lesson.Question
import com.example.caliscapstone.data.model.get_lesson.QuestionDetails
import com.example.caliscapstone.ui.activity.dashboard.learning.HomeLessonActivity
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.example.caliscapstone.utils.sound.playback.AndroidAudioPlayer
import com.example.caliscapstone.utils.sound.record.AndroidAudioRecorder
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import java.io.File
import java.util.Locale


class ReadVoiceAcitivity : AppCompatActivity(), TextToSpeech.OnInitListener   {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private var tts: TextToSpeech? = null
    private var buttonSpeak: ImageView? = null
    private var instructionText: TextView? = null
    private lateinit var questionList: QuestionDetails

    // Audio Interface
    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }
    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_voice_acitivity)
        buttonSpeak = findViewById(R.id.instruction)
        val continueButton = findViewById<FrameLayout>(R.id.continueButton)

        /* Backward Navigation */
        val backwardPage = findViewById<ImageView>(R.id.backward)
        backwardPage?.setOnClickListener{
            intent = Intent(this@ReadVoiceAcitivity, HomeLessonActivity::class.java)
                .putExtra("read_hover", "baca")
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

        /* Text To Speech quiz */
        buttonSpeak!!.isEnabled = false;
        tts = TextToSpeech(this, this)
        buttonSpeak!!.setOnClickListener { speakOut() }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        /* Text To Speech quiz */
        val startRecord = findViewById<ImageView>(R.id.buttonOn)
        val endRecord = findViewById<ImageView>(R.id.buttonOff)
        startRecord.visibility = View.GONE
        endRecord.visibility = View.VISIBLE

        /* Audio recorder */
        startRecord.setOnClickListener {
            File(cacheDir, "audio.mp3").also {
                recorder.start(it)
                audioFile = it
            }
            Toast.makeText(this@ReadVoiceAcitivity, "Mulai membaca !", Toast.LENGTH_SHORT).show()
            startRecord.visibility = View.GONE
            endRecord.visibility = View.VISIBLE
        }
        endRecord.setOnClickListener {
            recorder.stop()
            Toast.makeText(this@ReadVoiceAcitivity, "Stop membaca !", Toast.LENGTH_SHORT).show()
            startRecord.visibility = View.VISIBLE
            endRecord.visibility = View.GONE
        }

        continueButton.setOnClickListener{
            val data = Intent()
            data.putExtra("current_progress", "value1")
            setResult(Activity.RESULT_OK, data)
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

    /* Text to speech */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                buttonSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    /* Speech */
    private fun speakOut() {
        val text = instructionText!!.text.toString()
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    /* Destroy Speech */
    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}