package com.runtheworld.data.repository

import com.russhwolf.settings.Settings
import com.runtheworld.domain.model.Run
import com.runtheworld.domain.repository.RunSyncRepository
import com.runtheworld.util.AppResult
import com.runtheworld.util.appRunCatching
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class RunUploadRequest(
    val id: String,
    val userId: String,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val areaKm2: Double,
    val score: Int
)

class KtorRunSyncRepositoryImpl(
    private val httpClient: HttpClient,
    private val settings: Settings
) : RunSyncRepository {

    private val baseUrl = com.runtheworld.data.network.NetworkConfig.BASE_URL

    override suspend fun syncRun(run: Run): AppResult<Unit> = appRunCatching {
        val uid = settings.getStringOrNull("auth_uid")
            ?: throw Exception("Not authenticated — cannot sync run")
        val response = httpClient.post("$baseUrl/runs") {
            contentType(ContentType.Application.Json)
            setBody(RunUploadRequest(
                id = run.id,
                userId = uid,
                distanceMeters = run.distanceMeters,
                durationSeconds = run.durationSeconds,
                areaKm2 = run.areaKm2,
                score = run.score
            ))
        }
        if (!response.status.isSuccess()) {
            throw Exception("Server error ${response.status.value}: ${response.bodyAsText()}")
        }
    }
}
