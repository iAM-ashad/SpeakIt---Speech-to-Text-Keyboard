package com.iamashad.speakit.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


object MultipartExt {
    private val MEDIA_TYPE_M4A = "audio/m4a".toMediaType()


    fun model(name: String) = name.toRequestBody("text/plain".toMediaType())
    fun plain(name: String) = name.toRequestBody("text/plain".toMediaType())


    fun filePart(paramName: String, file: File): MultipartBody.Part {
        val body: RequestBody = file.asRequestBody(MEDIA_TYPE_M4A)
        return MultipartBody.Part.createFormData(paramName, file.name, body)
    }
}