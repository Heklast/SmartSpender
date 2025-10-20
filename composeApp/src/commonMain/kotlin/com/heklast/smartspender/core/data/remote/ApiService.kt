package com.heklast.smartspender.core.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.datetime.Clock

class ApiService {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) { level = LogLevel.INFO }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }

    suspend fun getRandomAdvice(): String {
        val url = "https://api.adviceslip.com/advice?ts=${Clock.System.now().toEpochMilliseconds()}"

        val respText = client.get(url) {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.CacheControl, "no-cache")
            header("X-Requested-With", "XMLHttpRequest")
        }.bodyAsText() //read as raw text to dodge text/html responses

        //try json parse first
        return try {
            json.decodeFromString(AdviceResponse.serializer(), respText).slip.advice
        } catch (_: Exception) {
            Regex("\"advice\"\\s*:\\s*\"([^\"]+)\"")
                .find(respText)?.groupValues?.get(1)
                ?: "Failed to fetch advice."
        }
    }
}

@Serializable data class AdviceResponse(val slip: Slip)
@Serializable data class Slip(val id: Int, val advice: String)