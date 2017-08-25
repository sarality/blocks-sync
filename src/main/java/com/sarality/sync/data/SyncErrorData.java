package com.sarality.sync.data;

/**
 * Data object for tracking errors during sync to server
 *
 * @author satya (satya puniani)
 */

public class SyncErrorData {
  private APISyncErrorLocation errorLocation;
  private APISyncErrorCode errorCode;
  private String tableName;
  private Long syncRecordId;
  private String errorDetail;

  public SyncErrorData(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, Exception e) {
    this(errorLocation, errorCode, null, null, e.toString());
  }

  public SyncErrorData(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, String errorDetail) {
    this(errorLocation, errorCode, null, null, errorDetail);
  }

  public SyncErrorData(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, String tableName,
      Long syncRecordId, Exception e) {
    this(errorLocation, errorCode, tableName, syncRecordId, e.toString());
  }

  public SyncErrorData(APISyncErrorLocation errorLocation, APISyncErrorCode errorCode, String tableName,
      Long syncRecordId, String errorDetail) {
    this.errorLocation = errorLocation;
    this.errorCode = errorCode;
    this.tableName = tableName;
    this.syncRecordId = syncRecordId;
    this.errorDetail = errorDetail;
  }

  public APISyncErrorLocation getErrorLocation() {
    return errorLocation;
  }

  public void setErrorLocation(APISyncErrorLocation errorLocation) {
    this.errorLocation = errorLocation;
  }

  public APISyncErrorCode getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(APISyncErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public Long getSyncRecordId() {
    return syncRecordId;
  }

  public void setSyncRecordId(Long syncRecordId) {
    this.syncRecordId = syncRecordId;
  }

  public String getErrorDetail() {
    return errorDetail;
  }

  public void setErrorDetail(String errorDetail) {
    this.errorDetail = errorDetail;
  }
}
