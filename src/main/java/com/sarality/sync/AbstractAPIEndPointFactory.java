package com.sarality.sync;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;

/**
 * Factory for generating a doThat API Endpoint object
 *
 * @author Satya@ (Satya Puniani)
 */

public interface AbstractAPIEndPointFactory<T extends AbstractGoogleJsonClient> {

  public T getAPIEndPoint();

}
