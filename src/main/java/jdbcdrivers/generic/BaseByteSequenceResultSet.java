package jdbcdrivers.generic;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.EnumSet;
import java.util.Objects;

import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;
import jdbcdrivers.generic.exceptions.WrongColumnTypeException;

/**
 * Base class for result sets out of a sequence of bytes, either retrieved over a connection or buffered.
 *
 * @param <PREPARED_STATEMENT> database protocol prepared statement type
 * @param <DATA_TYPE> database protocol datatype
 */
abstract class BaseByteSequenceResultSet<PREPARED_STATEMENT, DATA_TYPE> extends ColumnGenericResultSet<PREPARED_STATEMENT, DATA_TYPE> {

    private static final boolean DEBUG = Boolean.FALSE;

    private static final CachedValues cachedValues = CachedValues.getInstance();

    /**
     * Get a sub-sequence of bytes from a subclass.
     *
     * @param resultRowBytesOffset start offset of bytes to retrieve
     * @param maxLength the max number of bytes to retrieve
     * @param dst must be initialized with the result
     */
    abstract void getBytesResult(long resultRowBytesOffset, int maxLength, BytesResult dst);

    /**
     * Retrieve more rows from any network connection.
     *
     * @return number of rows retrieved
     *
     * @throws GenericProtocolException if any database protocol communication error occurred
     */
    abstract int retrieveMoreRows() throws GenericProtocolException;

    /**
     * Get the number of bytes in a row.
     *
     * @param rowIndex the index of the row to get the number of bytes for.
     *
     * @return number of bytes in the row
     */
    abstract int getRowLength(long rowIndex);

    /**
     * Read all remaining result set data to a {@link BufferedResultSet}.
     *
     * @param resultRowBytesOffset start offset of bytes to buffer
     * @param remainingBufferedBytes number of remaining already buffered bytes
     * @param bufferRowIndex start index of rows to buffer
     * @param remainingBufferedRows number of remaining already buffered rows
     *
     * @return a {@link BufferedResultSet} with all remaining row data
     *
     * @throws GenericProtocolException if any database protocol communication error occurred
     */
    abstract BufferedResultSet<PREPARED_STATEMENT, DATA_TYPE> readRemainingToBuffer(long resultRowBytesOffset, long remainingBufferedBytes, long bufferRowIndex,
            long remainingBufferedRows) throws GenericProtocolException;

    private final ResultRowDecoder<DATA_TYPE> resultRowDecoder;

    private final BytesResult bytesResult;

    private long numBufferedRows;
    private long bufferRowIndex;

    private long bufferedRowsByteOffset;
    private long numBufferedRowBytes;

    BaseByteSequenceResultSet(PREPARED_STATEMENT preparedStatement, ResultSetClosing resultSetClosing, GenericResultSetMetaData<DATA_TYPE> metaData,
            ResultRowDecoder<DATA_TYPE> resultRowDecoder) {
        super(preparedStatement, resultSetClosing, metaData, resultRowDecoder);

        this.resultRowDecoder = Objects.requireNonNull(resultRowDecoder);

        this.bytesResult = new BytesResult();

        this.numBufferedRows = 0L;
        this.bufferRowIndex = 0L;

        this.bufferedRowsByteOffset = 0L;
        this.numBufferedRowBytes = 0L;
    }

