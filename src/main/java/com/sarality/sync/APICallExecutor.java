package com.sarality.sync;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.sarality.sync.data.APISyncErrorLocation;
import com.sarality.sync.data.BaseAPISyncErrorCode;
import com.sarality.sync.data.SyncErrorData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * base class for encapsulating the logic for trying an API Call, handling the response and error cases.
 * Implementors need to provide the API Call that should be attempted and the response handler that can take care of
 * actions based on the response, or if there was an IOException then process the exception.
 *
 * @author satya@ (Satya Puniani)
 */

public abstract class APICallExecutor<S, R> {

  private static Logger logger = LoggerFactory.getLogger(APICallExecutor.class);
  private static int MAX_RETRY_COUNT = 3;

  private S source;
  private R request;

  private List<SyncErrorData> syncErrors = new ArrayList<>();

  public void init(S source, R request) throws IOException {
    this.source = source;
    this.request = request;
    this.syncErrors = new ArrayList<>();
  }

  private void addError(SyncErrorData errorData) {
    syncErrors.add(errorData);
  }

  private void addErrors(List<SyncErrorData> errorDataList) {
    syncErrors.addAll(errorDataList);
  }

  public List<SyncErrorData> getSyncErrors() {
    return syncErrors;
  }

  protected abstract AbstractGoogleJsonClientRequest<R> getApiCall();

  protected abstract APISyncResponseHandler<S, R> getResponseHandler();

  public final boolean execute() {
    //mothing to execute
    if (getApiCall() == null) {
      logger.info("[SYNC-ERROR] No API Call for: {}", request.toString());
      addError(new SyncErrorData(APISyncErrorLocation.API_CALL_EXECUTOR,
          BaseAPISyncErrorCode.NO_API_CALL_INITIALISED,
          String.format(Locale.getDefault(),"No API Call initialized for: %1s", request.toString())));
      return false;
    }

    APISyncResponseType apiSyncResponseType = APISyncResponseType.FAILED_RETRY;
    //protect ourselves from a poor implementation
    int retryCount = 0;

    while (apiSyncResponseType.equals(APISyncResponseType.FAILED_RETRY) && (retryCount++ < MAX_RETRY_COUNT)) {
      // TODO(@Satya) use a request transformer here to handle any changes to request object based on error processing

      try {
        R response = getApiCall().execute();
        apiSyncResponseType = getResponseHandler().process(source, response);
      } catch (IOException e) {
        // TODO(@Satya) request transformer should be set by error processor
        apiSyncResponseType = getResponseHandler().processError(e, source, request);
      }

    }

    if (!apiSyncResponseType.equals(APISyncResponseType.SUCCESS)) {
      addErrors(getResponseHandler().getSyncErrors());
    }
    return apiSyncResponseType.equals(APISyncResponseType.SUCCESS);
  }
}
