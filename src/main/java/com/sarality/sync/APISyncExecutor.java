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

public class APISyncExecutor<T, S, R> {

  private final APISyncDataFetcher<T> fetcher;
  private final APISyncSourceCollator<T, S> collator;
  private final APISyncRequestGenerator<S, R> requestGenerator;
  private final APICallExecutor<S, R> apiExecutor;

  public APISyncExecutor(APISyncDataFetcher<T> fetcher,
      APISyncSourceCollator<T, S> collator,
      APISyncRequestGenerator<S, R> requestGenerator,
      APICallExecutor<S, R> apiExecutor) {
    this.fetcher = fetcher;
    this.requestGenerator = requestGenerator;
    this.apiExecutor = apiExecutor;
    this.collator = collator;
  }

  public DateTime execute(DateTime lastSyncTimeStamp) {

    DateTime currentSyncTimeStamp = DateTime.now(TimeZone.getDefault());

    fetcher.init(lastSyncTimeStamp, currentSyncTimeStamp);

    List<T> sourceDataList = fetcher.fetchNext();
    while (sourceDataList != null) {
      List<S> collatedList = collator.collate(sourceDataList);

      for (S data : collatedList) {
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
    }

    return currentSyncTimeStamp;

  }

}
