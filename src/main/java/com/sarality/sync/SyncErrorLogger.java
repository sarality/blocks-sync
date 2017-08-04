package com.sarality.sync;

/**
 * Log syncing errors
 *
 * @author satya (satya puniani)
 */

public interface SyncErrorLogger {

  public void logError(Exception e);

  public void logError(Exception e, Long syncRecordId, String syncTableName);

}
