package com.sarality.sync;

import java.util.List;

/**
 * generates API Request objects from the source domain data objects
 *
 * @author satya@ (Satya Puniani)
 */
public interface APIRequestGenerator<T, R> {

  void init(List<T> dataList);

  R getRequest(Long objectId);


}
