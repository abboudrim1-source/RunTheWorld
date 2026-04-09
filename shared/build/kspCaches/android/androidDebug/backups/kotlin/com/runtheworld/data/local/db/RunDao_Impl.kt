package com.runtheworld.`data`.local.db

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.runtheworld.domain.model.GpsPoint
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RunDao_Impl(
  __db: RoomDatabase,
) : RunDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRunEntity: EntityInsertAdapter<RunEntity>

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertAdapterOfRunEntity = object : EntityInsertAdapter<RunEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `runs` (`id`,`startedAt`,`endedAt`,`distanceMeters`,`areaKm2`,`path`,`claimedPolygon`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RunEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.startedAt)
        statement.bindLong(3, entity.endedAt)
        statement.bindDouble(4, entity.distanceMeters)
        statement.bindDouble(5, entity.areaKm2)
        val _tmp: String = __converters.fromGpsPointList(entity.path)
        statement.bindText(6, _tmp)
        val _tmp_1: String = __converters.fromGpsPointList(entity.claimedPolygon)
        statement.bindText(7, _tmp_1)
      }
    }
  }

  public override suspend fun insert(run: RunEntity): Unit = performSuspending(__db, false, true) {
      _connection ->
    __insertAdapterOfRunEntity.insert(_connection, run)
  }

  public override fun observeAll(): Flow<List<RunEntity>> {
    val _sql: String = "SELECT * FROM runs ORDER BY startedAt DESC"
    return createFlow(__db, false, arrayOf("runs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfDistanceMeters: Int = getColumnIndexOrThrow(_stmt, "distanceMeters")
        val _columnIndexOfAreaKm2: Int = getColumnIndexOrThrow(_stmt, "areaKm2")
        val _columnIndexOfPath: Int = getColumnIndexOrThrow(_stmt, "path")
        val _columnIndexOfClaimedPolygon: Int = getColumnIndexOrThrow(_stmt, "claimedPolygon")
        val _result: MutableList<RunEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RunEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpEndedAt: Long
          _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          val _tmpDistanceMeters: Double
          _tmpDistanceMeters = _stmt.getDouble(_columnIndexOfDistanceMeters)
          val _tmpAreaKm2: Double
          _tmpAreaKm2 = _stmt.getDouble(_columnIndexOfAreaKm2)
          val _tmpPath: List<GpsPoint>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfPath)
          _tmpPath = __converters.toGpsPointList(_tmp)
          val _tmpClaimedPolygon: List<GpsPoint>
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfClaimedPolygon)
          _tmpClaimedPolygon = __converters.toGpsPointList(_tmp_1)
          _item =
              RunEntity(_tmpId,_tmpStartedAt,_tmpEndedAt,_tmpDistanceMeters,_tmpAreaKm2,_tmpPath,_tmpClaimedPolygon)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: String): RunEntity? {
    val _sql: String = "SELECT * FROM runs WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfDistanceMeters: Int = getColumnIndexOrThrow(_stmt, "distanceMeters")
        val _columnIndexOfAreaKm2: Int = getColumnIndexOrThrow(_stmt, "areaKm2")
        val _columnIndexOfPath: Int = getColumnIndexOrThrow(_stmt, "path")
        val _columnIndexOfClaimedPolygon: Int = getColumnIndexOrThrow(_stmt, "claimedPolygon")
        val _result: RunEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpEndedAt: Long
          _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          val _tmpDistanceMeters: Double
          _tmpDistanceMeters = _stmt.getDouble(_columnIndexOfDistanceMeters)
          val _tmpAreaKm2: Double
          _tmpAreaKm2 = _stmt.getDouble(_columnIndexOfAreaKm2)
          val _tmpPath: List<GpsPoint>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfPath)
          _tmpPath = __converters.toGpsPointList(_tmp)
          val _tmpClaimedPolygon: List<GpsPoint>
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfClaimedPolygon)
          _tmpClaimedPolygon = __converters.toGpsPointList(_tmp_1)
          _result =
              RunEntity(_tmpId,_tmpStartedAt,_tmpEndedAt,_tmpDistanceMeters,_tmpAreaKm2,_tmpPath,_tmpClaimedPolygon)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM runs WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
