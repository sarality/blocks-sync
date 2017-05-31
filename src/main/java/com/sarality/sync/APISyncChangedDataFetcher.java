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
  private DateTime lastSyncTimeStamp;
  private DateTime currentSyncTimeStamp;
  private boolean isAfterLast;

  public APISyncChangedDataFetcher(Table<T> table, Column modifiedTimeStampColumn) {
    this.table = table;
    this.modifiedTimeStampColumn = modifiedTimeStampColumn;
  }

  @Override
  public void init(DateTime lastSyncTimeStamp, DateTime currentSyncTimeStamp) {
    this.lastSyncTimeStamp = lastSyncTimeStamp;
    this.currentSyncTimeStamp = currentSyncTimeStamp;
    this.isAfterLast = false;
  }

  @Override
  public List<T> fetchNext() {
    if (isAfterLast) {
      return null;
    }
    isAfterLast = true;
    return fetchAll();
  }

  private List<T> fetchAll() {
    try {
      table.open();

      Query newQuery = new SimpleQueryBuilder()
          .withFilter(modifiedTimeStampColumn, Operator.GREATER_THAN, lastSyncTimeStamp)
          .withFilter(modifiedTimeStampColumn, Operator.LESS_THAN_EQUAL_TO, currentSyncTimeStamp)
          .build();

      return table.readAll(newQuery);
    } finally {
      table.close();
    }
  }

}
