package com.sarality.sync;

import java.util.ArrayList;
import java.util.List;

/**
 * collects errors during sync execution then makes the collection available for processing
 *
 * @author satya (satya puniani)
 */

public class BaseAPISyncErrorCollector<T> implements APISyncErrorCollector<T> {

  private List<T> syncErrorsList = new ArrayList<>();

  @Override
  public void initErrorList() {
    syncErrorsList = new ArrayList<>();
  }

  @Override
  public void addError(T errorData) {
    syncErrorsList.add(errorData);
  }

  @Override
  public void addErrors(List<T> errorDataList) {
    syncErrorsList.addAll(errorDataList);
  }

  @Override
  public List<T> getSyncErrors() {
    return syncErrorsList;
  }

  @Override
  public boolean hasErrors() {
    return (syncErrorsList != null && syncErrorsList.size() > 0);
  }

}
