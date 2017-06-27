package com.sarality.sync;

import com.sarality.db.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for API Sync Executors
 *
 * @author Satya@ (Satya Puniani)
 */

public class APISyncRegistry {

  private final Map<String, APISyncExecutor> syncRegistryMap = new HashMap<>();

  public final void registerSyncExecutor(String tableName, APISyncExecutor syncExecutor) {
    syncRegistryMap.put(tableName, syncExecutor);
  }

  public final APISyncExecutor getSyncExecutor(String tableName) {
    return syncRegistryMap.get(tableName);
  }

}