    @Override
    public final boolean next() throws GenericProtocolException {

        if (DEBUG) {

            System.out.println("result set next numBufferedRows=" + numBufferedRows + " bufferRowIndex=" + bufferRowIndex + " bufferedRowsByteOffset=" + bufferedRowsByteOffset
                    + " numBufferedRowBytes=" + numBufferedRowBytes);
        }

        if (numBufferedRows < 0L) {

            throw new IllegalStateException();
        }
        else if (numBufferedRows == 0L || bufferRowIndex == numBufferedRows - 1L) {

            // No more data buffered, retrieve more
            this.numBufferedRows = retrieveMoreRows();
            this.bufferRowIndex = 0L;
            this.bufferedRowsByteOffset = 0L;
            this.numBufferedRowBytes = 0L;
        }
        else {
            // More data available from buffers
            ++ this.bufferRowIndex;

            getBytesResult(bufferedRowsByteOffset, bytesResult);

            this.bufferedRowsByteOffset += getRowLength(bufferRowIndex);
        }

        final boolean hasNext = numBufferedRows > 0L;

        if (DEBUG) {

            System.out.println("exit result set next hasNext=" + hasNext + " numBufferedRows=" + numBufferedRows + " bufferRowIndex=" + bufferRowIndex
                    + " resultRowBytesOffset=" + bufferedRowsByteOffset + " numBufferedRowBytes=" + numBufferedRowBytes);
        }

        return hasNext;
    }

    private static final EnumSet<JDBCType> stringJDBCTypes = EnumSet.of(JDBCType.CHAR, JDBCType.VARCHAR, JDBCType.LONGVARCHAR, JDBCType.NVARCHAR, JDBCType.LONGNVARCHAR);

    @Override
    public final String getString(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, stringJDBCTypes);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeString(bytesResult.getBytes(), bytesResult.getByteBuffer(), column.getDataType(), rowColumnBytesOffset, column.getLength(), cachedValues);
    }

    @Override
    public final boolean getBoolean(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.BOOLEAN);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeBoolean(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final byte getByte(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.TINYINT);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeByte(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final short getShort(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.SMALLINT);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeShort(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final int getInt(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.INTEGER);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeInt(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final long getLong(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.BIGINT);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeLong(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final float getFloat(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.FLOAT);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeFloat(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final double getDouble(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.DOUBLE);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeDouble(bytesResult.getBytes(),column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final BigDecimal getBigDecimal(int index, int scale) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.DECIMAL);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeDecimal(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength(), scale, cachedValues);
    }

    @Override
    public final byte[] getBytes(int index) throws ResultSetClosedException, WrongColumnTypeException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getDate(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.DATE);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeDate(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final long getTime(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.TIME);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeTime(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final long getTimestamp(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index, JDBCType.TIMESTAMP);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeTimestamp(bytesResult.getBytes(), column.getDataType(), rowColumnBytesOffset, column.getLength());
    }

    @Override
    public final Object getObject(int index) throws ResultSetClosedException, WrongColumnTypeException {

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index);

        getBytesResult(bufferedRowsByteOffset, bytesResult);

        final byte[] bytes = bytesResult.getBytes();
        final int rowColumnBytesOffset = getResultRowColumnBytesOffset(bytes, bytesResult.getBytesOffset(), column, index);

        return resultRowDecoder.decodeObject(bytesResult.getBytes(), bytesResult.getByteBuffer(), column.getDataType(), rowColumnBytesOffset, column.getLength(), cachedValues);
    }

    final BufferedResultSet<PREPARED_STATEMENT, DATA_TYPE> readRemainingToBuffer() throws GenericProtocolException {

        return readRemainingToBuffer(bufferedRowsByteOffset, numBufferedRowBytes - bufferedRowsByteOffset, bufferRowIndex, numBufferedRows - bufferRowIndex);
    }

    final void getBytesResult(long resultRowBytesOffset, BytesResult dst) {

        getBytesResult(resultRowBytesOffset, getMetaData().getMaxBytesPerResultRow(), dst);
    }

    final ResultRowDecoder<DATA_TYPE> getResultRowDecoder() {
        return resultRowDecoder;
    }

    private int getResultRowColumnBytesOffset(byte[] resultRowBytes, int bytesOffset, Column<DATA_TYPE> column, int index) {

        final int result = column.isRetrieveByOffset()
                ? bytesOffset + column.getByteArrayRowOffset()
                : getResultRowDecoder().findRowColumnOffset(resultRowBytes, getMetaData(), bytesOffset, index);

        if (DEBUG) {

            System.out.println("result row column bytes offet result=" + result + " column=" + column);
        }

        return result;
    }
}
