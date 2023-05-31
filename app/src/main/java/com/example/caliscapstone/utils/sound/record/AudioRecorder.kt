package com.example.caliscapstone.utils.sound.record

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}