package com.sarality.sync;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpResponseException;
import com.sarality.sync.data.APISyncErrorLocation;
import com.sarality.sync.data.BaseAPISyncErrorCode;
import com.sarality.sync.data.SyncError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

  private static final int HTTP_STATUS_CODE_CONFLICT = 409;

  private static Logger logger = LoggerFactory.getLogger(APICallExecutor.class);
  private static int MAX_RETRY_COUNT = 3;

  private S source;
  private R request;
  private BaseAPISyncErrorCollector<SyncError> collector = new BaseAPISyncErrorCollector<>();

  public void init(S source, R request) throws IOException {
    this.collector.initErrorList();
    this.source = source;
    this.request = request;
  }

  protected abstract AbstractGoogleJsonClientRequest<R> getApiCall();

  protected abstract APISyncResponseHandler<S, R> getResponseHandler();

  public final boolean execute() {
    //mothing to execute
    if (getApiCall() == null) {
      logger.info("[SYNC-ERROR] No API Call for: {}", request.toString());
      collector.addError(new SyncError(APISyncErrorLocation.API_CALL_EXECUTOR,
          BaseAPISyncErrorCode.NO_API_CALL_INITIALISED,
          String.format(Locale.getDefault(), "No API Call initialized for: %1s", request.toString())));
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
      } catch (HttpResponseException hre) {
        int statusCode = hre.getStatusCode();
        if (statusCode == HTTP_STATUS_CODE_CONFLICT) {
          apiSyncResponseType = getResponseHandler().processConflict(hre, source, request);
        } else {
          // TODO(@Satya) request transformer should be set by error processor
          apiSyncResponseType = getResponseHandler().processError(hre, source, request);
        }
      } catch (IOException e) {
        // TODO(@Satya) request transformer should be set by error processor
        apiSyncResponseType = getResponseHandler().processError(e, source, request);
      }

    }

    if (apiSyncResponseType.equals(APISyncResponseType.SUCCESS)
        || apiSyncResponseType.equals(APISyncResponseType.CONFLICT)) {
      return true;
    } else {
      collector.addErrors(getResponseHandler().getSyncErrors());
      return false;
    }
  }

  public boolean hasErrors() {
    return collector.hasErrors();
  }

  public List<SyncError> getSyncErrors() {
    return collector.getSyncErrors();
  }
}
