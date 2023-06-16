package com.example.caliscapstone.ui.activity.dashboard.learning.type

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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
import com.example.caliscapstone.tflite.CalisManualAudioClassifier
import com.example.caliscapstone.ui.activity.login.LoginActivity
import com.example.caliscapstone.utils.voice.TTSHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import java.util.Locale


class ReadVoiceAcitivity : AppCompatActivity()   {
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private var tts: TextToSpeech? = null
    private var buttonSpeak: ImageView? = null
    private var instructionText: TextView? = null
    private lateinit var questionList: QuestionDetails

    private lateinit var calisAudioClassifier: CalisManualAudioClassifier;

    // Audio Interface
    private lateinit var audioRecord: AudioRecord

    private fun initAudioRecord() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            /* Sound Permissions */
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                0
            )
        }
        audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(16000)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build())
            .setBufferSizeInBytes(AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT))
            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_voice_acitivity)
        instructionText = findViewById(R.id.textQuiz)
        buttonSpeak = findViewById(R.id.instruction)
        val continueButton = findViewById<FrameLayout>(R.id.continueButton)

        tts = TTSHelper.createTTS(this)

        /* Audio classifier */
        initAudioRecord()
        calisAudioClassifier = CalisManualAudioClassifier(assets)

        /* Backward Navigation */
        val backwardPage = findViewById<ImageView>(R.id.backward)
        backwardPage?.setOnClickListener {
            intent = Intent(this@ReadVoiceAcitivity, HomeLessonActivity::class.java)
                .putExtra("read_hover", "baca")
            startActivity(intent)
        }

        /* Progress Bar Value */
        val progressBarValue = intent.getIntExtra("progrees_bar_value", 0)
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
        val account: GoogleSignInAccount? = GoogleSignIn
            .getLastSignedInAccount(this)
        if (account == null) {
            goSignOut()
        }

        /* Text To Speech quiz */
        buttonSpeak!!.isEnabled = false;
        buttonSpeak!!.setOnClickListener { speakOut() }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        /* Text To Speech quiz */
        val startRecord = findViewById<ImageView>(R.id.buttonOn)
        val endRecord = findViewById<ImageView>(R.id.speakQuestion)
        startRecord.visibility = View.GONE
        endRecord.visibility = View.VISIBLE

        /* Audio recorder */
        startRecord.setOnClickListener {
            Log.d("STARTRECORD", "Start record clicked")
            // FIXME: endRecord is is clicked instead of this startRecord
            audioRecord.startRecording()
            Toast.makeText(this@ReadVoiceAcitivity, "Stop membaca !", Toast.LENGTH_SHORT).show()
            startRecord.visibility = View.GONE
            endRecord.visibility = View.VISIBLE
        }

        endRecord.setOnClickListener {
            audioRecord.stop()
            calisAudioClassifier.isAnswerCorrect(audioRecord, "muak")
            Toast.makeText(this@ReadVoiceAcitivity, "Mulai membaca !", Toast.LENGTH_SHORT).show()
            startRecord.visibility = View.VISIBLE
            endRecord.visibility = View.GONE
            // TODO: Call the function below to release resources
            // audioRecord.release()
        }

        continueButton.setOnClickListener {
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

    /* Speech */
    private fun speakOut() {
        if (instructionText == null){
            Log.e("TTS", "instruction text shouldnt be null")
        }
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