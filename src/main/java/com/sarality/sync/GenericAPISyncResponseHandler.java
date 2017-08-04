package com.sarality.sync;

import com.sarality.db.Table;
import com.sarality.db.common.FieldValueGetter;
import com.sarality.db.common.FieldValueSetter;
import com.sarality.sync.db.SyncStatusUpdater;

import java.io.IOException;
import java.util.List;

/**
 * Response Handler for API Create/Update requests.
 *
 * @author Satya@ (Satya Puniani)
 */

public class GenericAPISyncResponseHandler<T, S, R, E extends Enum<E>> implements APISyncResponseHandler<S, R> {

  private final Table<T> table;
  SyncStatusUpdater<T, E> syncUpdater;
  SyncErrorLogger logger;
  FieldValueGetter<T, Long> idGetter;
  FieldValueGetter<R, Long> responseGlobalIdGetter;
  FieldValueGetter<R, Long> responseVersionGetter;
  FieldValueGetter<S, List<T>> sourceDataGetter;
  FieldValueSetter<T, String> globalIdSetter;
  FieldValueSetter<T, Long> globalVersionSetter;

  public GenericAPISyncResponseHandler(Table<T> table,
      SyncStatusUpdater<T, E> syncUpdater,
      SyncErrorLogger logger,
      FieldValueGetter<T, Long> idGetter,
      FieldValueGetter<R, Long> responseGlobalIdGetter,
      FieldValueGetter<R, Long> responseVersionGetter,
      FieldValueGetter<S, List<T>> sourceDataGetter,
      FieldValueSetter<T, String> globalIdSetter,
      FieldValueSetter<T, Long> globalVersionSetter) {
    this.table = table;
    this.syncUpdater = syncUpdater;
    this.logger = logger;
    this.idGetter = idGetter;
    this.responseGlobalIdGetter = responseGlobalIdGetter;
    this.responseVersionGetter = responseVersionGetter;
    this.sourceDataGetter = sourceDataGetter;
    this.globalIdSetter = globalIdSetter;
    this.globalVersionSetter = globalVersionSetter;
  }


  @Override
  public APISyncResponseType process(S sourceData, R responseData) {

    Long globalId = responseGlobalIdGetter.getValue(responseData);
    Long globalVersion = responseVersionGetter.getValue(responseData);
    List<T> dataList = sourceDataGetter.getValue(sourceData);
    String globalDataId = globalId.toString();

    for (T data : dataList) {
      globalIdSetter.setValue(data, globalDataId);
      globalVersionSetter.setValue(data, globalVersion);

      try {
        table.open();
        syncUpdater.updateSyncStatus(data);
      } finally {
        table.close();
      }
    }

    return APISyncResponseType.SUCCESS;
  }

  @Override
  public APISyncResponseType processError(IOException e, S sourceData, R requestData) {

    if (logger != null) {
      List<T> dataList = sourceDataGetter.getValue(sourceData);
      if (dataList.size() > 0) {
        for (T data : dataList) {
          Long sourceId = idGetter.getValue(data);
          logger.logError(e, sourceId, table.getName());
        }
      } else {
        logger.logError(e);
      }
    }
    return APISyncResponseType.FAILED_ABORT;
  }
}
