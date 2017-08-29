package com.sarality.sync;

import com.sarality.db.Table;
import com.sarality.db.common.FieldValueGetter;
import com.sarality.db.common.FieldValueSetter;
import com.sarality.sync.data.APISyncErrorLocation;
import com.sarality.sync.data.BaseAPISyncErrorCode;
import com.sarality.sync.data.SyncErrorData;
import com.sarality.sync.db.SyncStatusUpdater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Response Handler for API Create/Update requests.
 *
 * @author Satya@ (Satya Puniani)
 */

public class GenericAPISyncResponseHandler<T, S, R, E extends Enum<E>> implements APISyncResponseHandler<S, R> {

  private final Table<T> table;
  private final SyncStatusUpdater<T, E> syncUpdater;
  private final FieldValueGetter<T, Long> idGetter;
  private final FieldValueGetter<R, Long> responseGlobalIdGetter;
  private final FieldValueGetter<R, Long> responseVersionGetter;
  private final FieldValueGetter<S, List<T>> sourceDataGetter;
  private final FieldValueSetter<T, String> globalIdSetter;
  private final FieldValueSetter<T, Long> globalVersionSetter;
  private List<SyncErrorData> syncErrorList = new ArrayList<>();


  public GenericAPISyncResponseHandler(Table<T> table,
      SyncStatusUpdater<T, E> syncUpdater,
      FieldValueGetter<T, Long> idGetter,
      FieldValueGetter<R, Long> responseGlobalIdGetter,
      FieldValueGetter<R, Long> responseVersionGetter,
      FieldValueGetter<S, List<T>> sourceDataGetter,
      FieldValueSetter<T, String> globalIdSetter,
      FieldValueSetter<T, Long> globalVersionSetter) {
    this.table = table;
    this.syncUpdater = syncUpdater;
    this.idGetter = idGetter;
    this.responseGlobalIdGetter = responseGlobalIdGetter;
    this.responseVersionGetter = responseVersionGetter;
    this.sourceDataGetter = sourceDataGetter;
    this.globalIdSetter = globalIdSetter;
    this.globalVersionSetter = globalVersionSetter;
  }

  private void addError(SyncErrorData errorData) {
    syncErrorList.add(errorData);
  }


  @Override
  public APISyncResponseType process(S sourceData, R responseData) {

    Long globalId = responseGlobalIdGetter.getValue(responseData);
    Long globalVersion = responseVersionGetter.getValue(responseData);
    List<T> dataList = sourceDataGetter.getValue(sourceData);
    String globalDataId = globalId.toString();
    // TODO (@Satya) if globalId is null OR existing global Id is different, log an exception

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
  public List<SyncErrorData> getSyncErrors() {
    return syncErrorList;
  }

  @Override
  public APISyncResponseType processError(IOException e, S sourceData, R requestData) {
    this.syncErrorList = new ArrayList<>();

    List<T> dataList = sourceDataGetter.getValue(sourceData);
    if (dataList.size() > 0) {
      for (T data : dataList) {
        Long sourceId = idGetter.getValue(data);
        addError(new SyncErrorData(APISyncErrorLocation.RESPONSE_HANDLER,
            BaseAPISyncErrorCode.IO_EXCEPTION,
            table.getName(),
            sourceId,
            e.toString()));
      }
    } else {
      addError(new SyncErrorData(APISyncErrorLocation.RESPONSE_HANDLER,
          BaseAPISyncErrorCode.IO_EXCEPTION,
          table.getName(),
          null,
          e.toString()));
    }

    return APISyncResponseType.FAILED_ABORT;
  }
}
