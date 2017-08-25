package com.sarality.sync.data;

/**
 * Generic errors raised by platform when no specifics are available from application
 *
 * @author satya (satya puniani)
 */

public enum BaseAPISyncErrorCode implements APISyncErrorCode {
  RUN_TIME_EXCEPTION(101),
  IO_EXCEPTION(102),
  NO_REQUEST_GENERATED(103),
  NO_API_CALL_INITIALISED(104);

  private static int baseValue = 10000;
  private int value;

  BaseAPISyncErrorCode(int value) {
    this.value = value;
  }

  @Override
  public int getCode() {
    return value;
  }

}
