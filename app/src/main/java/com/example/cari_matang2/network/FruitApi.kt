package com.example.cari_matang2.network

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import com.example.cari_matang2.network.AnalyzeFruitResponse

class FruitApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://carimatangfinal-smmif3gy7a-uc.a.run.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(FruitApiService::class.java)

    fun analyzeFruit(imageFilePath: String, callback: Callback<AnalyzeFruitResponse>) {
        val imageFile = File(imageFilePath)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile)
        val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        val call = service.analyzeFruit(body)

        call.enqueue(callback)
    }
}