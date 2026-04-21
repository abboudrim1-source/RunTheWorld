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
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import java.security.SecureRandom

// ── Request / response models ──────────────────────────────────────────────────

@Serializable
data class ProfileSyncRequest(
    val uid: String,
    val username: String,
    val displayName: String,
    val colorHex: String,
    val totalAreaKm2: Double,
    val runCount: Int,
    val avatarBase64: String? = null,
    val city: String? = null
)

@Serializable
data class LeaderboardEntryDto(
    val username: String,
    val area: Double,
    val runs: Int,
    val score: Int,
    val color: String,
    val city: String?
)

@Serializable
data class RunSyncRequest(
    val id: String,
    val userId: String,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val areaKm2: Double,
    val score: Int = 0
)

@Serializable
data class SendFriendRequestBody(
    val senderUid: String,
    val receiverUid: String
)

@Serializable
data class FriendRequestResponse(
    val id: String,
    val senderUid: String,
    val senderUsername: String,
    val senderDisplayName: String,
    val senderColorHex: String,
    val status: String,
    val createdAt: Long
)

@Serializable data class PolygonPoint(val lat: Double, val lng: Double)

@Serializable data class TerritoryUploadRequest(
    val id: String,
    val userId: String,
    val ownerUsername: String,
    val ownerColorHex: String,
    val polygon: List<PolygonPoint>,
    val claimedAt: Long,
    val areaKm2: Double
)

@Serializable data class SignUpRequest(val email: String, val password: String, val displayName: String? = null)
@Serializable data class SignInRequest(val email: String, val password: String)
@Serializable data class GoogleSignInRequest(val googleId: String, val email: String, val displayName: String? = null)
@Serializable data class AuthResponse(val uid: String, val email: String, val displayName: String?)

private fun sha256(input: String): String =
    MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }

private fun generateSalt(): String =
    ByteArray(16).also { SecureRandom().nextBytes(it) }
        .joinToString("") { "%02x".format(it) }

private fun hashPassword(password: String, salt: String) = sha256(password + salt)

private fun randomUid(): String =
    ByteArray(16).also { SecureRandom().nextBytes(it) }
        .joinToString("") { "%02x".format(it) }

