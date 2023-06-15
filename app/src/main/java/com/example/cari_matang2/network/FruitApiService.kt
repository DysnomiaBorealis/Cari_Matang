package com.example.cari_matang2.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import com.example.cari_matang2.network.AnalyzeFruitResponse

interface FruitApiService {
    @Multipart
    @POST("predict")
    fun analyzeFruit(@Part image: MultipartBody.Part): Call<AnalyzeFruitResponse>
}