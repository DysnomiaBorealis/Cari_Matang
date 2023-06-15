package com.example.cari_matang2.network

import com.google.gson.annotations.SerializedName

data class AnalyzeFruitResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("fruit")
    val fruit: String,

    @SerializedName("condition")
    val condition: String
)
