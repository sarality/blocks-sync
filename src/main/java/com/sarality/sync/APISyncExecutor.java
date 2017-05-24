package com.sarality.sync;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

/**
 * Executes an API Sync by calling the appropriate Endpoint action for each item in the source list
 *
 * @author Satya@ (Satya Puniani)
 */

public class APISyncExecutor<T, R> {

  private final APISyncDataFetcher<T> fetcher;
  private final APISyncRequestGenerator<T, R> requestGenerator;
  private final APICallExecutor<T, R> apiExecutor;

  public APISyncExecutor(APISyncDataFetcher<T> fetcher,
      APISyncRequestGenerator<T, R> requestGenerator,
      APICallExecutor<T, R> apiExecutor) {
    this.fetcher = fetcher;
    this.requestGenerator = requestGenerator;
    this.apiExecutor = apiExecutor;
  }

  public DateTime execute(DateTime lastSyncTimeStamp) {

    DateTime currentSyncTimeStamp = DateTime.now(TimeZone.getDefault());

    List<T> sourceDataList = fetcher.fetch(lastSyncTimeStamp, currentSyncTimeStamp);
    requestGenerator.init(sourceDataList);

    for (T data : sourceDataList) {
      R request = requestGenerator.generateSyncRequest(data);
      try {
        apiExecutor.init(data, request);
      } catch (IOException e) {
        // TODO (@Satya) if executor could not init - what next?
      }

      // TODO (@Satya) if failed, should increment error count and then determine if we should continue or abort sync
      // because error count is too high. maintain error/retry queue
      apiExecutor.execute();
    }

    return currentSyncTimeStamp;

  }

}
