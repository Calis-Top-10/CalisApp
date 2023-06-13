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
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.provider.MediaStore.Audio
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp


class CalisManualAudioClassifier(assetManager: AssetManager) {
    private var INTERPRETER_AUDIO: Interpreter
    private var LABEL_AUDIO_LIST: List<String>
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
        tfliteOptions.setUseNNAPI(false)
        INTERPRETER_AUDIO = Interpreter(loadModelFile(assetManager, "audio.tflite"), tfliteOptions)
        LABEL_AUDIO_LIST = loadLabelList(assetManager, "audio_labels.txt")
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

    fun isAnswersCorrect(audioRecord: AudioRecord, expectedAnswers: List<String>): Boolean {
        for (expectedAnswer in expectedAnswers) {
            if (isAnswerCorrect(audioRecord, expectedAnswer)) {
                return true
            }
        }
        return false
    }

    fun isAnswerCorrect(audioRecord: AudioRecord, expectedAnswer: String): Boolean {
        val result = recognizeAudio(audioRecord)
        Log.d("CalisManualAudioClassifier", "Result: $result")
        return result[0].title == expectedAnswer
    }

    fun recognizeAudio(audioRecord: AudioRecord): List<Recognition> {
        // FIXME: The sampleCount for tensorAudio is incorrect
        // The sampleCount is bufferSizeInframes * 1 because it is MONO
        // FIXME: Get the label mapping from dwiky's colab on the pandas get unique map
        val tensorAudio = TensorAudio.create(audioRecord.format, audioRecord.bufferSizeInFrames)
        // The value is already normalized
        tensorAudio.load(audioRecord)

        Log.d("INPUTTENSORSHAPE", tensorAudio.tensorBuffer.shape.contentToString())
        // Need to resize the input based on the length of the input tensor
        // FIXME: Need to see if the preprocessing or the label list is the problem
        INTERPRETER_AUDIO.resizeInput(
            INTERPRETER_AUDIO.getInputTensor(0).index(),
            IntArray(1) {tensorAudio.tensorBuffer.shape[1]})
        INTERPRETER_AUDIO.allocateTensors()

        Log.d("RECOGNIZEAUDIO", "tensorAudio: ${tensorAudio.tensorBuffer.buffer.capacity()  }")

        val byteBuffer = tensorAudio.tensorBuffer.buffer

        val result = FloatArray(LABEL_AUDIO_LIST.size)

//        val inputDetails = interpreterToUse.getInputTensor(0)
//        println("Input details:")
//        println("Name: ${inputDetails.name()}")
//        println("Shape: ${inputDetails.shape().contentToString()}")
//        println("Data type: ${inputDetails.dataType()}")
//        println()

        INTERPRETER_AUDIO.run(byteBuffer, result)
        val sortedResult = getSortedResult(result, LABEL_AUDIO_LIST)
        Log.d("CalisManualAudioClassifier", "Result: $sortedResult")
        return sortedResult
    }

    /** Writes Image data into a `ByteBuffer`.  */
    private fun convertAudioToByteBuffer(audioFilePath: String): ByteBuffer {
        // Resize the bitmap first to 300x300

        // Preprocess the image
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
            .add(TransformToGrayscaleOp())
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage = imageProcessor.process(tensorImage)

        return tensorImage.buffer
    }

    private fun getSortedResult(labelProbArray: FloatArray, labelList: List<String>): List<Recognition> {
        // TODO: Make the LABEL_LIST customizable
        Log.d("Classifier", "List Size:(%d, %d, %d)".format(labelProbArray.size, labelProbArray.size, labelList.size))

        val pq = PriorityQueue(
            MAX_RESULTS,
            Comparator<Recognition> { (_, _, confidence1), (_, _, confidence2)
                ->
                java.lang.Float.compare(confidence1, confidence2) * -1
            })

        for (i in labelList.indices) {
            val confidence = labelProbArray[i]
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