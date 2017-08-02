package com.sarality.sync;

import com.sarality.task.TaskCompletionListener;
import com.sarality.task.TaskProgressListener;
import com.sarality.task.TaskProgressPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Executes an API Sync by calling the appropriate Endpoint action for each item in the source list
 *
 * @author Satya@ (Satya Puniani)
 */

public class APISyncExecutor<T, S, R> implements TaskProgressPublisher<SyncProgressCount> {

  private static final Logger logger = LoggerFactory.getLogger(APISyncExecutor.class.getSimpleName());
  private static final int PROGRESS_INTERVAL = 10;

  private final APISyncDataFetcher<T> fetcher;
  private final APISyncSourceCollator<T, S> collator;
  private final APISyncRequestGenerator<S, R> requestGenerator;
  private final APICallExecutor<S, R> apiExecutor;
  private TaskProgressListener<SyncProgressCount> progressListener;

  public APISyncExecutor(APISyncDataFetcher<T> fetcher,
      APISyncSourceCollator<T, S> collator,
      APISyncRequestGenerator<S, R> requestGenerator,
      APICallExecutor<S, R> apiExecutor) {
    this.fetcher = fetcher;
    this.requestGenerator = requestGenerator;
    this.apiExecutor = apiExecutor;
    this.collator = collator;
  }

  public void execute() {
    execute(null, null);
  }

  public void execute(TaskProgressListener<SyncProgressCount> progressListener,
      TaskCompletionListener<SyncProgressCount> completionListener) {
    this.progressListener = progressListener;

    int pageCount = fetcher.init();


    List<T> sourceDataList = fetcher.fetchNext();
    int currentPage = 0;
    int progressCount = 0;
    int itemsOnPage = 0;

    while (sourceDataList != null) {
      List<S> collatedList = collator.collate(sourceDataList);
      itemsOnPage = collatedList.size();

      updateProgress(new SyncProgressCount(++currentPage, pageCount, progressCount, itemsOnPage));

      for (S data : collatedList) {
        R request = requestGenerator.generateSyncRequest(data);
        if (request != null) {
          try {
            apiExecutor.init(data, request);
          } catch (IOException e) {
            // TODO (@Satya) if executor could not init - what next?
            logger.info("[SYNC-ERROR] IOError from apiExecutor for request object {} for source {}: " + e.toString(),
                request.toString(),
                data.toString());
          }

          if (!apiExecutor.execute()) {
            // TODO (@Satya) if failed, should increment error count and then determine if we should continue or abort sync
            // because error count is too high. maintain error/retry queue
            logger.info("[SYNC-ERROR] Error in API call for request {} for source {}",
                request.toString(),
                data.toString());
          }
        } else {
          logger.info("[SYNC-ERROR] Skipped. Request object is null for source " + data.toString());
        }

        if (++progressCount % PROGRESS_INTERVAL == 0) {
          updateProgress(new SyncProgressCount(currentPage, pageCount, progressCount, itemsOnPage));
        }
      }
      sourceDataList = fetcher.fetchNext();
    }
    if (completionListener != null) {
      completionListener.onComplete(new SyncProgressCount(currentPage, pageCount, progressCount, itemsOnPage));
    }
  }

  @Override
  public void updateProgress(SyncProgressCount progress) {
    if (progressListener != null) {
      progressListener.onProgressUpdate(progress);
    }
  }
}
