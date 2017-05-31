package com.sarality.sync;

import java.util.List;

/**
 * Collecting and Combining all the source data needed for generating the request data for the API call
 *
 * @author Satya@ (Satya Puniani)
 */

public interface APISyncSourceCollator<T, S> {

  public List<S> collate(List<T> dataList);

  // TODO(@Satya) implement a globalId Manager for setting globalId for localId of objects
}
