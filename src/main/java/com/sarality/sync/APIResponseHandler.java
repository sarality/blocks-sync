package com.sarality.sync;

/**
 * Interface for handlers for API Responses
 *
 * @author Satya@ (Satya Puniani)
 */

public interface APIResponseHandler<T, R> {

  public T process(T sourceData, R responseData);

}
