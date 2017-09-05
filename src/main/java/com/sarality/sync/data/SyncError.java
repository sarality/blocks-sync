package com.sarality.sync.data;


import hirondelle.date4j.DateTime;

/**
 * Data object for tracking errors during sync to server
 *
 * @author satya (satya puniani)
 */

public class SyncError {
  private APISyncErrorLocation errorLocation;
  private APISyncErrorCode errorCode;
  private String tableName;
  private Long syncRecordId;
  private String errorDetail;
  private int errorCodeValue;
  private Long syncErrorId;
  private DateTime errorTimeStamp;


  public SyncError(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, Throwable t) {
    this(errorLocation, errorCode, null, null, t.toString());
  }

  public SyncError(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, String errorDetail) {
    this(errorLocation, errorCode, null, null, errorDetail);
  }

  public SyncError(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, String tableName,
      String errorDetail) {
    this(errorLocation, errorCode, tableName, null, errorDetail);
  }


  public SyncError(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, String tableName,
      Long syncRecordId, Exception e) {
    this(errorLocation, errorCode, tableName, syncRecordId, e.toString());
  }

  public SyncError(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, String tableName,
      Long syncRecordId, String errorDetail) {
    this.errorLocation = errorLocation;
    this.errorCode = errorCode;
    this.errorCodeValue = errorCode.getCode();
    this.tableName = tableName;
    this.syncRecordId = syncRecordId;
    this.errorDetail = errorDetail;
  }

  public SyncError(Long syncErrorId, APISyncErrorLocation errorLocation, int errorCodeValue, String tableName,
      Long syncRecordId, String errorDetail, DateTime errorTimeStamp) {
    this.syncErrorId = syncErrorId;
    this.errorLocation = errorLocation;
    this.errorCode = null;
    this.errorCodeValue = errorCodeValue;
    this.tableName = tableName;
    this.syncRecordId = syncRecordId;
    this.errorDetail = errorDetail;
    this.errorTimeStamp = errorTimeStamp;
  }

  public APISyncErrorLocation getErrorLocation() {
    return errorLocation;
  }

  public APISyncErrorCode getErrorCode() {
    return errorCode;
  }

  public String getTableName() {
    return tableName;
  }

  public Long getSyncRecordId() {
    return syncRecordId;
  }

  public String getErrorDetail() {
    return errorDetail;
  }

  public int getErrorCodeValue() {
    return errorCodeValue;
  }

  public DateTime getErrorTimeStamp() {
    return errorTimeStamp;
  }

  public Long getSyncErrorId() {
    return syncErrorId;
  }

}
