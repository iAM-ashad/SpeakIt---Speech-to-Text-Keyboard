package com.iamashad.speakit.data.api

import com.iamashad.speakit.data.model.TranscriptionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApiService {
    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun transcribe(
        @Part("model") model: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("response_format") responseFormat: RequestBody? = null,
        @Part("language") language: RequestBody? = null
    ): TranscriptionResponse
}