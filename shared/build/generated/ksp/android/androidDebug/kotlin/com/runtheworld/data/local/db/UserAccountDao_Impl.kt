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
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UserAccountDao_Impl(
  __db: RoomDatabase,
) : UserAccountDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUserAccountEntity: EntityInsertAdapter<UserAccountEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUserAccountEntity = object : EntityInsertAdapter<UserAccountEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `user_accounts` (`uid`,`email`,`displayName`,`passwordHash`,`salt`,`loginType`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserAccountEntity) {
        statement.bindText(1, entity.uid)
        statement.bindText(2, entity.email)
        val _tmpDisplayName: String? = entity.displayName
        if (_tmpDisplayName == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpDisplayName)
        }
        val _tmpPasswordHash: String? = entity.passwordHash
        if (_tmpPasswordHash == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpPasswordHash)
        }
        val _tmpSalt: String? = entity.salt
        if (_tmpSalt == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpSalt)
        }
        statement.bindText(6, entity.loginType)
      }
    }
  }

  public override suspend fun insert(account: UserAccountEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfUserAccountEntity.insert(_connection, account)
  }

  public override suspend fun findByEmail(email: String): UserAccountEntity? {
    val _sql: String = "SELECT * FROM user_accounts WHERE email = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, email)
        val _columnIndexOfUid: Int = getColumnIndexOrThrow(_stmt, "uid")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfSalt: Int = getColumnIndexOrThrow(_stmt, "salt")
        val _columnIndexOfLoginType: Int = getColumnIndexOrThrow(_stmt, "loginType")
        val _result: UserAccountEntity?
        if (_stmt.step()) {
          val _tmpUid: String
          _tmpUid = _stmt.getText(_columnIndexOfUid)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpDisplayName: String?
          if (_stmt.isNull(_columnIndexOfDisplayName)) {
            _tmpDisplayName = null
          } else {
            _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          }
          val _tmpPasswordHash: String?
          if (_stmt.isNull(_columnIndexOfPasswordHash)) {
            _tmpPasswordHash = null
          } else {
            _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          }
          val _tmpSalt: String?
          if (_stmt.isNull(_columnIndexOfSalt)) {
            _tmpSalt = null
          } else {
            _tmpSalt = _stmt.getText(_columnIndexOfSalt)
          }
          val _tmpLoginType: String
          _tmpLoginType = _stmt.getText(_columnIndexOfLoginType)
          _result =
              UserAccountEntity(_tmpUid,_tmpEmail,_tmpDisplayName,_tmpPasswordHash,_tmpSalt,_tmpLoginType)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByGoogleId(googleId: String): UserAccountEntity? {
    val _sql: String = "SELECT * FROM user_accounts WHERE uid = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, googleId)
        val _columnIndexOfUid: Int = getColumnIndexOrThrow(_stmt, "uid")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfPasswordHash: Int = getColumnIndexOrThrow(_stmt, "passwordHash")
        val _columnIndexOfSalt: Int = getColumnIndexOrThrow(_stmt, "salt")
        val _columnIndexOfLoginType: Int = getColumnIndexOrThrow(_stmt, "loginType")
        val _result: UserAccountEntity?
        if (_stmt.step()) {
          val _tmpUid: String
          _tmpUid = _stmt.getText(_columnIndexOfUid)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpDisplayName: String?
          if (_stmt.isNull(_columnIndexOfDisplayName)) {
            _tmpDisplayName = null
          } else {
            _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          }
          val _tmpPasswordHash: String?
          if (_stmt.isNull(_columnIndexOfPasswordHash)) {
            _tmpPasswordHash = null
          } else {
            _tmpPasswordHash = _stmt.getText(_columnIndexOfPasswordHash)
          }
          val _tmpSalt: String?
          if (_stmt.isNull(_columnIndexOfSalt)) {
            _tmpSalt = null
          } else {
            _tmpSalt = _stmt.getText(_columnIndexOfSalt)
          }
          val _tmpLoginType: String
          _tmpLoginType = _stmt.getText(_columnIndexOfLoginType)
          _result =
              UserAccountEntity(_tmpUid,_tmpEmail,_tmpDisplayName,_tmpPasswordHash,_tmpSalt,_tmpLoginType)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun emailExists(email: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM user_accounts WHERE email = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, email)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
