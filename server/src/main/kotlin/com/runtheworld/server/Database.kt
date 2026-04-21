package com.runtheworld.server

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Profiles : Table("profiles") {
    val uid          = varchar("uid", 64)
    val username     = varchar("username", 64)
    val displayName  = varchar("display_name", 255)
    val colorHex     = varchar("color_hex", 16)
    val totalAreaKm2 = double("total_area_km2").default(0.0)
    val runCount     = integer("run_count").default(0)
    val totalScore   = integer("total_score").default(0)
    val avatarBase64 = text("avatar_base64").nullable()
    val city         = varchar("city", 128).nullable()
    override val primaryKey = PrimaryKey(uid)
}

object Territories : Table("territories") {
    val id            = varchar("id", 64)
    val userId        = varchar("user_id", 64)
    val ownerUsername = varchar("owner_username", 64)
    val ownerColorHex = varchar("owner_color_hex", 16)
    val polygonJson   = text("polygon_json")
    val claimedAt     = long("claimed_at")
    val areaKm2       = double("area_km2")
    override val primaryKey = PrimaryKey(id)
}

object Runs : Table("runs") {
    val id              = varchar("id", 64)
    val userId          = varchar("user_id", 64)
    val distanceMeters  = double("distance_meters")
    val durationSeconds = long("duration_seconds")
    val areaKm2         = double("area_km2")
    val score           = integer("score").default(0)
    val createdAt       = long("created_at")
    override val primaryKey = PrimaryKey(id)
}

object FriendRequests : Table("friend_requests") {
    val id          = varchar("id", 64)
    val senderUid   = varchar("sender_uid", 64)
    val receiverUid = varchar("receiver_uid", 64)
    val status      = varchar("status", 16).default("PENDING") // PENDING | ACCEPTED | DECLINED
    val createdAt   = long("created_at")
    override val primaryKey = PrimaryKey(id)
}

object Accounts : Table("accounts") {
    val uid          = varchar("uid", 64)
    val email        = varchar("email", 255).uniqueIndex()
    val displayName  = varchar("display_name", 255).nullable()
    val passwordHash = varchar("password_hash", 128).nullable()
    val salt         = varchar("salt", 64).nullable()
    val loginType    = varchar("login_type", 16).default("email")
    override val primaryKey = PrimaryKey(uid)
}

object DatabaseFactory {
    fun init() {
        val dbPath = System.getenv("DB_PATH") ?: "runtheworld_server.db"
        Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Profiles, Runs, FriendRequests, Accounts, Territories)
        }
        // Backfill scores for runs that were saved before the score column existed
        transaction {
            Runs.selectAll().where { Runs.score eq 0 }.forEach { row ->
                val computed = kotlin.math.round(row[Runs.distanceMeters] / 10.0).toInt()
                if (computed > 0) {
                    Runs.update({ Runs.id eq row[Runs.id] }) { it[score] = computed }
                }
            }
            // Recompute each profile's totalScore from the runs table
            Profiles.selectAll().forEach { profile ->
                val uid = profile[Profiles.uid]
                val total = Runs.select(Runs.score).where { Runs.userId eq uid }
                    .sumOf { it[Runs.score] }
                Profiles.update({ Profiles.uid eq uid }) { it[totalScore] = total }
            }
        }
    }
}
