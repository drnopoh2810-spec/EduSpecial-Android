package com.eduspecial.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.eduspecial.data.local.entities.DailyReviewLogEntity;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AnalyticsDao_Impl implements AnalyticsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyReviewLogEntity> __insertionAdapterOfDailyReviewLogEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementLog;

  public AnalyticsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyReviewLogEntity = new EntityInsertionAdapter<DailyReviewLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_review_logs` (`dayEpoch`,`reviewCount`,`archivedCount`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyReviewLogEntity entity) {
        statement.bindLong(1, entity.getDayEpoch());
        statement.bindLong(2, entity.getReviewCount());
        statement.bindLong(3, entity.getArchivedCount());
      }
    };
    this.__preparedStmtOfIncrementLog = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE daily_review_logs\n"
                + "        SET reviewCount = reviewCount + ?, archivedCount = archivedCount + ?\n"
                + "        WHERE dayEpoch = ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object upsertLog(final DailyReviewLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyReviewLogEntity.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementLog(final long dayEpoch, final int delta, final int archived,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementLog.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, delta);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, archived);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, dayEpoch);
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
          __preparedStmtOfIncrementLog.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getLogsFrom(final long fromDay,
      final Continuation<? super List<DailyReviewLogEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_review_logs WHERE dayEpoch >= ? ORDER BY dayEpoch ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fromDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyReviewLogEntity>>() {
      @Override
      @NonNull
      public List<DailyReviewLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDayEpoch = CursorUtil.getColumnIndexOrThrow(_cursor, "dayEpoch");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfArchivedCount = CursorUtil.getColumnIndexOrThrow(_cursor, "archivedCount");
          final List<DailyReviewLogEntity> _result = new ArrayList<DailyReviewLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyReviewLogEntity _item;
            final long _tmpDayEpoch;
            _tmpDayEpoch = _cursor.getLong(_cursorIndexOfDayEpoch);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final int _tmpArchivedCount;
            _tmpArchivedCount = _cursor.getInt(_cursorIndexOfArchivedCount);
            _item = new DailyReviewLogEntity(_tmpDayEpoch,_tmpReviewCount,_tmpArchivedCount);
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
  public Object getLast7Days(final Continuation<? super List<DailyReviewLogEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_review_logs ORDER BY dayEpoch DESC LIMIT 7";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyReviewLogEntity>>() {
      @Override
      @NonNull
      public List<DailyReviewLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDayEpoch = CursorUtil.getColumnIndexOrThrow(_cursor, "dayEpoch");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfArchivedCount = CursorUtil.getColumnIndexOrThrow(_cursor, "archivedCount");
          final List<DailyReviewLogEntity> _result = new ArrayList<DailyReviewLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyReviewLogEntity _item;
            final long _tmpDayEpoch;
            _tmpDayEpoch = _cursor.getLong(_cursorIndexOfDayEpoch);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final int _tmpArchivedCount;
            _tmpArchivedCount = _cursor.getInt(_cursorIndexOfArchivedCount);
            _item = new DailyReviewLogEntity(_tmpDayEpoch,_tmpReviewCount,_tmpArchivedCount);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
