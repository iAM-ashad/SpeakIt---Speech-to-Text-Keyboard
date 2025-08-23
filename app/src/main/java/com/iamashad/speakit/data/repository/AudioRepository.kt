package com.iamashad.speakit.data.repository

import android.content.Context
import android.media.MediaRecorder
import com.iamashad.speakit.data.recorder.AudioRecorderFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

interface AudioRepository {
    @Throws(IllegalStateException::class)
    fun startRecording(): File

    fun stopRecording(): File?

    fun cancelRecording()

    fun isRecording(): Boolean
}

@Singleton
class AudioRepositoryImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val factory: AudioRecorderFactory,
) : AudioRepository {

    private val recorderRef = AtomicReference<MediaRecorder?>(null)
    private val fileRef = AtomicReference<File?>(null)

    override fun startRecording(): File {

        check(recorderRef.get() == null) { "A recording session is already active." }
        val outFile = newAudioFile()
        val recorder = factory.create(outFile)
        recorderRef.set(recorder)
        fileRef.set(outFile)

        try {
            recorder.prepare()
            recorder.start()
        } catch (t: Throwable) {
            try {
                recorder.reset()
            } catch (_: Throwable) {
            }
            recorder.releaseSafely()
            fileRef.getAndSet(null)?.delete()
            throw IllegalStateException("Failed to start recording", t)
        }
        return outFile
    }


    override fun stopRecording(): File? {
        val recorder = recorderRef.getAndSet(null) ?: return null
        val file = fileRef.getAndSet(null)
        return try {
            recorder.stop()
            recorder.releaseSafely()
            file
        } catch (_: Throwable) {
            recorder.releaseSafely()
            file?.delete()
            null
        }
    }

    override fun cancelRecording() {
        val recorder = recorderRef.getAndSet(null) ?: return
        val file = fileRef.getAndSet(null)
        try {
            recorder.reset()
        } catch (_: Throwable) {
        }
        recorder.releaseSafely()
        file?.delete()
    }

    override fun isRecording(): Boolean = recorderRef.get() != null

    private fun newAudioFile(): File {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(appContext.cacheDir, "rec_$stamp.m4a")
    }
}

private fun MediaRecorder.releaseSafely() {
    try {
        release()
    } catch (_: Throwable) {
    }
}
