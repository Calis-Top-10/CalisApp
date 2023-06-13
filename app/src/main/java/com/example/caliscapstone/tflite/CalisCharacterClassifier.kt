// TODO:
// 1. Copy class ini lalu rename jadi AudioClassifier
// 2. Hapus interpreter_small, dkk, jadiin cuma 1 interpreter
// 3. Hapus LABEL_SMALL_LIST, dkk, jadiin cuma 1 label list
// 4. Buat file label audio_labels.txt isi 18 baris
// 5. Ubah fungsi recognizeImage jadi recognizeAudio
// 6. Ubah fungsi convertBitmapToByteBuffer jadi convertAudioToByteBuffer
// 7.

package com.example.caliscapstone.tflite

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.media.AudioRecord
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp


class CalisCharacterClassifier(assetManager: AssetManager) {
    private var INTERPRETER_SMALL: Interpreter
    private var INTERPRETER_CAPITAL: Interpreter
    private var INTERPRETER_DIGIT: Interpreter
    private var LABEL_SMALL_LIST: List<String>
    private var LABEL_CAPITAL_LIST: List<String>
    private var LABEL_DIGIT_LIST: List<String>
    private val INPUT_SIZE: Int = 300
    private val PIXEL_SIZE: Int = 4
    private val IMAGE_MEAN = 0
    private val IMAGE_STD = 255.0f
    private val MAX_RESULTS = 1
    private val THRESHOLD = 0.0f

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
        val tfliteOptions = Interpreter.Options()
        tfliteOptions.setNumThreads(5)
        tfliteOptions.setUseNNAPI(true)
        INTERPRETER_SMALL = Interpreter(loadModelFile(assetManager, "small_char.tflite"),tfliteOptions)
        INTERPRETER_CAPITAL = Interpreter(loadModelFile(assetManager, "capital_char.tflite"),tfliteOptions)
        INTERPRETER_DIGIT = Interpreter(loadModelFile(assetManager, "digit.tflite"),tfliteOptions)
        LABEL_SMALL_LIST = loadLabelList(assetManager, "small_labels.txt")
        LABEL_CAPITAL_LIST = loadLabelList(assetManager, "capital_labels.txt")
        LABEL_DIGIT_LIST = loadLabelList(assetManager, "digit_labels.txt")
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabelList(assetManager: AssetManager, labelPath: String): List<String> {
        return assetManager.open(labelPath).bufferedReader().useLines { it.toList() }
    }

    fun isAnswersCorrect(answerBitmap: Bitmap, expectedAnswers: List<String>): Boolean {
        for (expectedAnswer in expectedAnswers) {
            var modelType: String = if (expectedAnswer[0].isUpperCase()) {
                "capital"
            } else if (expectedAnswer[0].isLowerCase()) {
                "small"
            } else if (expectedAnswer[0].isDigit()) {
                "digit"
            } else {
                throw Exception("Expected answer is not an alphabet/digit")
            }

            if (isAnswerCorrect(answerBitmap, modelType, expectedAnswer)) {
                return true
            }
        }
        return false
    }

    fun isAnswerCorrect(answerBitmap: Bitmap, modelType: String, expectedAnswer: String): Boolean {
        val result = recognizeImage(answerBitmap, modelType)
        return result[0].title == expectedAnswer
    }

    fun recognizeImage(bitmap: Bitmap, modelType: String): List<Recognition> {
        // TODO: Make the target parameter as the distinguisher for "small" "capital" and "digit"
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
        val byteBuffer = convertBitmapToByteBuffer(scaledBitmap)

        val interpreterToUse: Interpreter
        val labelListToUse: List<String>

        when (modelType) {
            "small" -> {
                // TODO: Also set the label list path
                interpreterToUse = INTERPRETER_SMALL
                labelListToUse = LABEL_SMALL_LIST
            }
            "capital" -> {
                interpreterToUse = INTERPRETER_CAPITAL
                labelListToUse = LABEL_CAPITAL_LIST
            }
            "digit" -> {
                interpreterToUse = INTERPRETER_DIGIT
                labelListToUse = LABEL_DIGIT_LIST
            }
            else -> throw Exception("Acceptable targets: small, capital, digit")
        }

        val result = Array(1) { FloatArray(labelListToUse.size) }

//        val inputDetails = interpreterToUse.getInputTensor(0)
//        println("Input details:")
//        println("Name: ${inputDetails.name()}")
//        println("Shape: ${inputDetails.shape().contentToString()}")
//        println("Data type: ${inputDetails.dataType()}")
//        println()

        interpreterToUse.run(byteBuffer, result)
        val sortedResult = getSortedResult(result, labelListToUse)
        Log.d("CharacterClassifier", "Result: $sortedResult")
        return sortedResult
    }

    /** Writes Image data into a `ByteBuffer`.  */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Resize the bitmap first to 300x300
        var bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)

        // Preprocess the image
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .add(TransformToGrayscaleOp())
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        return tensorImage.buffer
    }

    private fun getSortedResult(labelProbArray: Array<FloatArray>, labelList: List<String>): List<Recognition> {
        // TODO: Make the LABEL_LIST customizable
        Log.d("Classifier", "List Size:(%d, %d, %d)".format(labelProbArray.size, labelProbArray[0].size, labelList.size))

        val pq = PriorityQueue(
            MAX_RESULTS,
            Comparator<Recognition> { (_, _, confidence1), (_, _, confidence2)
                ->
                java.lang.Float.compare(confidence1, confidence2) * -1
            })

        for (i in labelList.indices) {
            val confidence = labelProbArray[0][i]
            Log.d("Classifier", "labelPropArray:(%d, %d, %f)".format(0, i, confidence))
            if (confidence >= THRESHOLD) {
                Log.d("confidence value:", "" + confidence);
                pq.add(Recognition("" + i,
                    if (labelList.size > i) labelList[i] else "Unknown",
                    confidence
                ))
            }
        }
        Log.d("Classifier", "pqsize:(%d)".format(pq.size))

        val recognitions = ArrayList<Recognition>()
        val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }
        return recognitions
    }
}