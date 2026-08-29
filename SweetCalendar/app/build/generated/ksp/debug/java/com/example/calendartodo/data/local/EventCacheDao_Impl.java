package com.example.calendartodo.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EventCacheDao_Impl implements EventCacheDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EventCacheEntity> __insertionAdapterOfEventCacheEntity;

  public EventCacheDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventCacheEntity = new EntityInsertionAdapter<EventCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `event_cache` (`jalaliDate`,`description`,`isHoliday`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventCacheEntity entity) {
        statement.bindString(1, entity.getJalaliDate());
        statement.bindString(2, entity.getDescription());
        final int _tmp = entity.isHoliday() ? 1 : 0;
        statement.bindLong(3, _tmp);
      }
    };
  }

  @Override
  public Object insertAll(final List<EventCacheEntity> events,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEventCacheEntity.insert(events);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EventCacheEntity>> observeForMonth(final String yearMonthPrefix) {
    final String _sql = "SELECT * FROM event_cache WHERE jalaliDate LIKE ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonthPrefix);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"event_cache"}, new Callable<List<EventCacheEntity>>() {
      @Override
      @NonNull
      public List<EventCacheEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsHoliday = CursorUtil.getColumnIndexOrThrow(_cursor, "isHoliday");
          final List<EventCacheEntity> _result = new ArrayList<EventCacheEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventCacheEntity _item;
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpIsHoliday;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsHoliday);
            _tmpIsHoliday = _tmp != 0;
            _item = new EventCacheEntity(_tmpJalaliDate,_tmpDescription,_tmpIsHoliday);
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
  public Flow<List<EventCacheEntity>> observeForDate(final String jalaliDate) {
    final String _sql = "SELECT * FROM event_cache WHERE jalaliDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, jalaliDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"event_cache"}, new Callable<List<EventCacheEntity>>() {
      @Override
      @NonNull
      public List<EventCacheEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJalaliDate = CursorUtil.getColumnIndexOrThrow(_cursor, "jalaliDate");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsHoliday = CursorUtil.getColumnIndexOrThrow(_cursor, "isHoliday");
          final List<EventCacheEntity> _result = new ArrayList<EventCacheEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventCacheEntity _item;
            final String _tmpJalaliDate;
            _tmpJalaliDate = _cursor.getString(_cursorIndexOfJalaliDate);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpIsHoliday;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsHoliday);
            _tmpIsHoliday = _tmp != 0;
            _item = new EventCacheEntity(_tmpJalaliDate,_tmpDescription,_tmpIsHoliday);
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
  public Object cachedDatesForMonth(final String yearMonthPrefix,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT jalaliDate FROM event_cache WHERE jalaliDate LIKE ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonthPrefix);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
