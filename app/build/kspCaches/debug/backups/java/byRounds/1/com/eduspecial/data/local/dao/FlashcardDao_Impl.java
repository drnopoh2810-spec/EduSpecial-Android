package com.eduspecial.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.paging.LimitOffsetPagingSource;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.eduspecial.data.local.entities.FlashcardEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FlashcardDao_Impl implements FlashcardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FlashcardEntity> __insertionAdapterOfFlashcardEntity;

  private final EntityDeletionOrUpdateAdapter<FlashcardEntity> __deletionAdapterOfFlashcardEntity;

  private final EntityDeletionOrUpdateAdapter<FlashcardEntity> __updateAdapterOfFlashcardEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByCategoryIfNotPending;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllNotPending;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReviewState;

  private final SharedSQLiteStatement __preparedStmtOfUpdateContent;

  public FlashcardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFlashcardEntity = new EntityInsertionAdapter<FlashcardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `flashcards` (`id`,`term`,`definition`,`category`,`mediaUrl`,`mediaType`,`contributor`,`createdAt`,`reviewState`,`easeFactor`,`interval`,`nextReviewDate`,`isOfflineCached`,`isPendingSync`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlashcardEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTerm());
        statement.bindString(3, entity.getDefinition());
        statement.bindString(4, entity.getCategory());
        if (entity.getMediaUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMediaUrl());
        }
        statement.bindString(6, entity.getMediaType());
        statement.bindString(7, entity.getContributor());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindString(9, entity.getReviewState());
        statement.bindDouble(10, entity.getEaseFactor());
        statement.bindLong(11, entity.getInterval());
        statement.bindLong(12, entity.getNextReviewDate());
        final int _tmp = entity.isOfflineCached() ? 1 : 0;
        statement.bindLong(13, _tmp);
        final int _tmp_1 = entity.isPendingSync() ? 1 : 0;
        statement.bindLong(14, _tmp_1);
      }
    };
    this.__deletionAdapterOfFlashcardEntity = new EntityDeletionOrUpdateAdapter<FlashcardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `flashcards` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlashcardEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfFlashcardEntity = new EntityDeletionOrUpdateAdapter<FlashcardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `flashcards` SET `id` = ?,`term` = ?,`definition` = ?,`category` = ?,`mediaUrl` = ?,`mediaType` = ?,`contributor` = ?,`createdAt` = ?,`reviewState` = ?,`easeFactor` = ?,`interval` = ?,`nextReviewDate` = ?,`isOfflineCached` = ?,`isPendingSync` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FlashcardEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTerm());
        statement.bindString(3, entity.getDefinition());
        statement.bindString(4, entity.getCategory());
        if (entity.getMediaUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMediaUrl());
        }
        statement.bindString(6, entity.getMediaType());
        statement.bindString(7, entity.getContributor());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindString(9, entity.getReviewState());
        statement.bindDouble(10, entity.getEaseFactor());
        statement.bindLong(11, entity.getInterval());
        statement.bindLong(12, entity.getNextReviewDate());
        final int _tmp = entity.isOfflineCached() ? 1 : 0;
        statement.bindLong(13, _tmp);
        final int _tmp_1 = entity.isPendingSync() ? 1 : 0;
        statement.bindLong(14, _tmp_1);
        statement.bindString(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByCategoryIfNotPending = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM flashcards WHERE category = ? AND isPendingSync = 0";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllNotPending = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM flashcards WHERE isPendingSync = 0";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateReviewState = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE flashcards SET reviewState = ?, easeFactor = ?, interval = ?, nextReviewDate = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateContent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE flashcards SET term = ?, definition = ?, category = ?, mediaUrl = ?, mediaType = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<FlashcardEntity> flashcards,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFlashcardEntity.insert(flashcards);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insert(final FlashcardEntity flashcard,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFlashcardEntity.insert(flashcard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final FlashcardEntity flashcard,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFlashcardEntity.handle(flashcard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final FlashcardEntity flashcard,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFlashcardEntity.handle(flashcard);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByCategoryIfNotPending(final String category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByCategoryIfNotPending.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, category);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByCategoryIfNotPending.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllNotPending(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllNotPending.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllNotPending.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReviewState(final String id, final String state, final float easeFactor,
      final int interval, final long nextReviewDate, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReviewState.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, state);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, easeFactor);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, interval);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, nextReviewDate);
        _argIndex = 5;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateReviewState.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateContent(final String id, final String term, final String definition,
      final String category, final String mediaUrl, final String mediaType,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateContent.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, term);
        _argIndex = 2;
        _stmt.bindString(_argIndex, definition);
        _argIndex = 3;
        _stmt.bindString(_argIndex, category);
        _argIndex = 4;
        if (mediaUrl == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, mediaUrl);
        }
        _argIndex = 5;
        _stmt.bindString(_argIndex, mediaType);
        _argIndex = 6;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateContent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FlashcardEntity>> getAllFlashcards() {
    final String _sql = "SELECT * FROM flashcards ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<FlashcardEntity>>() {
      @Override
      @NonNull
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "term");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewState");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(_cursor, "isOfflineCached");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTerm;
            _tmpTerm = _cursor.getString(_cursorIndexOfTerm);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpReviewState;
            _tmpReviewState = _cursor.getString(_cursorIndexOfReviewState);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextReviewDate;
            _tmpNextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            final boolean _tmpIsOfflineCached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOfflineCached);
            _tmpIsOfflineCached = _tmp != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public PagingSource<Integer, FlashcardEntity> getFlashcardsPaged() {
    final String _sql = "SELECT * FROM flashcards ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new LimitOffsetPagingSource<FlashcardEntity>(_statement, __db, "flashcards") {
      @Override
      @NonNull
      protected List<FlashcardEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(cursor, "term");
        final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(cursor, "definition");
        final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(cursor, "category");
        final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(cursor, "mediaUrl");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "mediaType");
        final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(cursor, "contributor");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "createdAt");
        final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(cursor, "reviewState");
        final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(cursor, "easeFactor");
        final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(cursor, "interval");
        final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(cursor, "nextReviewDate");
        final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(cursor, "isOfflineCached");
        final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(cursor, "isPendingSync");
        final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final FlashcardEntity _item;
          final String _tmpId;
          _tmpId = cursor.getString(_cursorIndexOfId);
          final String _tmpTerm;
          _tmpTerm = cursor.getString(_cursorIndexOfTerm);
          final String _tmpDefinition;
          _tmpDefinition = cursor.getString(_cursorIndexOfDefinition);
          final String _tmpCategory;
          _tmpCategory = cursor.getString(_cursorIndexOfCategory);
          final String _tmpMediaUrl;
          if (cursor.isNull(_cursorIndexOfMediaUrl)) {
            _tmpMediaUrl = null;
          } else {
            _tmpMediaUrl = cursor.getString(_cursorIndexOfMediaUrl);
          }
          final String _tmpMediaType;
          _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          final String _tmpContributor;
          _tmpContributor = cursor.getString(_cursorIndexOfContributor);
          final long _tmpCreatedAt;
          _tmpCreatedAt = cursor.getLong(_cursorIndexOfCreatedAt);
          final String _tmpReviewState;
          _tmpReviewState = cursor.getString(_cursorIndexOfReviewState);
          final float _tmpEaseFactor;
          _tmpEaseFactor = cursor.getFloat(_cursorIndexOfEaseFactor);
          final int _tmpInterval;
          _tmpInterval = cursor.getInt(_cursorIndexOfInterval);
          final long _tmpNextReviewDate;
          _tmpNextReviewDate = cursor.getLong(_cursorIndexOfNextReviewDate);
          final boolean _tmpIsOfflineCached;
          final int _tmp;
          _tmp = cursor.getInt(_cursorIndexOfIsOfflineCached);
          _tmpIsOfflineCached = _tmp != 0;
          final boolean _tmpIsPendingSync;
          final int _tmp_1;
          _tmp_1 = cursor.getInt(_cursorIndexOfIsPendingSync);
          _tmpIsPendingSync = _tmp_1 != 0;
          _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, FlashcardEntity> getFlashcardsPagedByCategory(
      final String category) {
    final String _sql = "SELECT * FROM flashcards WHERE category = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return new LimitOffsetPagingSource<FlashcardEntity>(_statement, __db, "flashcards") {
      @Override
      @NonNull
      protected List<FlashcardEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(cursor, "term");
        final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(cursor, "definition");
        final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(cursor, "category");
        final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(cursor, "mediaUrl");
        final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(cursor, "mediaType");
        final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(cursor, "contributor");
        final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "createdAt");
        final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(cursor, "reviewState");
        final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(cursor, "easeFactor");
        final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(cursor, "interval");
        final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(cursor, "nextReviewDate");
        final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(cursor, "isOfflineCached");
        final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(cursor, "isPendingSync");
        final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final FlashcardEntity _item;
          final String _tmpId;
          _tmpId = cursor.getString(_cursorIndexOfId);
          final String _tmpTerm;
          _tmpTerm = cursor.getString(_cursorIndexOfTerm);
          final String _tmpDefinition;
          _tmpDefinition = cursor.getString(_cursorIndexOfDefinition);
          final String _tmpCategory;
          _tmpCategory = cursor.getString(_cursorIndexOfCategory);
          final String _tmpMediaUrl;
          if (cursor.isNull(_cursorIndexOfMediaUrl)) {
            _tmpMediaUrl = null;
          } else {
            _tmpMediaUrl = cursor.getString(_cursorIndexOfMediaUrl);
          }
          final String _tmpMediaType;
          _tmpMediaType = cursor.getString(_cursorIndexOfMediaType);
          final String _tmpContributor;
          _tmpContributor = cursor.getString(_cursorIndexOfContributor);
          final long _tmpCreatedAt;
          _tmpCreatedAt = cursor.getLong(_cursorIndexOfCreatedAt);
          final String _tmpReviewState;
          _tmpReviewState = cursor.getString(_cursorIndexOfReviewState);
          final float _tmpEaseFactor;
          _tmpEaseFactor = cursor.getFloat(_cursorIndexOfEaseFactor);
          final int _tmpInterval;
          _tmpInterval = cursor.getInt(_cursorIndexOfInterval);
          final long _tmpNextReviewDate;
          _tmpNextReviewDate = cursor.getLong(_cursorIndexOfNextReviewDate);
          final boolean _tmpIsOfflineCached;
          final int _tmp;
          _tmp = cursor.getInt(_cursorIndexOfIsOfflineCached);
          _tmpIsOfflineCached = _tmp != 0;
          final boolean _tmpIsPendingSync;
          final int _tmp_1;
          _tmp_1 = cursor.getInt(_cursorIndexOfIsPendingSync);
          _tmpIsPendingSync = _tmp_1 != 0;
          _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public Flow<List<FlashcardEntity>> getStudyQueue() {
    final String _sql = "SELECT * FROM flashcards WHERE reviewState IN ('NEW', 'LEARNING', 'REVIEW') ORDER BY nextReviewDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<FlashcardEntity>>() {
      @Override
      @NonNull
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "term");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewState");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(_cursor, "isOfflineCached");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTerm;
            _tmpTerm = _cursor.getString(_cursorIndexOfTerm);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpReviewState;
            _tmpReviewState = _cursor.getString(_cursorIndexOfReviewState);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextReviewDate;
            _tmpNextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            final boolean _tmpIsOfflineCached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOfflineCached);
            _tmpIsOfflineCached = _tmp != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<FlashcardEntity>> getArchivedFlashcards() {
    final String _sql = "SELECT * FROM flashcards WHERE reviewState = 'ARCHIVED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<FlashcardEntity>>() {
      @Override
      @NonNull
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "term");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewState");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(_cursor, "isOfflineCached");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTerm;
            _tmpTerm = _cursor.getString(_cursorIndexOfTerm);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpReviewState;
            _tmpReviewState = _cursor.getString(_cursorIndexOfReviewState);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextReviewDate;
            _tmpNextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            final boolean _tmpIsOfflineCached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOfflineCached);
            _tmpIsOfflineCached = _tmp != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getFlashcardById(final String id,
      final Continuation<? super FlashcardEntity> $completion) {
    final String _sql = "SELECT * FROM flashcards WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FlashcardEntity>() {
      @Override
      @Nullable
      public FlashcardEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "term");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewState");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(_cursor, "isOfflineCached");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final FlashcardEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTerm;
            _tmpTerm = _cursor.getString(_cursorIndexOfTerm);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpReviewState;
            _tmpReviewState = _cursor.getString(_cursorIndexOfReviewState);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextReviewDate;
            _tmpNextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            final boolean _tmpIsOfflineCached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOfflineCached);
            _tmpIsOfflineCached = _tmp != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _result = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countByTerm(final String term, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM flashcards WHERE LOWER(term) = LOWER(?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, term);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FlashcardEntity>> getByCategory(final String category) {
    final String _sql = "SELECT * FROM flashcards WHERE category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flashcards"}, new Callable<List<FlashcardEntity>>() {
      @Override
      @NonNull
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "term");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewState");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(_cursor, "isOfflineCached");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTerm;
            _tmpTerm = _cursor.getString(_cursorIndexOfTerm);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpReviewState;
            _tmpReviewState = _cursor.getString(_cursorIndexOfReviewState);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextReviewDate;
            _tmpNextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            final boolean _tmpIsOfflineCached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOfflineCached);
            _tmpIsOfflineCached = _tmp != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPendingSync(final Continuation<? super List<FlashcardEntity>> $completion) {
    final String _sql = "SELECT * FROM flashcards WHERE isPendingSync = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FlashcardEntity>>() {
      @Override
      @NonNull
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "term");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewState");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(_cursor, "isOfflineCached");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTerm;
            _tmpTerm = _cursor.getString(_cursorIndexOfTerm);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpReviewState;
            _tmpReviewState = _cursor.getString(_cursorIndexOfReviewState);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextReviewDate;
            _tmpNextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            final boolean _tmpIsOfflineCached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOfflineCached);
            _tmpIsOfflineCached = _tmp != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchFlashcards(final String query,
      final Continuation<? super List<FlashcardEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM flashcards\n"
            + "        WHERE LOWER(term) LIKE '%' || LOWER(?) || '%'\n"
            + "           OR LOWER(definition) LIKE '%' || LOWER(?) || '%'\n"
            + "        ORDER BY\n"
            + "            CASE WHEN LOWER(term) LIKE LOWER(?) || '%' THEN 0 ELSE 1 END,\n"
            + "            createdAt DESC\n"
            + "        LIMIT 30\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FlashcardEntity>>() {
      @Override
      @NonNull
      public List<FlashcardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTerm = CursorUtil.getColumnIndexOrThrow(_cursor, "term");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfMediaType = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaType");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfReviewState = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewState");
          final int _cursorIndexOfEaseFactor = CursorUtil.getColumnIndexOrThrow(_cursor, "easeFactor");
          final int _cursorIndexOfInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "interval");
          final int _cursorIndexOfNextReviewDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewDate");
          final int _cursorIndexOfIsOfflineCached = CursorUtil.getColumnIndexOrThrow(_cursor, "isOfflineCached");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<FlashcardEntity> _result = new ArrayList<FlashcardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FlashcardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTerm;
            _tmpTerm = _cursor.getString(_cursorIndexOfTerm);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpMediaType;
            _tmpMediaType = _cursor.getString(_cursorIndexOfMediaType);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpReviewState;
            _tmpReviewState = _cursor.getString(_cursorIndexOfReviewState);
            final float _tmpEaseFactor;
            _tmpEaseFactor = _cursor.getFloat(_cursorIndexOfEaseFactor);
            final int _tmpInterval;
            _tmpInterval = _cursor.getInt(_cursorIndexOfInterval);
            final long _tmpNextReviewDate;
            _tmpNextReviewDate = _cursor.getLong(_cursorIndexOfNextReviewDate);
            final boolean _tmpIsOfflineCached;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOfflineCached);
            _tmpIsOfflineCached = _tmp != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new FlashcardEntity(_tmpId,_tmpTerm,_tmpDefinition,_tmpCategory,_tmpMediaUrl,_tmpMediaType,_tmpContributor,_tmpCreatedAt,_tmpReviewState,_tmpEaseFactor,_tmpInterval,_tmpNextReviewDate,_tmpIsOfflineCached,_tmpIsPendingSync);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCategoryMastery(
      final Continuation<? super List<CategoryMasteryRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT category,\n"
            + "               COUNT(*) AS total,\n"
            + "               SUM(CASE WHEN reviewState = 'ARCHIVED' THEN 1 ELSE 0 END) AS archived\n"
            + "        FROM flashcards\n"
            + "        GROUP BY category\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CategoryMasteryRow>>() {
      @Override
      @NonNull
      public List<CategoryMasteryRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategory = 0;
          final int _cursorIndexOfTotal = 1;
          final int _cursorIndexOfArchived = 2;
          final List<CategoryMasteryRow> _result = new ArrayList<CategoryMasteryRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryMasteryRow _item;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final int _tmpArchived;
            _tmpArchived = _cursor.getInt(_cursorIndexOfArchived);
            _item = new CategoryMasteryRow(_tmpCategory,_tmpTotal,_tmpArchived);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDueCount(final long now, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM flashcards WHERE nextReviewDate <= ? AND reviewState IN ('NEW', 'LEARNING', 'REVIEW')";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM flashcards";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
