package com.example.caliscapstone.utils.sound.playback

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}