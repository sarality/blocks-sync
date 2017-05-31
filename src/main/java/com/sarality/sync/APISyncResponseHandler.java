package com.sarality.sync;

import java.io.IOException;

/**
 * Interface for handlers for API Responses
 *
 * @param <S> transformed domain data object that was being synced
 * @param <R> request object sent to API / response object from the API
 * @author Satya@ (Satya Puniani)
 */

public interface APISyncResponseHandler<S, R> {

  public APISyncResponseType process(S sourceData, R responseData);

  public APISyncResponseType processError(IOException e, R requestData);

}
