package com.sarality.sync;

import com.sarality.sync.data.APISyncErrorLocation;
import com.sarality.sync.data.BaseAPISyncErrorCode;
import com.sarality.sync.data.SyncError;
import com.sarality.task.TaskCompletionListener;
import com.sarality.task.TaskProgressListener;
import com.sarality.task.TaskProgressPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
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
  private final APISyncSourceCollator<T, S, SyncError> collator;
  private final APISyncRequestGenerator<S, R, SyncError> requestGenerator;
  private final APICallExecutor<S, R> apiExecutor;
  private TaskProgressListener<SyncProgressCount> progressListener;
  private BaseAPISyncErrorCollector<SyncError> collector = new BaseAPISyncErrorCollector<>();

  public APISyncExecutor(APISyncDataFetcher<T> fetcher,
      APISyncSourceCollator<T, S, SyncError> collator,
      APISyncRequestGenerator<S, R, SyncError> requestGenerator,
      APICallExecutor<S, R> apiExecutor) {
    this.fetcher = fetcher;
    this.requestGenerator = requestGenerator;
    this.apiExecutor = apiExecutor;
    this.collator = collator;
  }

  public void execute() {
    execute(null, null);
  }

  public boolean execute(TaskProgressListener<SyncProgressCount> progressListener,
      TaskCompletionListener<SyncProgressCount> completionListener) {
    this.progressListener = progressListener;
    collector.initErrorList();

    int pageCount = fetcher.init();

    List<T> sourceDataList = fetcher.fetchNext();
    // TODO (@Satya) check if there were errors in fetcher and add to error list.

    int currentPage = 0;
    int progressCount = 0;
    int itemsOnPage = 0;

    while (sourceDataList != null) {
      List<S> collatedList = null;
      itemsOnPage = 0;
      try {
        collatedList = collator.collate(sourceDataList);
        // TODO (@Satya) check if there were errors in collator and add to error list.
      } catch (Throwable t) {
        collector.addError(new SyncError(APISyncErrorLocation.COLLATOR,
            BaseAPISyncErrorCode.RUN_TIME_EXCEPTION, t));
      }

      if (collatedList != null) {
        itemsOnPage = collatedList.size();
      } else {
        collatedList = new ArrayList<>();
      }

      updateProgress(new SyncProgressCount(++currentPage, pageCount, progressCount, itemsOnPage));

      for (S data : collatedList) {
        R request = null;
        try {
          request = requestGenerator.generateSyncRequest(data);
          if (requestGenerator.hasErrors()) {
            collector.addErrors(requestGenerator.getSyncErrors());
          }
        } catch (Throwable t) {
          collector.addError(new SyncError(APISyncErrorLocation.API_SYNC_EXECUTE,
              BaseAPISyncErrorCode.NO_REQUEST_GENERATED, t));
        }
        if (request != null) {
          try {
            apiExecutor.init(data, request);
          } catch (IOException e) {
            logger.info("[SYNC-ERROR] IOError from apiExecutor for request object {} for source {}: " + e.toString(),
                request.toString(),
                data.toString());
            collector.addError(new SyncError(APISyncErrorLocation.API_SYNC_EXECUTE,
                BaseAPISyncErrorCode.IO_EXCEPTION, e));
          }

          try {
            if (!apiExecutor.execute()) {
              // TODO (@Satya) retry based on error type.
              logger.info("[SYNC-ERROR] Error in API call for request {} for source {}",
                  request.toString(),
                  data.toString());
              collector.addErrors(apiExecutor.getSyncErrors());
            }
          } catch (Throwable t) {
            collector.addError(new SyncError(APISyncErrorLocation.API_SYNC_EXECUTE,
                BaseAPISyncErrorCode.RUN_TIME_EXCEPTION, t));
          }
        }

        if (++progressCount % PROGRESS_INTERVAL == 0) {
          updateProgress(new SyncProgressCount(currentPage, pageCount, progressCount, itemsOnPage));
        }
      }
      sourceDataList = fetcher.fetchNext();
      // TODO (@Satya) check if there were errors in fetcher and add to error list.
    }
    if (completionListener != null) {
      completionListener.onComplete(new SyncProgressCount(currentPage, pageCount, progressCount, itemsOnPage));
    }

    return !collector.hasErrors();
  }

  @Override
  public void updateProgress(SyncProgressCount progress) {
    if (progressListener != null) {
      progressListener.onProgressUpdate(progress);
    }
  }

  public List<SyncError> getSyncErrors() {
    return collector.getSyncErrors();
  }

  public boolean hasErrors() {
    return collector.hasErrors();
  }


}
