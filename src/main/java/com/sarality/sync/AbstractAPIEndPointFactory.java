package com.sarality.sync;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;

/**
 * Factory for generating a doThat API Endpoint object
 *
 * @author Satya@ (Satya Puniani)
 */

public interface AbstractAPIEndPointFactory<T extends AbstractGoogleJsonClient, H extends HttpRequestInitializer> {

  public T getAPIEndPoint(H initializer);

}
