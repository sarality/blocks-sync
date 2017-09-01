package com.sarality.sync;

import com.sarality.sync.data.APISyncErrorLocation;
import com.sarality.sync.data.BaseAPISyncErrorCode;
import com.sarality.sync.data.SyncErrorData;
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
  private final APISyncSourceCollator<T, S> collator;
  private final APISyncRequestGenerator<S, R> requestGenerator;
  private final APICallExecutor<S, R> apiExecutor;
  private TaskProgressListener<SyncProgressCount> progressListener;
  private List<SyncErrorData> syncErrorList = new ArrayList<>();
  private boolean hasErrors = false;


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

  public boolean execute(TaskProgressListener<SyncProgressCount> progressListener,
      TaskCompletionListener<SyncProgressCount> completionListener) {
    this.progressListener = progressListener;
    hasErrors = false;
    syncErrorList = new ArrayList<>();

    int pageCount = fetcher.init();

    List<T> sourceDataList = fetcher.fetchNext();
    // TODO (@Satya) check if there were errors in fetcher and add to error list.

    int currentPage = 0;
    int progressCount = 0;
    int itemsOnPage = 0;

    while (sourceDataList != null) {
      List<S> collatedList = collator.collate(sourceDataList);
      // TODO (@Satya) check if there were errors in collator and add to error list.
      itemsOnPage = collatedList.size();

      updateProgress(new SyncProgressCount(++currentPage, pageCount, progressCount, itemsOnPage));

      for (S data : collatedList) {
        R request = null;
        try {
          request = requestGenerator.generateSyncRequest(data);
          // TODO (@Satya) check if there were errors and add them to the error list.
        } catch (Throwable t) {
          hasErrors = true;
          addError(new SyncErrorData(APISyncErrorLocation.API_SYNC_EXECUTE,
              BaseAPISyncErrorCode.NO_REQUEST_GENERATED, t));
        }
        if (request != null) {
          try {
            apiExecutor.init(data, request);
          } catch (IOException e) {
            logger.info("[SYNC-ERROR] IOError from apiExecutor for request object {} for source {}: " + e.toString(),
                request.toString(),
                data.toString());
            hasErrors = true;
            addError(new SyncErrorData(APISyncErrorLocation.API_SYNC_EXECUTE,
                BaseAPISyncErrorCode.IO_EXCEPTION, e));
          }

          try {
            if (!apiExecutor.execute()) {
              // TODO (@Satya) retry based on error type.
              hasErrors = true;
              logger.info("[SYNC-ERROR] Error in API call for request {} for source {}",
                  request.toString(),
                  data.toString());
              addErrors(apiExecutor.getSyncErrors());
            }
          } catch (Throwable t) {
            hasErrors = true;
            addError(new SyncErrorData(APISyncErrorLocation.API_SYNC_EXECUTE,
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

    return !hasErrors;
  }

  @Override
  public void updateProgress(SyncProgressCount progress) {
    if (progressListener != null) {
      progressListener.onProgressUpdate(progress);
    }
  }

  private void addError(SyncErrorData errorData) {
    syncErrorList.add(errorData);
  }

  private void addErrors(List<SyncErrorData> errorDataList) {
    syncErrorList.addAll(errorDataList);
  }

  public List<SyncErrorData> getSyncErrors() {
    return syncErrorList;
  }

  public boolean hasErrors() {
    return hasErrors;
  }

}
