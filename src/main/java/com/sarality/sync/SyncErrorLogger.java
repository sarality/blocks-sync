package com.sarality.sync;

import hirondelle.date4j.DateTime;

/**
 * Log syncing errors
 *
 * @author satya (satya puniani)
 */

public interface SyncErrorLogger {

  public void logError(Exception e);

  public void logError(Exception e, Long errorRecordId, String errorTableName, DateTime errorTimeStamp,
      String errorDescription);

}
