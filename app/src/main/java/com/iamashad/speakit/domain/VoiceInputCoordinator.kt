package com.iamashad.speakit.domain

import com.iamashad.speakit.data.repository.AudioRepository
import com.iamashad.speakit.data.repository.TranscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceInputCoordinator @Inject constructor(
    private val audioRepo: AudioRepository,
    private val transcriptionRepo: TranscriptionRepository,
) {
    suspend fun onPressDownStartRecording(): Result<File> = withContext(Dispatchers.IO) {
        runCatching { audioRepo.startRecording() }
    }


    suspend fun onReleaseStopAndTranscribe(language: String? = null): Result<String> =
        withContext(Dispatchers.IO) {
            val recorded = audioRepo.stopRecording()
            if (recorded == null) Result.failure(IllegalStateException("No active recording"))
            else transcriptionRepo.transcribeFile(recorded, language)
        }


    fun cancel() {
        audioRepo.cancelRecording()
    }
}