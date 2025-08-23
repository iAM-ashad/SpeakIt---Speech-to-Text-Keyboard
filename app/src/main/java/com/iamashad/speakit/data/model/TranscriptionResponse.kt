package com.iamashad.speakit.data.model

import com.squareup.moshi.Json


data class TranscriptionResponse(
    @Json(name = "text") val text: String?
)