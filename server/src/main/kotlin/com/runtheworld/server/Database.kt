package com.runtheworld.server

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// ── Tables ────────────────────────────────────────────────────────────────────

object Profiles : Table("profiles") {
    val uid         = varchar("uid", 64)
    val username    = varchar("username", 64)
    val displayName = varchar("display_name", 255)
    val colorHex    = varchar("color_hex", 16)
    val totalAreaKm2 = double("total_area_km2").default(0.0)
    val runCount    = integer("run_count").default(0)
    override val primaryKey = PrimaryKey(uid)
}

object Runs : Table("runs") {
    val id              = varchar("id", 64)
    val userId          = varchar("user_id", 64)
    val distanceMeters  = double("distance_meters")
    val durationSeconds = long("duration_seconds")
    val areaKm2         = double("area_km2")
    val createdAt       = long("created_at")
    override val primaryKey = PrimaryKey(id)
}

// ── Init ──────────────────────────────────────────────────────────────────────

object DatabaseFactory {
    fun init() {
        val dbPath = "runtheworld_server.db"
        Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Profiles, Runs)
        }
    }
}
