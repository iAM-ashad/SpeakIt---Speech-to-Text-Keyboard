package com.iamashad.speakit.data.recorder


import android.media.MediaRecorder
import java.io.File
import javax.inject.Inject


class AudioRecorderFactoryImpl @Inject constructor() : AudioRecorderFactory {
    override fun create(outputFile: File): MediaRecorder {
        return MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128_000)
            setAudioSamplingRate(44_100)
            setOutputFile(outputFile.absolutePath)
        }
    }
}