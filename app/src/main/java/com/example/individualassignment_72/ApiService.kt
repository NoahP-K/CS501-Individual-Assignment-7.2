package com.example.individualassignment_72

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.http.Path

interface ApiService {
    @GET("users/{login}/repos")
    suspend fun getAccount(
        @Path("login") login: String,
        @Query("page") offset: Int,
        @Query("per_page") limit: Int
    ): List<Repo>
}

object ApiClient {
    private const val BASE_URL = "https://api.github.com/"
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Explicitly add KotlinJsonAdapterFactory
        .build() // Build the Moshi instance


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}