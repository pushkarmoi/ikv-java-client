package io.inlined.clients;

import com.inlineio.schemas.Common;
import com.inlineio.schemas.Streaming;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class DirectJNIBenchmarkingClient implements InlineKVReader, InlineKVWriter {
  private final DefaultInlineKVReader _dbAccessor;

  private volatile boolean _readerOpen = false;
  private volatile boolean _writerOpen = false;

  public DirectJNIBenchmarkingClient(ClientOptions options, Common.IKVStoreConfig mergedConfig) {
    _dbAccessor = new DefaultInlineKVReader(options, mergedConfig);
    _readerOpen = false;
    _writerOpen = false;
  }

  @Override
  public void startupReader() throws RuntimeException {
    _readerOpen = true;
    _dbAccessor.startupReader();
  }

  @Override
  public void shutdownReader() {
    _readerOpen = false;

    if (_writerOpen) {
      // writer also needs access to jni object
      return;
    }

    _dbAccessor.shutdownReader();
  }

  @Override
  public void startupWriter() {
    _writerOpen = true;
    _dbAccessor.startupReader();
  }

  @Override
  public void shutdownWriter() {
    _writerOpen = false;

    if (_readerOpen) {
      // reader also needs access to jni object
      return;
    }

    _dbAccessor.shutdownReader();
  }

  @Override
  public void upsertFieldValues(IKVDocument document) {
    Map<String, Common.FieldValue> fieldValues = document.asNameToFieldValueMap();
    Common.IKVDocumentOnWire documentOnWire =
        Common.IKVDocumentOnWire.newBuilder().putAllDocument(fieldValues).build();

    Streaming.IKVDataEvent event =
        Streaming.IKVDataEvent.newBuilder()
            .setUpsertDocumentFieldsEvent(
                Streaming.UpsertDocumentFieldsEvent.newBuilder()
                    .setDocument(documentOnWire)
                    .build())
            .build();

    // jni call
    _dbAccessor.ikvClientJNI().directWriteIKVDataEvent(_dbAccessor.handle(), event.toByteArray());
  }

  @Override
  public void deleteFieldValues(IKVDocument documentId, Collection<String> fieldsToDelete) {
    Map<String, Common.FieldValue> fieldValues = documentId.asNameToFieldValueMap();
    Common.IKVDocumentOnWire documentOnWire =
        Common.IKVDocumentOnWire.newBuilder().putAllDocument(fieldValues).build();

    Streaming.IKVDataEvent event =
        Streaming.IKVDataEvent.newBuilder()
            .setDeleteDocumentFieldsEvent(
                Streaming.DeleteDocumentFieldsEvent.newBuilder()
                    .setDocumentId(documentOnWire)
                    .addAllFieldsToDelete(fieldsToDelete)
                    .build())
            .build();

    // jni call
    _dbAccessor.ikvClientJNI().directWriteIKVDataEvent(_dbAccessor.handle(), event.toByteArray());
  }

  @Override
  public void deleteDocument(IKVDocument documentId) {
    Map<String, Common.FieldValue> fieldValues = documentId.asNameToFieldValueMap();
    Common.IKVDocumentOnWire documentOnWire =
        Common.IKVDocumentOnWire.newBuilder().putAllDocument(fieldValues).build();

    Streaming.IKVDataEvent event =
        Streaming.IKVDataEvent.newBuilder()
            .setDeleteDocumentEvent(
                Streaming.DeleteDocumentEvent.newBuilder().setDocumentId(documentOnWire).build())
            .build();

    // jni call
    _dbAccessor.ikvClientJNI().directWriteIKVDataEvent(_dbAccessor.handle(), event.toByteArray());
  }

  @Override
  public void dropFieldsByName(List<String> fieldNames) {
    throw new UnsupportedOperationException("not supported yet");
  }

  @Override
  public void dropFieldsByNamePrefix(List<String> fieldNamePrefixes) {
    throw new UnsupportedOperationException("not supported yet");
  }

  @Override
  public void dropAllDocuments() {
    throw new UnsupportedOperationException("not supported yet");
  }

  public void flushWrites() {
    _dbAccessor.ikvClientJNI().flushWrites(_dbAccessor.handle());
  }

  @Nullable
  @Override
  public byte[] getBytesValue(Object key, String fieldName) {
    return _dbAccessor.getBytesValue(key, fieldName);
  }

  @Override
  public Iterator<byte[]> multiGetBytesValues(List<Object> keys, List<String> fieldNames) {
    return _dbAccessor.multiGetBytesValues(keys, fieldNames);
  }

  @Nullable
  @Override
  public String getStringValue(Object key, String fieldName) {
    return _dbAccessor.getStringValue(key, fieldName);
  }

  @Nullable
  @Override
  public Integer getIntValue(Object key, String fieldName) {
    return _dbAccessor.getIntValue(key, fieldName);
  }

  @Nullable
  @Override
  public Long getLongValue(Object key, String fieldName) {
    return _dbAccessor.getLongValue(key, fieldName);
  }

  @Nullable
  @Override
  public Float getFloatValue(Object key, String fieldName) {
    return _dbAccessor.getFloatValue(key, fieldName);
  }

  @Nullable
  @Override
  public Double getDoubleValue(Object key, String fieldName) {
    return _dbAccessor.getDoubleValue(key, fieldName);
  }
}
