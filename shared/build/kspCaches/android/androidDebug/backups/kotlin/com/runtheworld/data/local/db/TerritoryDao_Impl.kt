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
public class TerritoryDao_Impl(
  __db: RoomDatabase,
) : TerritoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTerritoryEntity: EntityInsertAdapter<TerritoryEntity>

  private val __converters: Converters = Converters()
  init {
    this.__db = __db
    this.__insertAdapterOfTerritoryEntity = object : EntityInsertAdapter<TerritoryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `territories` (`id`,`ownerUsername`,`ownerColorHex`,`polygon`,`claimedAt`,`areaKm2`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TerritoryEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.ownerUsername)
        statement.bindText(3, entity.ownerColorHex)
        val _tmp: String = __converters.fromGpsPointList(entity.polygon)
        statement.bindText(4, _tmp)
        statement.bindLong(5, entity.claimedAt)
        statement.bindDouble(6, entity.areaKm2)
      }
    }
  }

  public override suspend fun insert(territory: TerritoryEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfTerritoryEntity.insert(_connection, territory)
  }

  public override fun observeAll(): Flow<List<TerritoryEntity>> {
    val _sql: String = "SELECT * FROM territories ORDER BY claimedAt DESC"
    return createFlow(__db, false, arrayOf("territories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfOwnerUsername: Int = getColumnIndexOrThrow(_stmt, "ownerUsername")
        val _columnIndexOfOwnerColorHex: Int = getColumnIndexOrThrow(_stmt, "ownerColorHex")
        val _columnIndexOfPolygon: Int = getColumnIndexOrThrow(_stmt, "polygon")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
        val _columnIndexOfAreaKm2: Int = getColumnIndexOrThrow(_stmt, "areaKm2")
        val _result: MutableList<TerritoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TerritoryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpOwnerUsername: String
          _tmpOwnerUsername = _stmt.getText(_columnIndexOfOwnerUsername)
          val _tmpOwnerColorHex: String
          _tmpOwnerColorHex = _stmt.getText(_columnIndexOfOwnerColorHex)
          val _tmpPolygon: List<GpsPoint>
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfPolygon)
          _tmpPolygon = __converters.toGpsPointList(_tmp)
          val _tmpClaimedAt: Long
          _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          val _tmpAreaKm2: Double
          _tmpAreaKm2 = _stmt.getDouble(_columnIndexOfAreaKm2)
          _item =
              TerritoryEntity(_tmpId,_tmpOwnerUsername,_tmpOwnerColorHex,_tmpPolygon,_tmpClaimedAt,_tmpAreaKm2)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByOwner(username: String) {
    val _sql: String = "DELETE FROM territories WHERE ownerUsername = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, username)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: String) {
    val _sql: String = "DELETE FROM territories WHERE id = ?"
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
