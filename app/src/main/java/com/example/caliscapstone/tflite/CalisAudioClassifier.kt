// TODO:
// 1. Copy class ini lalu rename jadi AudioClassifier
// 2. Hapus interpreter_small, dkk, jadiin cuma 1 interpreter
// 3. Hapus LABEL_SMALL_LIST, dkk, jadiin cuma 1 label list
// 4. Buat file label audio_labels.txt isi 18 baris
// 5. Ubah fungsi recognizeImage jadi recognizeAudio
// 6. Ubah fungsi convertBitmapToByteBuffer jadi convertAudioToByteBuffer
// 7.

package com.example.caliscapstone.tflite

import android.content.Context
import android.content.res.AssetManager
import android.media.AudioRecord
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.AudioClassifier.AudioClassifierOptions
import org.tensorflow.lite.task.audio.classifier.Classifications
import org.tensorflow.lite.task.core.BaseOptions
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*


class CalisAudioClassifier(assetManager: AssetManager, context: Context) {
    private var MODEL_FILE_PATH: String = "audio.tflite"
    private var record: AudioRecord;
    private var classifier: AudioClassifier;

    data class Recognition(
        var id: String = "",
        var title: String = "",
        var confidence: Float = 0F
    ) {
        override fun toString(): String {
            return "Title = $title, Confidence = $confidence)"
        }
    }

    init {
        val options = AudioClassifierOptions.builder()
            .setBaseOptions(BaseOptions.builder().build())
            .setMaxResults(1)
            .build()
        classifier = AudioClassifier.createFromFileAndOptions(context, MODEL_FILE_PATH, options)

        record = classifier.createAudioRecord()

    }

    fun startRecording() {
        record.startRecording()
    }

    fun stopRecordingAndClassify(): List<Classifications> {
        val audioTensor = classifier.createInputTensorAudio()
        audioTensor.load(record)
        record.stop()
        val results: List<Classifications> = classifier.classify(audioTensor)

        Log.d("CalisAudioClassifier", "stopRecordingAndClassify: $results")

        return results;
    }

//    fun isAnswerCorrect(audioFilePath: String, expectedAnswer: String): Boolean {
//        val result = recognizeAudio(audioFilePath)
//        return result[0].title == expectedAnswer
//    }
}