package com.example.myapplication.ha

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient

interface HomeAssistantApi {
    @GET("api/states/{entity_id}")
    suspend fun getEntityState(
        @Header("Authorization") token: String,
        @Path("entity_id") entityId: String
    ): EntityState
}

object HomeAssistantClient {
    const val BASE_URL = "http://10.0.0.44:8123/"
    const val TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI0YzcxODgxZTEwMzE0ZDIyYjZkYTkwODc2NWE5NzE4NCIsImlhdCI6MTc3NzIyMjYwMiwiZXhwIjoyMDkyNTgyNjAyfQ.Bf1nHQOkxBRl4TedjBTynby5tktSxeykTAxoUR0uFqo"
    const val ENTITY_ID = "media_player.playstation_5"

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder().build()

    val service: HomeAssistantApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(HomeAssistantApi::class.java)
    }
}
