package com.iamashad.speakit.data.repository


import com.iamashad.speakit.BuildConfig
import com.iamashad.speakit.data.api.WhisperApiService
import com.iamashad.speakit.data.model.TranscriptionResponse
import com.iamashad.speakit.util.MultipartExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface TranscriptionRepository {
    suspend fun transcribeFile(file: File, language: String? = null): Result<String>
}

@Singleton
class TranscriptionRepositoryImpl @Inject constructor(
    private val api: WhisperApiService
) : TranscriptionRepository {


    override suspend fun transcribeFile(file: File, language: String?): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val model: RequestBody = MultipartExt.model(BuildConfig.WHISPER_MODEL)
                val filePart: MultipartBody.Part = MultipartExt.filePart("file", file)
                val response: TranscriptionResponse = api.transcribe(
                    model = model,
                    file = filePart,
                    responseFormat = MultipartExt.plain("json"),
                    language = language?.let { MultipartExt.plain(it) }
                )
                response.text?.ifBlank { null } ?: error("Empty transcription text")
            }
        }
}