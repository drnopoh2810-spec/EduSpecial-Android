package com.eduspecial.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.eduspecial.data.local.dao.AnalyticsDao;
import com.eduspecial.data.local.dao.AnalyticsDao_Impl;
import com.eduspecial.data.local.dao.BookmarkDao;
import com.eduspecial.data.local.dao.BookmarkDao_Impl;
import com.eduspecial.data.local.dao.FlashcardDao;
import com.eduspecial.data.local.dao.FlashcardDao_Impl;
import com.eduspecial.data.local.dao.PendingSubmissionDao;
import com.eduspecial.data.local.dao.PendingSubmissionDao_Impl;
import com.eduspecial.data.local.dao.QADao;
import com.eduspecial.data.local.dao.QADao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EduSpecialDatabase_Impl extends EduSpecialDatabase {
  private volatile FlashcardDao _flashcardDao;

  private volatile QADao _qADao;

  private volatile PendingSubmissionDao _pendingSubmissionDao;

  private volatile BookmarkDao _bookmarkDao;

  private volatile AnalyticsDao _analyticsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `flashcards` (`id` TEXT NOT NULL, `term` TEXT NOT NULL, `definition` TEXT NOT NULL, `category` TEXT NOT NULL, `mediaUrl` TEXT, `mediaType` TEXT NOT NULL, `contributor` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `reviewState` TEXT NOT NULL, `easeFactor` REAL NOT NULL, `interval` INTEGER NOT NULL, `nextReviewDate` INTEGER NOT NULL, `isOfflineCached` INTEGER NOT NULL, `isPendingSync` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `qa_questions` (`id` TEXT NOT NULL, `question` TEXT NOT NULL, `category` TEXT NOT NULL, `contributor` TEXT NOT NULL, `upvotes` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `isAnswered` INTEGER NOT NULL, `tags` TEXT NOT NULL, `isPendingSync` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `qa_answers` (`id` TEXT NOT NULL, `questionId` TEXT NOT NULL, `content` TEXT NOT NULL, `contributor` TEXT NOT NULL, `upvotes` INTEGER NOT NULL, `isAccepted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pending_submissions` (`localId` TEXT NOT NULL, `type` TEXT NOT NULL, `payload` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `retryCount` INTEGER NOT NULL, PRIMARY KEY(`localId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` TEXT NOT NULL, `itemId` TEXT NOT NULL, `itemType` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_bookmarks_itemId_itemType` ON `bookmarks` (`itemId`, `itemType`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_review_logs` (`dayEpoch` INTEGER NOT NULL, `reviewCount` INTEGER NOT NULL, `archivedCount` INTEGER NOT NULL, PRIMARY KEY(`dayEpoch`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd1f21c120f9c1abf653be665ba8cfdfa')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `flashcards`");
        db.execSQL("DROP TABLE IF EXISTS `qa_questions`");
        db.execSQL("DROP TABLE IF EXISTS `qa_answers`");
        db.execSQL("DROP TABLE IF EXISTS `pending_submissions`");
        db.execSQL("DROP TABLE IF EXISTS `bookmarks`");
        db.execSQL("DROP TABLE IF EXISTS `daily_review_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFlashcards = new HashMap<String, TableInfo.Column>(14);
        _columnsFlashcards.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("term", new TableInfo.Column("term", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("definition", new TableInfo.Column("definition", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("mediaUrl", new TableInfo.Column("mediaUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("mediaType", new TableInfo.Column("mediaType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("contributor", new TableInfo.Column("contributor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("reviewState", new TableInfo.Column("reviewState", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("easeFactor", new TableInfo.Column("easeFactor", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("interval", new TableInfo.Column("interval", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("nextReviewDate", new TableInfo.Column("nextReviewDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("isOfflineCached", new TableInfo.Column("isOfflineCached", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlashcards.put("isPendingSync", new TableInfo.Column("isPendingSync", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFlashcards = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFlashcards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFlashcards = new TableInfo("flashcards", _columnsFlashcards, _foreignKeysFlashcards, _indicesFlashcards);
        final TableInfo _existingFlashcards = TableInfo.read(db, "flashcards");
        if (!_infoFlashcards.equals(_existingFlashcards)) {
          return new RoomOpenHelper.ValidationResult(false, "flashcards(com.eduspecial.data.local.entities.FlashcardEntity).\n"
                  + " Expected:\n" + _infoFlashcards + "\n"
                  + " Found:\n" + _existingFlashcards);
        }
        final HashMap<String, TableInfo.Column> _columnsQaQuestions = new HashMap<String, TableInfo.Column>(9);
        _columnsQaQuestions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("question", new TableInfo.Column("question", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("contributor", new TableInfo.Column("contributor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("upvotes", new TableInfo.Column("upvotes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("isAnswered", new TableInfo.Column("isAnswered", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaQuestions.put("isPendingSync", new TableInfo.Column("isPendingSync", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQaQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQaQuestions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQaQuestions = new TableInfo("qa_questions", _columnsQaQuestions, _foreignKeysQaQuestions, _indicesQaQuestions);
        final TableInfo _existingQaQuestions = TableInfo.read(db, "qa_questions");
        if (!_infoQaQuestions.equals(_existingQaQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "qa_questions(com.eduspecial.data.local.entities.QAQuestionEntity).\n"
                  + " Expected:\n" + _infoQaQuestions + "\n"
                  + " Found:\n" + _existingQaQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsQaAnswers = new HashMap<String, TableInfo.Column>(7);
        _columnsQaAnswers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaAnswers.put("questionId", new TableInfo.Column("questionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaAnswers.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaAnswers.put("contributor", new TableInfo.Column("contributor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaAnswers.put("upvotes", new TableInfo.Column("upvotes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaAnswers.put("isAccepted", new TableInfo.Column("isAccepted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQaAnswers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQaAnswers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQaAnswers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQaAnswers = new TableInfo("qa_answers", _columnsQaAnswers, _foreignKeysQaAnswers, _indicesQaAnswers);
        final TableInfo _existingQaAnswers = TableInfo.read(db, "qa_answers");
        if (!_infoQaAnswers.equals(_existingQaAnswers)) {
          return new RoomOpenHelper.ValidationResult(false, "qa_answers(com.eduspecial.data.local.entities.QAAnswerEntity).\n"
                  + " Expected:\n" + _infoQaAnswers + "\n"
                  + " Found:\n" + _existingQaAnswers);
        }
        final HashMap<String, TableInfo.Column> _columnsPendingSubmissions = new HashMap<String, TableInfo.Column>(5);
        _columnsPendingSubmissions.put("localId", new TableInfo.Column("localId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSubmissions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSubmissions.put("payload", new TableInfo.Column("payload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSubmissions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSubmissions.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPendingSubmissions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPendingSubmissions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPendingSubmissions = new TableInfo("pending_submissions", _columnsPendingSubmissions, _foreignKeysPendingSubmissions, _indicesPendingSubmissions);
        final TableInfo _existingPendingSubmissions = TableInfo.read(db, "pending_submissions");
        if (!_infoPendingSubmissions.equals(_existingPendingSubmissions)) {
          return new RoomOpenHelper.ValidationResult(false, "pending_submissions(com.eduspecial.data.local.entities.PendingSubmissionEntity).\n"
                  + " Expected:\n" + _infoPendingSubmissions + "\n"
                  + " Found:\n" + _existingPendingSubmissions);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmarks = new HashMap<String, TableInfo.Column>(4);
        _columnsBookmarks.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("itemId", new TableInfo.Column("itemId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("itemType", new TableInfo.Column("itemType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmarks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookmarks = new HashSet<TableInfo.Index>(1);
        _indicesBookmarks.add(new TableInfo.Index("index_bookmarks_itemId_itemType", true, Arrays.asList("itemId", "itemType"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoBookmarks = new TableInfo("bookmarks", _columnsBookmarks, _foreignKeysBookmarks, _indicesBookmarks);
        final TableInfo _existingBookmarks = TableInfo.read(db, "bookmarks");
        if (!_infoBookmarks.equals(_existingBookmarks)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmarks(com.eduspecial.data.local.entities.BookmarkEntity).\n"
                  + " Expected:\n" + _infoBookmarks + "\n"
                  + " Found:\n" + _existingBookmarks);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyReviewLogs = new HashMap<String, TableInfo.Column>(3);
        _columnsDailyReviewLogs.put("dayEpoch", new TableInfo.Column("dayEpoch", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyReviewLogs.put("reviewCount", new TableInfo.Column("reviewCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyReviewLogs.put("archivedCount", new TableInfo.Column("archivedCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyReviewLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyReviewLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyReviewLogs = new TableInfo("daily_review_logs", _columnsDailyReviewLogs, _foreignKeysDailyReviewLogs, _indicesDailyReviewLogs);
        final TableInfo _existingDailyReviewLogs = TableInfo.read(db, "daily_review_logs");
        if (!_infoDailyReviewLogs.equals(_existingDailyReviewLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_review_logs(com.eduspecial.data.local.entities.DailyReviewLogEntity).\n"
                  + " Expected:\n" + _infoDailyReviewLogs + "\n"
                  + " Found:\n" + _existingDailyReviewLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d1f21c120f9c1abf653be665ba8cfdfa", "763aba5f57cb9ba14ac6c185947af499");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "flashcards","qa_questions","qa_answers","pending_submissions","bookmarks","daily_review_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `flashcards`");
      _db.execSQL("DELETE FROM `qa_questions`");
      _db.execSQL("DELETE FROM `qa_answers`");
      _db.execSQL("DELETE FROM `pending_submissions`");
      _db.execSQL("DELETE FROM `bookmarks`");
      _db.execSQL("DELETE FROM `daily_review_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FlashcardDao.class, FlashcardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QADao.class, QADao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PendingSubmissionDao.class, PendingSubmissionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookmarkDao.class, BookmarkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AnalyticsDao.class, AnalyticsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FlashcardDao flashcardDao() {
    if (_flashcardDao != null) {
      return _flashcardDao;
    } else {
      synchronized(this) {
        if(_flashcardDao == null) {
          _flashcardDao = new FlashcardDao_Impl(this);
        }
        return _flashcardDao;
      }
    }
  }

  @Override
  public QADao qaDao() {
    if (_qADao != null) {
      return _qADao;
    } else {
      synchronized(this) {
        if(_qADao == null) {
          _qADao = new QADao_Impl(this);
        }
        return _qADao;
      }
    }
  }

  @Override
  public PendingSubmissionDao pendingSubmissionDao() {
    if (_pendingSubmissionDao != null) {
      return _pendingSubmissionDao;
    } else {
      synchronized(this) {
        if(_pendingSubmissionDao == null) {
          _pendingSubmissionDao = new PendingSubmissionDao_Impl(this);
        }
        return _pendingSubmissionDao;
      }
    }
  }

  @Override
  public BookmarkDao bookmarkDao() {
    if (_bookmarkDao != null) {
      return _bookmarkDao;
    } else {
      synchronized(this) {
        if(_bookmarkDao == null) {
          _bookmarkDao = new BookmarkDao_Impl(this);
        }
        return _bookmarkDao;
      }
    }
  }

  @Override
  public AnalyticsDao analyticsDao() {
    if (_analyticsDao != null) {
      return _analyticsDao;
    } else {
      synchronized(this) {
        if(_analyticsDao == null) {
          _analyticsDao = new AnalyticsDao_Impl(this);
        }
        return _analyticsDao;
      }
    }
  }
}
