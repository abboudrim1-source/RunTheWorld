package com.runtheworld.server

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ProfileSyncRequest(
    val uid: String,
    val username: String,
    val displayName: String,
    val colorHex: String,
    val totalAreaKm2: Double,
    val runCount: Int
)

@Serializable
data class RunSyncRequest(
    val id: String,
    val userId: String,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val areaKm2: Double
)

fun main() {
    // Port 8080 on 0.0.0.0 means "Listen to everyone"
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }
    
    DatabaseFactory.init()

    routing {
        get("/") {
            call.respondText("Run The World Server is Online!")
        }

        post("/profiles") {
            try {
                val req = call.receive<ProfileSyncRequest>()
                println("SERVER: Received profile for ${req.username} (${req.uid})")
                transaction {
                    Profiles.upsert {
                        it[uid] = req.uid
                        it[username] = req.username
                        it[displayName] = req.displayName
                        it[colorHex] = req.colorHex
                        it[totalAreaKm2] = req.totalAreaKm2
                        it[runCount] = req.runCount
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("status" to "synced"))
            } catch (e: Exception) {
                application.log.error("Profile sync failed", e)
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
            }
        }

        get("/profiles") {
            val allProfiles = transaction {
                Profiles.selectAll().map {
                    ProfileSyncRequest(
                        uid = it[Profiles.uid],
                        username = it[Profiles.username],
                        displayName = it[Profiles.displayName],
                        colorHex = it[Profiles.colorHex],
                        totalAreaKm2 = it[Profiles.totalAreaKm2],
                        runCount = it[Profiles.runCount]
                    )
                }
            }
            call.respond(allProfiles)
        }

        get("/leaderboard") {
            val leaderboard = transaction {
                Profiles.selectAll()
                    .orderBy(Profiles.totalAreaKm2 to SortOrder.DESC)
                    .limit(10)
                    .map {
                        mapOf(
                            "username" to it[Profiles.username],
                            "area" to it[Profiles.totalAreaKm2],
                            "runs" to it[Profiles.runCount],
                            "color" to it[Profiles.colorHex]
                        )
                    }
            }
            call.respond(leaderboard)
        }
    }
}
