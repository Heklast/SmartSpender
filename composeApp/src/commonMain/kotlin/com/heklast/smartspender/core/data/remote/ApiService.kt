package com.heklast.smartspender.core.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getRandomAdvice(): String {
        val response: AdviceResponse = client.get("https://api.adviceslip.com/advice").body()
        return response.slip.advice
    }
}

@Serializable
data class AdviceResponse(val slip: Slip)

@Serializable
data class Slip(val id: Int, val advice: String)
