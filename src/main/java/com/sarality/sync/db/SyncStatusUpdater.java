package com.sarality.sync.db;

import android.content.ContentValues;

import com.sarality.db.Column;
import com.sarality.db.Table;
import com.sarality.db.common.FieldValueGetter;
import com.sarality.db.content.ContentValueWriter;
import com.sarality.db.modifier.LocallyModifiedColumnUpdater;
import com.sarality.db.query.CompoundQueryClause;
import com.sarality.db.query.LogicalOperator;
import com.sarality.db.query.Operator;
import com.sarality.db.query.Query;
import com.sarality.db.query.SimpleQueryBuilder;
import com.sarality.db.query.SimpleQueryClause;

/**
 * Marks data as synced when pushing changes from App to Server
 *
 * @author abhideep@ (Abhideep Singh)
 */
public class SyncStatusUpdater<T, E extends Enum<E>> {

  private final Table<T> table;

  private final FieldValueGetter<T, Long> idGetter;
  private final FieldValueGetter<T, String> globalIdGetter;
  private final FieldValueGetter<T, Long> globalVersionGetter;

  private final Column idColumn;
  private final Column globalIdColumn;
  private final Column globalVersionColumn;
  private final Column locallyModifiedColumn;

  private final E unmodifiedStatusValue;

  public SyncStatusUpdater(Table<T> table,
      FieldValueGetter<T, Long> idGetter,
      FieldValueGetter<T, String> globalIdGetter,
      FieldValueGetter<T, Long> globalVersionGetter,
      Column idColumn,
      Column globalIdColumn,
      Column globalVersionColumn,
      Column locallyModifiedColumn,
      E unmodifiedStatusValue) {
    this.table = table;
    this.idGetter = idGetter;
    this.globalIdGetter = globalIdGetter;
    this.globalVersionGetter = globalVersionGetter;
    this.idColumn = idColumn;
    this.globalIdColumn = globalIdColumn;
    this.globalVersionColumn = globalVersionColumn;
    this.locallyModifiedColumn = locallyModifiedColumn;
    this.unmodifiedStatusValue = unmodifiedStatusValue;
  }

  public void updateSyncStatus(T data) {
    Long entityId = idGetter.getValue(data);
    String globalId = globalIdGetter.getValue(data);
    Long globalVersion = globalVersionGetter.getValue(data);

    ContentValues contentValues = new ContentValues();
    ContentValueWriter writer = new ContentValueWriter(contentValues);
    writer.addString(globalIdColumn, globalId);
    writer.addLong(globalVersionColumn, globalVersion);

    Query query = new SimpleQueryBuilder()
        .withFilter(idColumn, entityId)
        .withFilter(new CompoundQueryClause(LogicalOperator.OR,
            new SimpleQueryClause(globalIdColumn, Operator.EQUALS, globalId),
            new SimpleQueryClause(globalIdColumn, Operator.IS_NULL)))
        .build();
    table.update(contentValues, query,
        new LocallyModifiedColumnUpdater<E>(locallyModifiedColumn, unmodifiedStatusValue));
  }
}
