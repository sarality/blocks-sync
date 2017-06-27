package com.sarality.sync;

import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * Interface for sync data fetchers - which provide a list of domain objects T that are unsynced since syncdate.
 *
 * @author Satya@ (Satya Puniani)
 */

public interface APISyncDataFetcher<T> {

  /**
   * Initializes the fetcher.
   * @param lastSyncTimeStamp Timestamp of the last successful sync
   * @param currentSyncTimeStamp Timestamp for the current sync to prevent race condition
   */
  void init(DateTime lastSyncTimeStamp, DateTime currentSyncTimeStamp);


  /**
   * Returns a page of results
   * @return List of data objects. null if no more results.
   */
  List<T> fetchNext();


}
