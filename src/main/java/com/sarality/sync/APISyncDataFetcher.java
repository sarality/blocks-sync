package com.sarality.sync;

import java.util.List;

/**
 * Interface for sync data fetchers - which provide a list of domain objects T that are unsynced since syncdate.
 *
 * @author Satya@ (Satya Puniani)
 */

public interface APISyncDataFetcher<T> {

  /**
   * Initializes the fetcher.
   *
   * @return number of pages of results
   */
  int init();


  /**
   * Returns a page of results
   *
   * @return List of data objects. null if no more results.
   */
  List<T> fetchNext();


}
