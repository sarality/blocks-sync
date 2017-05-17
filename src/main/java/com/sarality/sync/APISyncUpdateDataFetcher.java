package com.sarality.sync;

import com.sarality.db.Column;
import com.sarality.db.Table;
import com.sarality.db.query.Operator;
import com.sarality.db.query.Query;
import com.sarality.db.query.SimpleQueryBuilder;
import com.sarality.db.query.SimpleQueryClause;

import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * Sync data fetcher for updated records - which have a global Id and were updated after last sync time
 *
 * @author Satya@ (Satya Puniani)
 */

public class APISyncUpdateDataFetcher<T> implements APISyncDataFetcher<T> {

  private final Table<T> table;
  private final Column globalIdColumn;
  private final Column timeStampColumn;

  public APISyncUpdateDataFetcher(Table<T> table, Column globalIdColumn, Column timeStampColumn) {
    this.table = table;
    this.globalIdColumn = globalIdColumn;
    this.timeStampColumn = timeStampColumn;
  }

  
  @Override
  public List<T> fetch(DateTime syncDate) {
    Query newQuery = new SimpleQueryBuilder()
        .withFilter(new SimpleQueryClause(globalIdColumn, Operator.IS_NOT_NULL))
        .withFilter(timeStampColumn, Operator.GREATER_THAN_EQUAL_TO, syncDate)
        .build();

    return table.readAll(newQuery);
  }
}
