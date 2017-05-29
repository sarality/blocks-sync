package com.sarality.sync;


import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;

/**
 * API Builder Factory to instantiate the builder for any cloud endpoint API
 *
 * @author Satya@ (Satya Puniani)
 */

public abstract class BaseApiBuilderFactory<T extends AbstractGoogleJsonClient.Builder> {

  public abstract T newBuilder();

  public abstract String getRootUrl();

  public T getBuilder() {
    T builder = newBuilder();
    return initBuilder(builder);
  }

  @SuppressWarnings("unchecked")
  public T initBuilder(T builder) {
    return (T) builder
        .setRootUrl(getRootUrl());
  }

}
