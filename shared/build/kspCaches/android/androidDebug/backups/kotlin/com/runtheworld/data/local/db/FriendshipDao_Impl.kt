package com.runtheworld.`data`.local.db

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FriendshipDao_Impl(
  __db: RoomDatabase,
) : FriendshipDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFriendshipEntity: EntityInsertAdapter<FriendshipEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFriendshipEntity = object : EntityInsertAdapter<FriendshipEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `friendships` (`ownerUid`,`friendUid`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FriendshipEntity) {
        statement.bindText(1, entity.ownerUid)
        statement.bindText(2, entity.friendUid)
      }
    }
  }

  public override suspend fun addFriend(friendship: FriendshipEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFriendshipEntity.insert(_connection, friendship)
  }

  public override suspend fun getFriendProfiles(ownerUid: String): List<UserProfileEntity> {
    val _sql: String = """
        |
        |        SELECT p.* FROM user_profiles p
        |        INNER JOIN friendships f ON p.uid = f.friendUid
        |        WHERE f.ownerUid = ?
        |        ORDER BY p.username ASC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerUid)
        val _columnIndexOfUid: Int = getColumnIndexOrThrow(_stmt, "uid")
        val _columnIndexOfUsername: Int = getColumnIndexOrThrow(_stmt, "username")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfColorHex: Int = getColumnIndexOrThrow(_stmt, "colorHex")
        val _result: MutableList<UserProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserProfileEntity
          val _tmpUid: String
          _tmpUid = _stmt.getText(_columnIndexOfUid)
          val _tmpUsername: String
          _tmpUsername = _stmt.getText(_columnIndexOfUsername)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpColorHex: String
          _tmpColorHex = _stmt.getText(_columnIndexOfColorHex)
          _item = UserProfileEntity(_tmpUid,_tmpUsername,_tmpDisplayName,_tmpColorHex)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isFriend(ownerUid: String, friendUid: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM friendships WHERE ownerUid = ? AND friendUid = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerUid)
        _argIndex = 2
        _stmt.bindText(_argIndex, friendUid)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeFriend(ownerUid: String, friendUid: String) {
    val _sql: String = "DELETE FROM friendships WHERE ownerUid = ? AND friendUid = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerUid)
        _argIndex = 2
        _stmt.bindText(_argIndex, friendUid)
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
