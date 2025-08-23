package com.iamashad.speakit.data.recorder


import android.media.MediaRecorder
import java.io.File

interface AudioRecorderFactory {
    fun create(outputFile: File): MediaRecorder
}