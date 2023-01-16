package com.example.studapp.utils

import com.google.gson.annotations.SerializedName

data class LocationObject (
    @SerializedName("type") val type: String,
    @SerializedName("coordinates") val coordinates: ArrayList<Double> = ArrayList()
)