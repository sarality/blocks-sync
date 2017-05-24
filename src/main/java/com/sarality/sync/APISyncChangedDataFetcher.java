package com.sarality.sync;

import com.sarality.db.Column;
import com.sarality.db.Table;
import com.sarality.db.query.Operator;
import com.sarality.db.query.Query;
import com.sarality.db.query.SimpleQueryBuilder;

import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * Sync data fetcher for all records created or updated after last sync timestamp and upto current sync timestamp
 *
 * @author Satya@ (Satya Puniani)
 */

public class APISyncChangedDataFetcher<T> implements APISyncDataFetcher<T> {

  private final Table<T> table;
  private final Column modifiedTimeStampColumn;

  public APISyncChangedDataFetcher(Table<T> table, Column modifiedTimeStampColumn) {
    this.table = table;
    this.modifiedTimeStampColumn = modifiedTimeStampColumn;
  }

  @Override
  public List<T> fetch(DateTime lastSyncTimeStamp, DateTime currentSyncTimeStamp) {
    Query newQuery = new SimpleQueryBuilder()
        .withFilter(modifiedTimeStampColumn, Operator.GREATER_THAN, lastSyncTimeStamp)
        .withFilter(modifiedTimeStampColumn, Operator.LESS_THAN_EQUAL_TO, currentSyncTimeStamp)
        .build();

    return table.readAll(newQuery);
  }
}
