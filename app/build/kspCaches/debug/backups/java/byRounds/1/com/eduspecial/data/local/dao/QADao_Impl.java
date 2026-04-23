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
import com.eduspecial.data.local.entities.QAAnswerEntity;
import com.eduspecial.data.local.entities.QAQuestionEntity;
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
public final class QADao_Impl implements QADao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QAQuestionEntity> __insertionAdapterOfQAQuestionEntity;

  private final EntityInsertionAdapter<QAAnswerEntity> __insertionAdapterOfQAAnswerEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpvoteQuestion;

  private final SharedSQLiteStatement __preparedStmtOfUpvoteAnswer;

  private final SharedSQLiteStatement __preparedStmtOfUpdateQuestion;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAnswer;

  private final SharedSQLiteStatement __preparedStmtOfAcceptAnswer;

  private final SharedSQLiteStatement __preparedStmtOfMarkQuestionAnswered;

  public QADao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQAQuestionEntity = new EntityInsertionAdapter<QAQuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `qa_questions` (`id`,`question`,`category`,`contributor`,`upvotes`,`createdAt`,`isAnswered`,`tags`,`isPendingSync`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QAQuestionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getQuestion());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getContributor());
        statement.bindLong(5, entity.getUpvotes());
        statement.bindLong(6, entity.getCreatedAt());
        final int _tmp = entity.isAnswered() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindString(8, entity.getTags());
        final int _tmp_1 = entity.isPendingSync() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
      }
    };
    this.__insertionAdapterOfQAAnswerEntity = new EntityInsertionAdapter<QAAnswerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `qa_answers` (`id`,`questionId`,`content`,`contributor`,`upvotes`,`isAccepted`,`createdAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QAAnswerEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getQuestionId());
        statement.bindString(3, entity.getContent());
        statement.bindString(4, entity.getContributor());
        statement.bindLong(5, entity.getUpvotes());
        final int _tmp = entity.isAccepted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfUpvoteQuestion = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE qa_questions SET upvotes = upvotes + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpvoteAnswer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE qa_answers SET upvotes = upvotes + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateQuestion = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE qa_questions SET question = ?, category = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAnswer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE qa_answers SET content = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAcceptAnswer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE qa_answers SET isAccepted = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkQuestionAnswered = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE qa_questions SET isAnswered = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertQuestion(final QAQuestionEntity question,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQAQuestionEntity.insert(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuestions(final List<QAQuestionEntity> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQAQuestionEntity.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAnswer(final QAAnswerEntity answer,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQAAnswerEntity.insert(answer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAnswers(final List<QAAnswerEntity> answers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQAAnswerEntity.insert(answers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upvoteQuestion(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpvoteQuestion.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfUpvoteQuestion.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upvoteAnswer(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpvoteAnswer.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfUpvoteAnswer.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateQuestion(final String id, final String question, final String category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateQuestion.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, question);
        _argIndex = 2;
        _stmt.bindString(_argIndex, category);
        _argIndex = 3;
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
          __preparedStmtOfUpdateQuestion.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAnswer(final String id, final String content,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAnswer.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, content);
        _argIndex = 2;
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
          __preparedStmtOfUpdateAnswer.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object acceptAnswer(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAcceptAnswer.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfAcceptAnswer.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markQuestionAnswered(final String questionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkQuestionAnswered.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, questionId);
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
          __preparedStmtOfMarkQuestionAnswered.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<QAQuestionEntity>> getAllQuestions() {
    final String _sql = "SELECT * FROM qa_questions ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"qa_questions"}, new Callable<List<QAQuestionEntity>>() {
      @Override
      @NonNull
      public List<QAQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfUpvotes = CursorUtil.getColumnIndexOrThrow(_cursor, "upvotes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsAnswered = CursorUtil.getColumnIndexOrThrow(_cursor, "isAnswered");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<QAQuestionEntity> _result = new ArrayList<QAQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QAQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final int _tmpUpvotes;
            _tmpUpvotes = _cursor.getInt(_cursorIndexOfUpvotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsAnswered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAnswered);
            _tmpIsAnswered = _tmp != 0;
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new QAQuestionEntity(_tmpId,_tmpQuestion,_tmpCategory,_tmpContributor,_tmpUpvotes,_tmpCreatedAt,_tmpIsAnswered,_tmpTags,_tmpIsPendingSync);
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
  public Flow<List<QAQuestionEntity>> getUnansweredQuestions() {
    final String _sql = "SELECT * FROM qa_questions WHERE isAnswered = 0 ORDER BY upvotes DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"qa_questions"}, new Callable<List<QAQuestionEntity>>() {
      @Override
      @NonNull
      public List<QAQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfUpvotes = CursorUtil.getColumnIndexOrThrow(_cursor, "upvotes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsAnswered = CursorUtil.getColumnIndexOrThrow(_cursor, "isAnswered");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<QAQuestionEntity> _result = new ArrayList<QAQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QAQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final int _tmpUpvotes;
            _tmpUpvotes = _cursor.getInt(_cursorIndexOfUpvotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsAnswered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAnswered);
            _tmpIsAnswered = _tmp != 0;
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new QAQuestionEntity(_tmpId,_tmpQuestion,_tmpCategory,_tmpContributor,_tmpUpvotes,_tmpCreatedAt,_tmpIsAnswered,_tmpTags,_tmpIsPendingSync);
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
  public Flow<List<QAAnswerEntity>> getAnswersForQuestion(final String questionId) {
    final String _sql = "SELECT * FROM qa_answers WHERE questionId = ? ORDER BY isAccepted DESC, upvotes DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, questionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"qa_answers"}, new Callable<List<QAAnswerEntity>>() {
      @Override
      @NonNull
      public List<QAAnswerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuestionId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfUpvotes = CursorUtil.getColumnIndexOrThrow(_cursor, "upvotes");
          final int _cursorIndexOfIsAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "isAccepted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<QAAnswerEntity> _result = new ArrayList<QAAnswerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QAAnswerEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpQuestionId;
            _tmpQuestionId = _cursor.getString(_cursorIndexOfQuestionId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final int _tmpUpvotes;
            _tmpUpvotes = _cursor.getInt(_cursorIndexOfUpvotes);
            final boolean _tmpIsAccepted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAccepted);
            _tmpIsAccepted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new QAAnswerEntity(_tmpId,_tmpQuestionId,_tmpContent,_tmpContributor,_tmpUpvotes,_tmpIsAccepted,_tmpCreatedAt);
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
  public Object countByQuestion(final String question,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM qa_questions WHERE LOWER(question) = LOWER(?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, question);
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
  public Object searchQuestions(final String query,
      final Continuation<? super List<QAQuestionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM qa_questions\n"
            + "        WHERE LOWER(question) LIKE '%' || LOWER(?) || '%'\n"
            + "        ORDER BY upvotes DESC, createdAt DESC\n"
            + "        LIMIT 20\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QAQuestionEntity>>() {
      @Override
      @NonNull
      public List<QAQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfUpvotes = CursorUtil.getColumnIndexOrThrow(_cursor, "upvotes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsAnswered = CursorUtil.getColumnIndexOrThrow(_cursor, "isAnswered");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<QAQuestionEntity> _result = new ArrayList<QAQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QAQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final int _tmpUpvotes;
            _tmpUpvotes = _cursor.getInt(_cursorIndexOfUpvotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsAnswered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAnswered);
            _tmpIsAnswered = _tmp != 0;
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new QAQuestionEntity(_tmpId,_tmpQuestion,_tmpCategory,_tmpContributor,_tmpUpvotes,_tmpCreatedAt,_tmpIsAnswered,_tmpTags,_tmpIsPendingSync);
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
  public Object getPendingSync(final Continuation<? super List<QAQuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM qa_questions WHERE isPendingSync = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QAQuestionEntity>>() {
      @Override
      @NonNull
      public List<QAQuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuestion = CursorUtil.getColumnIndexOrThrow(_cursor, "question");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfContributor = CursorUtil.getColumnIndexOrThrow(_cursor, "contributor");
          final int _cursorIndexOfUpvotes = CursorUtil.getColumnIndexOrThrow(_cursor, "upvotes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsAnswered = CursorUtil.getColumnIndexOrThrow(_cursor, "isAnswered");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final List<QAQuestionEntity> _result = new ArrayList<QAQuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QAQuestionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpQuestion;
            _tmpQuestion = _cursor.getString(_cursorIndexOfQuestion);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpContributor;
            _tmpContributor = _cursor.getString(_cursorIndexOfContributor);
            final int _tmpUpvotes;
            _tmpUpvotes = _cursor.getInt(_cursorIndexOfUpvotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsAnswered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAnswered);
            _tmpIsAnswered = _tmp != 0;
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsPendingSync;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_1 != 0;
            _item = new QAQuestionEntity(_tmpId,_tmpQuestion,_tmpCategory,_tmpContributor,_tmpUpvotes,_tmpCreatedAt,_tmpIsAnswered,_tmpTags,_tmpIsPendingSync);
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