// ── Entry point ────────────────────────────────────────────────────────────────

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) { json() }
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }

    DatabaseFactory.init()

    routing {

        // ── Health ─────────────────────────────────────────────────────────────

        get("/") {
            call.respondText("Run The World Server is Online!")
        }

        // ── Auth ───────────────────────────────────────────────────────────────

        get("/auth/verify/{uid}") {
            val uid = call.parameters["uid"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val exists = transaction { Accounts.selectAll().where { Accounts.uid eq uid }.firstOrNull() } != null
            if (exists) call.respond(HttpStatusCode.OK, mapOf("valid" to true))
            else call.respond(HttpStatusCode.NotFound, mapOf("valid" to false))
        }

        post("/auth/signup") {
            try {
                val req = call.receive<SignUpRequest>()
                val email = req.email.lowercase().trim()
                val existing = transaction { Accounts.selectAll().where { Accounts.email eq email }.firstOrNull() }
                if (existing != null) return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "An account with this email already exists"))
                val salt = generateSalt()
                val uid  = randomUid()
                transaction {
                    Accounts.insert {
                        it[Accounts.uid]          = uid
                        it[Accounts.email]        = email
                        it[Accounts.displayName]  = req.displayName
                        it[Accounts.passwordHash] = hashPassword(req.password, salt)
                        it[Accounts.salt]         = salt
                        it[Accounts.loginType]    = "email"
                    }
                }
                call.respond(AuthResponse(uid = uid, email = email, displayName = req.displayName))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "error")))
            }
        }

        post("/auth/signin") {
            try {
                val req = call.receive<SignInRequest>()
                val email = req.email.lowercase().trim()
                val account = transaction { Accounts.selectAll().where { Accounts.email eq email }.firstOrNull() }
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "No account found with that email"))
                if (account[Accounts.loginType] != "email")
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "This email is linked to a Google account"))
                val hash = hashPassword(req.password, account[Accounts.salt]!!)
                if (hash != account[Accounts.passwordHash])
                    return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Incorrect password"))
                call.respond(AuthResponse(uid = account[Accounts.uid], email = email, displayName = account[Accounts.displayName]))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "error")))
            }
        }

        post("/auth/google") {
            try {
                val req = call.receive<GoogleSignInRequest>()
                val account = transaction {
                    Accounts.selectAll().where { Accounts.uid eq req.googleId }.firstOrNull()
                        ?: run {
                            Accounts.insert {
                                it[uid]         = req.googleId
                                it[email]       = req.email.lowercase()
                                it[displayName] = req.displayName
                                it[loginType]   = "google"
                            }
                            Accounts.selectAll().where { Accounts.uid eq req.googleId }.first()
                        }
                }
                call.respond(AuthResponse(uid = account[Accounts.uid], email = account[Accounts.email], displayName = account[Accounts.displayName]))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "error")))
            }
        }

        // ── Admin ──────────────────────────────────────────────────────────────

        delete("/profiles/{uid}") {
            val uid = call.parameters["uid"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            transaction { Profiles.deleteWhere { Profiles.uid eq uid } }
            call.respond(HttpStatusCode.OK, mapOf("status" to "deleted"))
        }

        // ── Profiles ───────────────────────────────────────────────────────────

        post("/profiles") {
            try {
                val req = call.receive<ProfileSyncRequest>()
                val takenBy = transaction {
                    Profiles.selectAll().where { Profiles.username eq req.username }.singleOrNull()
                }
                if (takenBy != null && takenBy[Profiles.uid] != req.uid) {
                    return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "Runner name already taken"))
                }
                transaction {
                    val exists = Profiles.selectAll().where { Profiles.uid eq req.uid }.firstOrNull()
                    if (exists == null) {
                        Profiles.insert {
                            it[uid]          = req.uid
                            it[username]     = req.username
                            it[displayName]  = req.displayName
                            it[colorHex]     = req.colorHex
                            it[totalAreaKm2] = req.totalAreaKm2
                            it[runCount]     = req.runCount
                            it[totalScore]   = 0
                            if (req.avatarBase64 != null) it[avatarBase64] = req.avatarBase64
                            if (req.city != null) it[city] = req.city
                        }
                    } else {
                        Profiles.update({ Profiles.uid eq req.uid }) {
                            it[username]     = req.username
                            it[displayName]  = req.displayName
                            it[colorHex]     = req.colorHex
                            it[totalAreaKm2] = req.totalAreaKm2
                            it[runCount]     = req.runCount
                            if (req.avatarBase64 != null) it[avatarBase64] = req.avatarBase64
                            if (req.city != null) it[city] = req.city
                            // totalScore is intentionally not touched — updated only by run sync
                        }
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("status" to "synced"))
            } catch (e: Exception) {
                application.log.error("Profile sync failed", e)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "error")))
            }
        }

        // Search users by username (cross-device friend discovery)
        get("/profiles/search") {
            val query      = call.request.queryParameters["q"]?.trim() ?: ""
            val excludeUid = call.request.queryParameters["excludeUid"] ?: ""
            if (query.isBlank()) { call.respond(emptyList<ProfileSyncRequest>()); return@get }

            val results = transaction {
                Profiles.selectAll()
                    .where { (Profiles.username like "%$query%") and (Profiles.uid neq excludeUid) }
                    .limit(20)
                    .map { row ->
                        ProfileSyncRequest(
                            uid          = row[Profiles.uid],
                            username     = row[Profiles.username],
                            displayName  = row[Profiles.displayName],
                            colorHex     = row[Profiles.colorHex],
                            totalAreaKm2 = row[Profiles.totalAreaKm2],
                            runCount     = row[Profiles.runCount],
                            avatarBase64 = row[Profiles.avatarBase64]
                        )
                    }
            }
            call.respond(results)
        }

        get("/profiles") {
            val all = transaction {
                Profiles.selectAll().map { row ->
                    ProfileSyncRequest(
                        uid          = row[Profiles.uid],
                        username     = row[Profiles.username],
                        displayName  = row[Profiles.displayName],
                        colorHex     = row[Profiles.colorHex],
                        totalAreaKm2 = row[Profiles.totalAreaKm2],
                        runCount     = row[Profiles.runCount]
                    )
                }
            }
            call.respond(all)
        }

        get("/profiles/{uid}") {
            val uid = call.parameters["uid"] ?: return@get call.respond(HttpStatusCode.BadRequest, "uid required")
            val row = transaction { Profiles.selectAll().where { Profiles.uid eq uid }.singleOrNull() }
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Profile not found"))
            call.respond(ProfileSyncRequest(
                uid          = row[Profiles.uid],
                username     = row[Profiles.username],
                displayName  = row[Profiles.displayName],
                colorHex     = row[Profiles.colorHex],
                totalAreaKm2 = row[Profiles.totalAreaKm2],
                runCount     = row[Profiles.runCount],
                avatarBase64 = row[Profiles.avatarBase64],
                city         = row[Profiles.city]
            ))
        }

        // ── Territories ────────────────────────────────────────────────────────

        post("/territories") {
            try {
                val req = call.receive<TerritoryUploadRequest>()
                val encodedPolygon = Json.encodeToString(req.polygon)
                transaction {
                    Territories.upsert {
                        it[id]            = req.id
                        it[userId]        = req.userId
                        it[ownerUsername] = req.ownerUsername
                        it[ownerColorHex] = req.ownerColorHex
                        it[polygonJson]   = encodedPolygon
                        it[claimedAt]     = req.claimedAt
                        it[areaKm2]       = req.areaKm2
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("status" to "synced"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "error")))
            }
        }

        get("/territories/friends") {
            val uidsParam = call.request.queryParameters["uids"] ?: ""
            if (uidsParam.isBlank()) { call.respond(emptyList<TerritoryUploadRequest>()); return@get }
            val uids = uidsParam.split(",").filter { it.isNotBlank() }
            val results = transaction {
                Territories.selectAll()
                    .where { Territories.userId inList uids }
                    .map { row ->
                        val polygon = Json.decodeFromString<List<PolygonPoint>>(row[Territories.polygonJson])
                        TerritoryUploadRequest(
                            id            = row[Territories.id],
                            userId        = row[Territories.userId],
                            ownerUsername = row[Territories.ownerUsername],
                            ownerColorHex = row[Territories.ownerColorHex],
                            polygon       = polygon,
                            claimedAt     = row[Territories.claimedAt],
                            areaKm2       = row[Territories.areaKm2]
                        )
                    }
            }
            call.respond(results)
        }

        // ── Runs ───────────────────────────────────────────────────────────────

        post("/runs") {
            try {
                val req = call.receive<RunSyncRequest>()
                println("SERVER: run sync ${req.id} for ${req.userId}")
                val runScore = req.score.takeIf { it > 0 }
                    ?: kotlin.math.round(req.distanceMeters / 10.0).toInt()
                transaction {
                    Runs.upsert {
                        it[id]              = req.id
                        it[userId]          = req.userId
                        it[distanceMeters]  = req.distanceMeters
                        it[durationSeconds] = req.durationSeconds
                        it[areaKm2]         = req.areaKm2
                        it[score]           = runScore
                        it[createdAt]       = System.currentTimeMillis()
                    }
                    val row = Profiles.selectAll().where { Profiles.uid eq req.userId }.singleOrNull()
                    if (row != null) {
                        Profiles.update({ Profiles.uid eq req.userId }) {
                            it[totalAreaKm2] = row[Profiles.totalAreaKm2] + req.areaKm2
                            it[runCount]     = row[Profiles.runCount] + 1
                            it[totalScore]   = row[Profiles.totalScore] + runScore
                        }
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("status" to "synced"))
            } catch (e: Exception) {
                application.log.error("Run sync failed", e)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "error")))
            }
        }

        // ── Leaderboard ────────────────────────────────────────────────────────

        get("/leaderboard/cities") {
            val cities = transaction {
                Profiles.select(Profiles.city)
                    .where { Profiles.city.isNotNull() }
                    .mapNotNull { it[Profiles.city] }
                    .distinct()
                    .sorted()
            }
            call.respond(cities)
        }

        get("/leaderboard") {
            val cityFilter = call.request.queryParameters["city"]
            val board = transaction {
                val query = if (cityFilter != null)
                    Profiles.selectAll().where { Profiles.city eq cityFilter }
                else
                    Profiles.selectAll()
                query
                    .orderBy(Profiles.totalScore to SortOrder.DESC)
                    .limit(50)
                    .map { row ->
                        LeaderboardEntryDto(
                            username = row[Profiles.username],
                            area     = row[Profiles.totalAreaKm2],
                            runs     = row[Profiles.runCount],
                            score    = row[Profiles.totalScore],
                            color    = row[Profiles.colorHex],
                            city     = row[Profiles.city]
                        )
                    }
            }
            call.respond(board)
        }

        // ── Friend requests ────────────────────────────────────────────────────

        post("/friend-requests") {
            try {
                val body = call.receive<SendFriendRequestBody>()
                transaction {
                    // Prevent duplicate active requests in either direction
                    val existing = FriendRequests.selectAll().where {
                        (
                            (FriendRequests.senderUid eq body.senderUid) and
                            (FriendRequests.receiverUid eq body.receiverUid)
                        ) or (
                            (FriendRequests.senderUid eq body.receiverUid) and
                            (FriendRequests.receiverUid eq body.senderUid)
                        )
                    }.firstOrNull()

                    if (existing != null && existing[FriendRequests.status] != "DECLINED") {
                        throw Exception("Friend request already exists")
                    }

                    val newId = java.util.UUID.randomUUID().toString()
                    FriendRequests.insert {
                        it[id]          = newId
                        it[senderUid]   = body.senderUid
                        it[receiverUid] = body.receiverUid
                        it[status]      = "PENDING"
                        it[createdAt]   = System.currentTimeMillis()
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("status" to "sent"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "error")))
            }
        }

        // Pending requests received by uid (inbox)
        get("/friend-requests/inbox") {
            val uid = call.request.queryParameters["uid"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "uid required")

            val inbox = transaction {
                FriendRequests.selectAll()
                    .where {
                        (FriendRequests.receiverUid eq uid) and
                        (FriendRequests.status eq "PENDING")
                    }
                    .orderBy(FriendRequests.createdAt to SortOrder.DESC)
                    .mapNotNull { req ->
                        val sender = Profiles.selectAll()
                            .where { Profiles.uid eq req[FriendRequests.senderUid] }
                            .singleOrNull() ?: return@mapNotNull null
                        FriendRequestResponse(
                            id                 = req[FriendRequests.id],
                            senderUid          = req[FriendRequests.senderUid],
                            senderUsername     = sender[Profiles.username],
                            senderDisplayName  = sender[Profiles.displayName],
                            senderColorHex     = sender[Profiles.colorHex],
                            status             = req[FriendRequests.status],
                            createdAt          = req[FriendRequests.createdAt]
                        )
                    }
            }
            call.respond(inbox)
        }

        // UIDs I have sent a pending request to (so the UI can show "Requested")
        get("/friend-requests/sent-pending") {
            val uid = call.request.queryParameters["uid"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "uid required")

            val uids = transaction {
                FriendRequests.selectAll()
                    .where {
                        (FriendRequests.senderUid eq uid) and
                        (FriendRequests.status eq "PENDING")
                    }
                    .map { it[FriendRequests.receiverUid] }
            }
            call.respond(uids)
        }

        patch("/friend-requests/{id}/accept") {
            val id = call.parameters["id"]
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "id required")
            transaction {
                FriendRequests.update({ FriendRequests.id eq id }) { it[status] = "ACCEPTED" }
            }
            call.respond(HttpStatusCode.OK, mapOf("status" to "accepted"))
        }

        patch("/friend-requests/{id}/decline") {
            val id = call.parameters["id"]
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "id required")
            transaction {
                FriendRequests.update({ FriendRequests.id eq id }) { it[status] = "DECLINED" }
            }
            call.respond(HttpStatusCode.OK, mapOf("status" to "declined"))
        }

        // Friends list (accepted requests in either direction)
        get("/friends") {
            val uid = call.request.queryParameters["uid"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "uid required")

            val friends = transaction {
                FriendRequests.selectAll()
                    .where {
                        ((FriendRequests.senderUid eq uid) or (FriendRequests.receiverUid eq uid)) and
                        (FriendRequests.status eq "ACCEPTED")
                    }
                    .mapNotNull { req ->
                        val friendUid = if (req[FriendRequests.senderUid] == uid)
                            req[FriendRequests.receiverUid]
                        else
                            req[FriendRequests.senderUid]

                        Profiles.selectAll()
                            .where { Profiles.uid eq friendUid }
                            .singleOrNull()
                            ?.let { p ->
                                ProfileSyncRequest(
                                    uid          = p[Profiles.uid],
                                    username     = p[Profiles.username],
                                    displayName  = p[Profiles.displayName],
                                    colorHex     = p[Profiles.colorHex],
                                    totalAreaKm2 = p[Profiles.totalAreaKm2],
                                    runCount     = p[Profiles.runCount]
                                )
                            }
                    }
            }
            call.respond(friends)
        }
    }
}
