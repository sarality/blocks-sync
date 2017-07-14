package com.sarality.sync;

/**
 * interface for class loader. Given an id, loads the object and all related objects
 *
 * @author satya@ (Satya Puniani)
 */

public interface SyncDataLoader<T> {

  /**
   *
   * @param entityId: Primary Key for the object to be loaded
   * @return T : fully loaded object with all related objects
   */
  T load(Long entityId);

}
