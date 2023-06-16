package com.example.caliscapstone.utils.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSHelper {
    // create a static attribute called tts
    companion object {
        private var tts: TextToSpeech? = null

        fun createTTS(context: Context): TextToSpeech {
            val onInitListener = TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.SUCCESS) {
                    Log.e("TTS", "Initilization Failed!")
                }
                // Set indonesia as language
                val result = tts!!.setLanguage(Locale.forLanguageTag("id"))

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language specified is not supported!")
                }
                tts!!.setSpeechRate(0.7f)
            }
            tts =  TextToSpeech(context, onInitListener)

            return tts as TextToSpeech
        }

        fun speakTTS(tts: TextToSpeech, text: String) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
        }
    }
}