package jdbcdrivers.generic;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

/**
 * Interface implemented database protocols for decoding rows received from the database.
 *
 * @param <DATA_TYPE> database specific data type
 */
public interface ResultRowDecoder<DATA_TYPE> {

    boolean isConstantLength(DATA_TYPE dataType);

    int findRowColumnOffset(byte[] buffer, ResultColumns<DATA_TYPE> resultColumns, int startOfRowOffset, int columnIndex);

    boolean decodeBoolean   (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);

    byte    decodeByte      (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);
    short   decodeShort     (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);
    int     decodeInt       (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);
    long    decodeLong      (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);

    float   decodeFloat     (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);
    double  decodeDouble    (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);

    BigDecimal decodeDecimal(byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length, int scale, CachedValues cachedValues);

    String  decodeString    (byte[] buffer, ByteBuffer byteBuffer, DATA_TYPE dataType, int rowColumnOffset, int length, CachedValues cachedValues);

    byte[]  decodeBytes     (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);

    int     decodeDate      (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);
    long    decodeTime      (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);
    long    decodeTimestamp (byte[] buffer, DATA_TYPE dataType, int rowColumnOffset, int length);

    Object  decodeObject    (byte[] buffer, ByteBuffer byteBuffer, DATA_TYPE dataType, int rowColumnOffset, int length, CachedValues cachedValues);
}
