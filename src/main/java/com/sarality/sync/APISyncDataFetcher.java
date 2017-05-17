package com.sarality.sync;

import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * Interface for sync data fetchers - which provide a list of domain objects T that are unsynced since syncdate.
 *
 * @author Satya@ (Satya Puniani)
 */

public interface APISyncDataFetcher<T> {

  List<T> fetch(DateTime syncDate);
}
