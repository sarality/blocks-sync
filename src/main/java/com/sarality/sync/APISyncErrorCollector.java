package com.sarality.sync;

import java.util.List;

/**
 * collects errors during sync execution then makes the collection available for processing
 *
 * @author satya (satya puniani)
 */

public interface APISyncErrorCollector<T> {

  void initErrorList();

  void addError(T errorData);

  void addErrors(List<T> errorDataList);

  List<T> getSyncErrors();

  boolean hasErrors();

}
