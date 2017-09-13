package com.sarality.sync;

import java.util.List;

/**
 * Collecting and Combining all the source data needed for generating the request data for the API call
 *
 * @author Satya@ (Satya Puniani)
 */

public interface APISyncSourceCollator<T, S, X> {

  List<S> collate(List<T> dataList);

  List<X> getSyncErrors();

  boolean hasErrors();


}
