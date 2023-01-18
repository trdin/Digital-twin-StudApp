package com.example.studapp.utils

import com.google.gson.annotations.SerializedName
import java.util.*

data class SerializableMessageObject (
    @SerializedName("category") val category: String,
    @SerializedName("content") val content: String,
    @SerializedName("time") val time: Date,
    @SerializedName("location") val location: LocationObject
    )