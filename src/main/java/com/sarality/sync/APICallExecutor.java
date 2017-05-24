package com.sarality.sync;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * base class for encapsulating the logic for trying an API Call, handling the response and error cases.
 * Implementors need to provide the API Call that should be attempted and the response handler that can take care of
 * actions based on the response, or if there was an IOException then process the exception.
 *
 * @author Satya@ (Satya Puniani)
 */

public abstract class APICallExecutor<T, R> {

  private T source;
  private R request;

  public void init(T source, R request) throws IOException {
    this.source = source;
    this.request = request;
  }

  protected abstract AbstractGoogleJsonClientRequest<R> getApiCall();

  protected abstract APISyncResponseHandler<T, R> getResponseHandler();

  public final boolean execute() {
    APISyncResponseType apiSyncResponseType = APISyncResponseType.FAILED_RETRY;

    while (apiSyncResponseType.equals(APISyncResponseType.FAILED_RETRY)) {
      // TODO(@Satya) use a request transformer here to handle any changes to request object based on error processing

      try {
        R response = getApiCall().execute();
        apiSyncResponseType = getResponseHandler().process(source, response);

      } catch (IOException e) {
        // TODO(@Satya) request transformer should be set by error processor
        apiSyncResponseType = getResponseHandler().processError(e, request);
      }

    }
    return apiSyncResponseType.equals(APISyncResponseType.SUCCESS);
  }
}
