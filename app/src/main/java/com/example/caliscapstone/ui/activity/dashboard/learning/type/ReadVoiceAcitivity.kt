package com.example.caliscapstone.ui.activity.dashboard.learning.type

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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

    // Audio Interface
    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }
    private var audioFile: File? = null

        private lateinit var questionList: QuestionDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_voice_acitivity)
        buttonSpeak = findViewById(R.id.instruction)

        Log.d("ResponseQuestion", intent.getSerializableExtra("intent_question").toString())
        Log.d("CurrentIndex", intent.getSerializableExtra("current_question_index").toString())
        Log.d("ProgressBarValue", intent.getSerializableExtra("progrees_bar_value").toString())

        questionList = intent.getSerializableExtra("ini_nyoba") as QuestionDetails

        /* Qyestion Box */
        val questionBox = findViewById<TextView>(R.id.textQuiz)
        questionBox.text = questionList.question
        Log.d("testIndex", questionList.toString())


        /* Progress Bar Value */
        val progressBarValue =  intent.getIntExtra("progrees_bar_value", 0)
        val progressBarHorizontal = findViewById<ProgressBar>(R.id.progressBarz)
        progressBarHorizontal.progress = progressBarValue

        /* Question */
/*
        val questionBox = findViewById<TextView>(R.id.textQuiz)

        questionBox.text = question.questionDetails.question

 */

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )

        val backwardPage = findViewById<ImageView>(R.id.backward)
        /* Backward Navigation */
        backwardPage?.setOnClickListener{
            intent = Intent(this@ReadVoiceAcitivity, HomeLessonActivity::class.java)
                .putExtra("read_hover", "baca")
            startActivity(intent)
        }

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
            startRecord.visibility = View.GONE
            endRecord.visibility = View.VISIBLE
        }
        endRecord.setOnClickListener {
            recorder.stop()
            startRecord.visibility = View.VISIBLE
            endRecord.visibility = View.GONE

            val data = Intent()
            data.putExtra("current_progress", "value1")
            setResult(Activity.RESULT_OK, data)
            finish()
        }
        /*
                val playRecord = findViewById<Button>(R.id.playRecord)
                val questionList = intent.getSerializableExtra("intent_question") as ArrayList<Question>
                Log.d("QuestionListTest", questionList.toString())

                /* Sound Permissions */
                val buttonSpeak = findViewById<ImageView>(R.id.instruction)
                val progressBarHorizontal = findViewById<ProgressBar>(R.id.progressBarz)
                buttonSpeak.setOnClickListener {
                    currentProgress += 10
                    progressBarHorizontal.progress = currentProgress

                }
        playRecord.setOnClickListener {
            audioFile?.let { it1 -> player.playFile(it1) }
        }
        */

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