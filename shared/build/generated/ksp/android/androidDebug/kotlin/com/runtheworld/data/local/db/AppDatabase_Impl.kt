package com.runtheworld.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _runDao: Lazy<RunDao> = lazy {
    RunDao_Impl(this)
  }

  private val _territoryDao: Lazy<TerritoryDao> = lazy {
    TerritoryDao_Impl(this)
  }

  private val _userAccountDao: Lazy<UserAccountDao> = lazy {
    UserAccountDao_Impl(this)
  }

  private val _userProfileDao: Lazy<UserProfileDao> = lazy {
    UserProfileDao_Impl(this)
  }

  private val _friendRequestDao: Lazy<FriendRequestDao> = lazy {
    FriendRequestDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(4,
        "609b4ae6887066779f11df178fe1bf99", "2706055c47a386120de0ec857a3b979d") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `runs` (`id` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `endedAt` INTEGER NOT NULL, `distanceMeters` REAL NOT NULL, `areaKm2` REAL NOT NULL, `path` TEXT NOT NULL, `claimedPolygon` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `territories` (`id` TEXT NOT NULL, `ownerUsername` TEXT NOT NULL, `ownerColorHex` TEXT NOT NULL, `polygon` TEXT NOT NULL, `claimedAt` INTEGER NOT NULL, `areaKm2` REAL NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `user_accounts` (`uid` TEXT NOT NULL, `email` TEXT NOT NULL, `displayName` TEXT, `passwordHash` TEXT, `salt` TEXT, `loginType` TEXT NOT NULL, PRIMARY KEY(`uid`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_accounts_email` ON `user_accounts` (`email`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `user_profiles` (`uid` TEXT NOT NULL, `username` TEXT NOT NULL, `displayName` TEXT NOT NULL, `colorHex` TEXT NOT NULL, PRIMARY KEY(`uid`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_profiles_username` ON `user_profiles` (`username`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `friend_requests` (`id` TEXT NOT NULL, `senderUid` TEXT NOT NULL, `receiverUid` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_friend_requests_senderUid_receiverUid` ON `friend_requests` (`senderUid`, `receiverUid`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '609b4ae6887066779f11df178fe1bf99')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `runs`")
        connection.execSQL("DROP TABLE IF EXISTS `territories`")
        connection.execSQL("DROP TABLE IF EXISTS `user_accounts`")
        connection.execSQL("DROP TABLE IF EXISTS `user_profiles`")
        connection.execSQL("DROP TABLE IF EXISTS `friend_requests`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsRuns: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRuns.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRuns.put("startedAt", TableInfo.Column("startedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRuns.put("endedAt", TableInfo.Column("endedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRuns.put("distanceMeters", TableInfo.Column("distanceMeters", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRuns.put("areaKm2", TableInfo.Column("areaKm2", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRuns.put("path", TableInfo.Column("path", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRuns.put("claimedPolygon", TableInfo.Column("claimedPolygon", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRuns: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRuns: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRuns: TableInfo = TableInfo("runs", _columnsRuns, _foreignKeysRuns, _indicesRuns)
        val _existingRuns: TableInfo = read(connection, "runs")
        if (!_infoRuns.equals(_existingRuns)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |runs(com.runtheworld.data.local.db.RunEntity).
              | Expected:
              |""".trimMargin() + _infoRuns + """
              |
              | Found:
              |""".trimMargin() + _existingRuns)
        }
        val _columnsTerritories: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTerritories.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTerritories.put("ownerUsername", TableInfo.Column("ownerUsername", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTerritories.put("ownerColorHex", TableInfo.Column("ownerColorHex", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTerritories.put("polygon", TableInfo.Column("polygon", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTerritories.put("claimedAt", TableInfo.Column("claimedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTerritories.put("areaKm2", TableInfo.Column("areaKm2", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTerritories: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTerritories: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTerritories: TableInfo = TableInfo("territories", _columnsTerritories,
            _foreignKeysTerritories, _indicesTerritories)
        val _existingTerritories: TableInfo = read(connection, "territories")
        if (!_infoTerritories.equals(_existingTerritories)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |territories(com.runtheworld.data.local.db.TerritoryEntity).
              | Expected:
              |""".trimMargin() + _infoTerritories + """
              |
              | Found:
              |""".trimMargin() + _existingTerritories)
        }
        val _columnsUserAccounts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUserAccounts.put("uid", TableInfo.Column("uid", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserAccounts.put("email", TableInfo.Column("email", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserAccounts.put("displayName", TableInfo.Column("displayName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserAccounts.put("passwordHash", TableInfo.Column("passwordHash", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserAccounts.put("salt", TableInfo.Column("salt", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserAccounts.put("loginType", TableInfo.Column("loginType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUserAccounts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUserAccounts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesUserAccounts.add(TableInfo.Index("index_user_accounts_email", true, listOf("email"),
            listOf("ASC")))
        val _infoUserAccounts: TableInfo = TableInfo("user_accounts", _columnsUserAccounts,
            _foreignKeysUserAccounts, _indicesUserAccounts)
        val _existingUserAccounts: TableInfo = read(connection, "user_accounts")
        if (!_infoUserAccounts.equals(_existingUserAccounts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |user_accounts(com.runtheworld.data.local.db.UserAccountEntity).
              | Expected:
              |""".trimMargin() + _infoUserAccounts + """
              |
              | Found:
              |""".trimMargin() + _existingUserAccounts)
        }
        val _columnsUserProfiles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUserProfiles.put("uid", TableInfo.Column("uid", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfiles.put("username", TableInfo.Column("username", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfiles.put("displayName", TableInfo.Column("displayName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfiles.put("colorHex", TableInfo.Column("colorHex", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUserProfiles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUserProfiles: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesUserProfiles.add(TableInfo.Index("index_user_profiles_username", true,
            listOf("username"), listOf("ASC")))
        val _infoUserProfiles: TableInfo = TableInfo("user_profiles", _columnsUserProfiles,
            _foreignKeysUserProfiles, _indicesUserProfiles)
        val _existingUserProfiles: TableInfo = read(connection, "user_profiles")
        if (!_infoUserProfiles.equals(_existingUserProfiles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |user_profiles(com.runtheworld.data.local.db.UserProfileEntity).
              | Expected:
              |""".trimMargin() + _infoUserProfiles + """
              |
              | Found:
              |""".trimMargin() + _existingUserProfiles)
        }
        val _columnsFriendRequests: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFriendRequests.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFriendRequests.put("senderUid", TableInfo.Column("senderUid", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFriendRequests.put("receiverUid", TableInfo.Column("receiverUid", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFriendRequests.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFriendRequests.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFriendRequests: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFriendRequests: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFriendRequests.add(TableInfo.Index("index_friend_requests_senderUid_receiverUid",
            true, listOf("senderUid", "receiverUid"), listOf("ASC", "ASC")))
        val _infoFriendRequests: TableInfo = TableInfo("friend_requests", _columnsFriendRequests,
            _foreignKeysFriendRequests, _indicesFriendRequests)
        val _existingFriendRequests: TableInfo = read(connection, "friend_requests")
        if (!_infoFriendRequests.equals(_existingFriendRequests)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |friend_requests(com.runtheworld.data.local.db.FriendRequestEntity).
              | Expected:
              |""".trimMargin() + _infoFriendRequests + """
              |
              | Found:
              |""".trimMargin() + _existingFriendRequests)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "runs", "territories",
        "user_accounts", "user_profiles", "friend_requests")
  }

  public override fun clearAllTables() {
    super.performClear(false, "runs", "territories", "user_accounts", "user_profiles",
        "friend_requests")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(RunDao::class, RunDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TerritoryDao::class, TerritoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserAccountDao::class, UserAccountDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserProfileDao::class, UserProfileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FriendRequestDao::class, FriendRequestDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun runDao(): RunDao = _runDao.value

  public override fun territoryDao(): TerritoryDao = _territoryDao.value

  public override fun userAccountDao(): UserAccountDao = _userAccountDao.value

  public override fun userProfileDao(): UserProfileDao = _userProfileDao.value

  public override fun friendRequestDao(): FriendRequestDao = _friendRequestDao.value
}
