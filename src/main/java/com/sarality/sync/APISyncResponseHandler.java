package com.sarality.sync;

import com.sarality.sync.data.SyncError;

import java.io.IOException;
import java.util.List;

/**
 * Interface for handlers for API Responses
 *
 * @param <S> transformed domain data object that was being synced
 * @param <R> request object sent to API / response object from the API
 * @author Satya@ (Satya Puniani)
 */

public interface APISyncResponseHandler<S, R>  {

  APISyncResponseType process(S sourceData, R responseData);

  APISyncResponseType processError(IOException e, S sourceData, R requestData);

  List<SyncError> getSyncErrors();

  boolean hasErrors();

}
