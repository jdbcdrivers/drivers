package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.util.IntLargeArray;

/**
 * Buffers an entire result set, or part of one.
 *
 * @param <PREPARED_STATEMENT> database protocol prepared statement type
 * @param <DATA_TYPE> database protocol datatype
 */
final class BufferedResultSet<PREPARED_STATEMENT, DATA_TYPE> extends BaseByteSequenceResultSet<PREPARED_STATEMENT, DATA_TYPE> {

    private final ResultSetBuffer resultSetBuffer;
    private final IntLargeArray rowLengths;

    BufferedResultSet(PREPARED_STATEMENT preparedStatement, ResultSetClosing resultSetClosing, GenericResultSetMetaData<DATA_TYPE> metaData,
            ResultRowDecoder<DATA_TYPE> resultRowDecoder, ResultSetBuffer resultSetBuffer, IntLargeArray rowLengths) {
        super(preparedStatement, resultSetClosing, metaData, resultRowDecoder);

        this.resultSetBuffer = Objects.requireNonNull(resultSetBuffer);
        this.rowLengths = rowLengths;
    }

    @Override
    BufferedResultSet<PREPARED_STATEMENT, DATA_TYPE> readRemainingToBuffer(long resultRowBytesOffset, long remainingBufferedBytes, long bufferRowIndex,
            long remainingBufferedRows) throws GenericProtocolException {

        throw new UnsupportedOperationException();
    }

    @Override
    void getBytesResult(long resultRowBytesOffset, int maxLength, BytesResult dst) {

        resultSetBuffer.decodeMaxLength(resultRowBytesOffset, maxLength, dst);
    }

    @Override
    int retrieveMoreRows() throws GenericProtocolException {

        throw new UnsupportedOperationException();
    }

    @Override
    int getRowLength(long rowIndex) {

        return rowLengths.getValue(rowIndex);
    }
}
