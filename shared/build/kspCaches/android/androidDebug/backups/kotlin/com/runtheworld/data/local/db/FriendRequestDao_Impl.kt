package com.runtheworld.`data`.local.db

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class FriendRequestDao_Impl(
  __db: RoomDatabase,
) : FriendRequestDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFriendRequestEntity: EntityInsertAdapter<FriendRequestEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFriendRequestEntity = object : EntityInsertAdapter<FriendRequestEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `friend_requests` (`id`,`senderUid`,`receiverUid`,`status`,`createdAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FriendRequestEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.senderUid)
        statement.bindText(3, entity.receiverUid)
        statement.bindText(4, entity.status)
        statement.bindLong(5, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(request: FriendRequestEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFriendRequestEntity.insert(_connection, request)
  }

  public override suspend fun getInboxRequests(uid: String): List<FriendRequestEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM friend_requests
        |        WHERE receiverUid = ? AND status = 'PENDING'
        |        ORDER BY createdAt DESC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, uid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSenderUid: Int = getColumnIndexOrThrow(_stmt, "senderUid")
        val _columnIndexOfReceiverUid: Int = getColumnIndexOrThrow(_stmt, "receiverUid")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<FriendRequestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FriendRequestEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSenderUid: String
          _tmpSenderUid = _stmt.getText(_columnIndexOfSenderUid)
          val _tmpReceiverUid: String
          _tmpReceiverUid = _stmt.getText(_columnIndexOfReceiverUid)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = FriendRequestEntity(_tmpId,_tmpSenderUid,_tmpReceiverUid,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSentPendingReceiverUids(uid: String): List<String> {
    val _sql: String = """
        |
        |        SELECT receiverUid FROM friend_requests
        |        WHERE senderUid = ? AND status = 'PENDING'
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, uid)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFriends(uid: String): List<UserProfileEntity> {
    val _sql: String = """
        |
        |        SELECT p.* FROM user_profiles p
        |        WHERE p.uid IN (
        |            SELECT CASE WHEN r.senderUid = ? THEN r.receiverUid ELSE r.senderUid END
        |            FROM friend_requests r
        |            WHERE (r.senderUid = ? OR r.receiverUid = ?) AND r.status = 'ACCEPTED'
        |        )
        |        ORDER BY p.username ASC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, uid)
        _argIndex = 2
        _stmt.bindText(_argIndex, uid)
        _argIndex = 3
        _stmt.bindText(_argIndex, uid)
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

  public override suspend fun getPendingInboxCount(uid: String): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(*) FROM friend_requests
        |        WHERE receiverUid = ? AND status = 'PENDING'
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, uid)
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

  public override suspend fun updateStatus(id: String, status: String) {
    val _sql: String = "UPDATE friend_requests SET status = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
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
