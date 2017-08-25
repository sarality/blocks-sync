package com.sarality.sync.data;

/**
 * Classifying the errors in sync
 *
 * @author satya (satya puniani)
 */

public enum APISyncErrorLocation {
  API_SYNC_EXECUTE,
  FETCHER,
  LOADER,
  COLLATOR,
  REQUEST_GENERATOR,
  API_CALL_EXECUTOR,
  RESPONSE_HANDLER
}
