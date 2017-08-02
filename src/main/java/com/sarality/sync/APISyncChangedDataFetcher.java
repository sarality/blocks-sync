package com.sarality.sync;

import com.sarality.db.Column;
import com.sarality.db.Table;
import com.sarality.db.query.LogicalOperator;
import com.sarality.db.query.Operator;
import com.sarality.db.query.Query;
import com.sarality.db.query.SimpleQueryBuilder;
import com.sarality.db.query.SimpleQueryClause;

import java.util.List;

/**
 * Sync data fetcher for all records created or updated after last sync timestamp and upto current sync timestamp
 *
 * @author Satya@ (Satya Puniani)
 */

public class APISyncChangedDataFetcher<T, E extends Enum<E>> implements APISyncDataFetcher<T> {

  private final Table<T> table;
  private final Column locallyModifiedColumn;
  private final E locallyModifiedValue;
  private final Class<E> enumClass;
  private boolean isAfterLast;

  public APISyncChangedDataFetcher(Table<T> table, Column locallyModifiedColumn,
      E locallyModifiedValue, Class<E> enumClass) {
    this.table = table;
    this.locallyModifiedColumn = locallyModifiedColumn;
    this.locallyModifiedValue = locallyModifiedValue;
    this.enumClass = enumClass;
  }

  @Override
  public int init() {
    this.isAfterLast = false;
    return 1;
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

      Query newQuery = new SimpleQueryBuilder(LogicalOperator.OR)
          .withFilter(locallyModifiedColumn, Operator.EQUALS, locallyModifiedValue, enumClass)
          .withFilter(new SimpleQueryClause(locallyModifiedColumn, Operator.IS_NULL))
          .build();

      return table.readAll(newQuery);
    } finally {
      table.close();
    }
  }

}
