package com.sarality.sync;

/**
 * enum for determining the outcome of an API Sync Request call
 *
 * @author Satya@ (Satya Puniani)
 */

public enum APISyncResponseType {
  SUCCESS,
  CONFLICT,
  FAILED_RETRY,
  FAILED_ABORT;
}
