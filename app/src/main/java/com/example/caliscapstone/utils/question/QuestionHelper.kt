package com.example.caliscapstone.utils.question

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.example.caliscapstone.utils.voice.TTSHelper

class QuestionHelper {
    companion object {
        private val correctFeedbackStringArray: Array<String> = arrayOf(
            "Hebat!", "Keren!", "Bagus!", "Luar biasa!", "Pintar!", "Cerdas!"
        )

        fun setLearningProgressResultAndFinish(
            activity: AppCompatActivity, isCorrectArray: ArrayList<Boolean>, questionId: String, textToSpeech: TextToSpeech
        ) {
            val intent = Intent()
            intent.putExtra("isCorrect_array", isCorrectArray.toBooleanArray())
            intent.putExtra("questionId", questionId)
            activity.setResult(AppCompatActivity.RESULT_OK, intent)
            TTSHelper.speakTTS(textToSpeech, correctFeedbackStringArray.random())
            activity.finish()
        }
    }
}