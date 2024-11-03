package jdbcdrivers.databaseprotocol.vendor.informix;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import jdbcdrivers.generic.CachedValues;
import jdbcdrivers.generic.ResultColumn;
import jdbcdrivers.generic.ResultColumns;
import jdbcdrivers.generic.ResultRowDecoder;
import jdbcdrivers.util.PrintDebug;

/**
 * Decodes Informix result rows.
 */
final class InformixResultRowDecoder implements ResultRowDecoder<InformixDataType>, PrintDebug {

    private static final boolean DEBUG = Boolean.FALSE;

    private final CharsetDecoder charsetDecoder;

    private final char[] decodeStringCharacters;
    private final CharBuffer decodeStringCharBuffer;

    InformixResultRowDecoder(Charset charset) {

        this.charsetDecoder = charset.newDecoder();

        this.decodeStringCharacters = new char[1 << 16];
        this.decodeStringCharBuffer = CharBuffer.wrap(decodeStringCharacters);
    }

    @Override
    public boolean isConstantLength(InformixDataType dataType) {

        return dataType.isConstantLength();
    }

    @Override
    public int findRowColumnOffset(byte[] buffer, ResultColumns<InformixDataType> resultColumns, int startOfRowOffset, int columnIndex) {

        int offset = startOfRowOffset;

        if (columnIndex >= resultColumns.getNumColumns()) {

            throw new IllegalArgumentException();
        }

        for (int i = 0; i < columnIndex; ++ i) {

            final ResultColumn<InformixDataType> resultColumn = resultColumns.getResultColumn(i);

            final InformixDataType informixDataType = resultColumn.getDataType();

            if (informixDataType.isConstantLength()) {

                offset += resultColumn.getNumRowBytesForColumn();
            }
            else {
                switch (informixDataType) {

                case VARCHAR:

                    offset += buffer[offset] + 1;
                    break;

                default:
                    throw new UnsupportedOperationException();
                }
            }
        }

        return offset;
    }

    @Override
    public boolean decodeBoolean(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        throw new UnsupportedOperationException();
    }

    @Override
    public byte decodeByte(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        return buffer[rowColumnOffset];
    }

    @Override
    public short decodeShort(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        return (short)((buffer[rowColumnOffset] << 8) | buffer[rowColumnOffset + 1]);
    }

    @Override
    public int decodeInt(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        return    (buffer[rowColumnOffset]     << 24)
                | (buffer[rowColumnOffset + 1] << 16)
                | (buffer[rowColumnOffset + 2] << 8)
                |  buffer[rowColumnOffset + 3];
    }

    @Override
    public long decodeLong(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        return    (buffer[rowColumnOffset]     << 56)
                | (buffer[rowColumnOffset + 1] << 48)
                | (buffer[rowColumnOffset + 2] << 40)
                | (buffer[rowColumnOffset + 1] << 32)
                | (buffer[rowColumnOffset + 2] << 24)
                | (buffer[rowColumnOffset + 1] << 16)
                | (buffer[rowColumnOffset + 2] << 8)
                |  buffer[rowColumnOffset + 3];
    }

    @Override
    public float decodeFloat(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        return Float.intBitsToFloat(decodeInt(buffer, dataType, rowColumnOffset, length));
    }

    @Override
    public double decodeDouble(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        return Double.longBitsToDouble(decodeLong(buffer, dataType, rowColumnOffset, length));
    }

    @Override
    public BigDecimal decodeDecimal(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length, int scale, CachedValues cachedValues) {

        throw new UnsupportedOperationException();
    }

    @Override
    public String decodeString(byte[] buffer, ByteBuffer byteBuffer, InformixDataType dataType, int rowColumnOffset, int maxBytes, CachedValues cachedValues) {

        final int length;
        final int stringOffset;

        switch (dataType) {

        case CHAR:

            length = maxBytes;
            stringOffset = rowColumnOffset;
            break;

        case VARCHAR:

            length = buffer[rowColumnOffset];

            stringOffset = rowColumnOffset + dataType.getNumUpdateLengthBytes();
            break;

        default:
            throw new UnsupportedOperationException();
        }

        if (DEBUG) {

            formatln("buffer 0x%02x 0x%02x", buffer[rowColumnOffset], buffer[rowColumnOffset + 1]);

            println("decode string by offset rowColumnOffset=" + rowColumnOffset + " length=" + length + " string='" + new String(buffer, stringOffset, length) + '\'');
        }

        byteBuffer.position(0);
        byteBuffer.limit(stringOffset + length);
        byteBuffer.position(stringOffset);

        decodeStringCharBuffer.position(0);
        decodeStringCharBuffer.limit(decodeStringCharBuffer.capacity());

        final CoderResult coderResult = charsetDecoder.decode(byteBuffer, decodeStringCharBuffer, true);

        if (coderResult.isError()) {

            throw new IllegalStateException();
        }

        final String result = cachedValues.getString(decodeStringCharacters, decodeStringCharBuffer.position());

        if (DEBUG) {

            println("decoded string '" + result + '\'');
        }

        return result;
    }

    @Override
    public byte[] decodeBytes(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        throw new UnsupportedOperationException();
    }

    @Override
    public int decodeDate(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        throw new UnsupportedOperationException();
    }

    @Override
    public long decodeTime(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        throw new UnsupportedOperationException();
    }

    @Override
    public long decodeTimestamp(byte[] buffer, InformixDataType dataType, int rowColumnOffset, int length) {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object decodeObject(byte[] buffer, ByteBuffer byteBuffer, InformixDataType dataType, int rowColumnOffset, int length, CachedValues cachedValues) {

        final Object result;

        switch (dataType) {

        case CHAR:

            result = decodeString(buffer, byteBuffer, dataType, rowColumnOffset, length, cachedValues);
            break;

        case INTEGER:
        case SERIAL:
            result = cachedValues.getInt(decodeInt(buffer, dataType, rowColumnOffset, length));
            break;

        case VARCHAR:

            result = decodeString(buffer, byteBuffer, dataType, rowColumnOffset, length, cachedValues);
            break;

        default:
            throw new UnsupportedOperationException();
        }

        return result;
    }
}
