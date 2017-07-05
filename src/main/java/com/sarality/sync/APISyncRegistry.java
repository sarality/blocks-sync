package com.sarality.sync;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for API Sync Executors
 *
 * @author satya@ (Satya Puniani)
 */

public class APISyncRegistry {

  private final Map<String, APISyncExecutor> syncRegistryMap = new HashMap<>();

  public final void registerSyncExecutor(String tableName, APISyncExecutor syncExecutor) {
    syncRegistryMap.put(tableName, syncExecutor);
  }

  public final APISyncExecutor getSyncExecutor(String tableName) {
    return syncRegistryMap.get(tableName);
  }

  public final void init() {
    syncRegistryMap.clear();
  }

}
