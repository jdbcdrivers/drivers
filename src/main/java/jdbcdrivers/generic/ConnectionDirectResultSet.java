package jdbcdrivers.generic;

import java.nio.ByteBuffer;
import java.util.Objects;

import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ProtocolErrorException;
import jdbcdrivers.util.IntLargeArray;

/**
 * Result set for retrieving row from a database connection.
 *
 * @implNote buffers some rows locally
 *
 * @param <PREPARED_STATEMENT> database protocol prepared statement type
 * @param <DATA_TYPE> database protocol datatype
 */
final class ConnectionDirectResultSet<PREPARED_STATEMENT, DATA_TYPE> extends BaseByteSequenceResultSet<PREPARED_STATEMENT, DATA_TYPE> implements AutoCloseable {

    private static final boolean DEBUG = Boolean.FALSE;

    // Max number of bytes to buffer locally
    private static final int MAX_BUFFER_SIZE = 10 * 1024 * 1024;

    // For retrieving more results from the connection from the database
    private final ResultRetrieval retrieval;

    // Buffer chunk size when retrieving remaining rows
    private final int remainingResultSetBufferChunkSize;

    // Max number of rows to retrieve at a time
    private final int maxRowsToRetrieve;

    // Length of rows retrieved into buffers
    private final int[] rowLengths;

    // Scratch object initialized by database protocol about retrieved roles
    private final RetrievedRows retrievedRows;

    // Number of remaining rows of the entire result set, only applicable if database returns the total number of rows
    // ahead of row data
    private long remainingRowsOfResult;

    // Row buffer
    private final byte[] resultRowBytes;

    // Corresponding ByteBuffer
    private final ByteBuffer resultRowByteBuffer;

    ConnectionDirectResultSet(PREPARED_STATEMENT preparedStatement, ResultRetrieval retrieval, GenericResultSetMetaData<DATA_TYPE> metaData,
            ResultRowDecoder<DATA_TYPE> resultRowDecoder, int remainingResultSetBufferChunkSize) {
        super(preparedStatement, retrieval, metaData, resultRowDecoder);

        this.retrieval = Objects.requireNonNull(retrieval);

        if (remainingResultSetBufferChunkSize < 1) {

            throw new IllegalArgumentException();
        }

        this.remainingResultSetBufferChunkSize = remainingResultSetBufferChunkSize;

        final long totalNumRows = metaData.getTotalNumRows();

        final int maxBytesPerResultRow = metaData.getMaxBytesPerResultRow();

        final int bufferSize;

        if (totalNumRows == GenericResultSetMetaData.NO_TOTAL_NUM_ROWS) {

            // Total number of rows returned from the database is not known ahead of time
            this.maxRowsToRetrieve = MAX_BUFFER_SIZE / maxBytesPerResultRow;
            this.remainingRowsOfResult = -1;

            bufferSize = MAX_BUFFER_SIZE;
        }
        else if (totalNumRows < 0) {

            throw new IllegalArgumentException();
        }
        else if (totalNumRows == 0) {

            // Database returned no rows, known ahead of time
            this.maxRowsToRetrieve = 0;
            this.remainingRowsOfResult = 0;

            bufferSize = 0;
        }
        else {
            // Total number of rows returned from database is known ahead of time
            this.remainingRowsOfResult = totalNumRows;

            final long totalSize = totalNumRows * maxBytesPerResultRow;

            if (totalSize > MAX_BUFFER_SIZE) {

                this.maxRowsToRetrieve = MAX_BUFFER_SIZE / maxBytesPerResultRow;
            }
            else {
                this.maxRowsToRetrieve = (int)totalNumRows;
            }

            bufferSize = maxRowsToRetrieve * maxBytesPerResultRow;
        }

        this.rowLengths = new int[maxRowsToRetrieve];
        this.retrievedRows = new RetrievedRows(rowLengths);

        this.resultRowBytes = bufferSize != 0 ? new byte[bufferSize] : null;
        this.resultRowByteBuffer = ByteBuffer.wrap(resultRowBytes);
    }

    @Override
    BufferedResultSet<PREPARED_STATEMENT, DATA_TYPE> readRemainingToBuffer(long resultRowBytesOffset, long remainingBufferedBytes, long bufferRowIndex,
            long remainingBufferedRows) throws GenericProtocolException {

        final ResultSetBuffer resultSetBuffer = new ResultSetBuffer(remainingResultSetBufferChunkSize);

        final int bytesOffet = checkCastToInt(resultRowBytesOffset);
        final int remainingBytes = checkCastToInt(remainingBufferedBytes);
        final int rowIndex = checkCastToInt(bufferRowIndex);
        final int remainingRows = checkCastToInt(remainingBufferedRows);

        resultSetBuffer.addData(resultRowBytes, bytesOffet, remainingBytes);

        final IntLargeArray rowLengthsIntLargeArray = new IntLargeArray();

        rowLengthsIntLargeArray.add(rowLengths, rowIndex, remainingRows);

        int numRetrievedRows;

        do {
            numRetrievedRows = retrieveMoreRows();

            resultSetBuffer.addData(resultRowBytes, 0, retrievedRows.getNumBytes());

            rowLengthsIntLargeArray.add(rowLengths, 0, numRetrievedRows);
        }
        while (numRetrievedRows > 0);

        return new BufferedResultSet<>(getPreparedStatement(), retrieval, getMetaData(), getResultRowDecoder(), resultSetBuffer, rowLengthsIntLargeArray);
    }

    @Override
    void getBytesResult(long resultRowBytesOffset, int maxLength, BytesResult dst) {

        if (resultRowBytesOffset > Integer.MAX_VALUE) {

            throw new IllegalStateException();
        }

        dst.init(resultRowBytes, resultRowByteBuffer, (int)resultRowBytesOffset);
    }

    @Override
    int retrieveMoreRows() throws GenericProtocolException {

        if (DEBUG) {

            System.out.println("result set retrieve more rows remainingRowsOfResult=" + remainingRowsOfResult);
        }

        final int maxBytesPerResultRow = getMetaData().getMaxBytesPerResultRow();

        final int numRetrievedRows;

        if (remainingRowsOfResult < 0) {

            // Total number of rows returned from the database is not known ahead of time,
            // retrieve as many as there is room for in the buffer
            retrieval.retrieveResultRows(resultRowBytes, Integer.MAX_VALUE, maxBytesPerResultRow, retrievedRows);

            numRetrievedRows = retrievedRows.getNumRows();
        }
        else if (remainingRowsOfResult == 0) {

            // Database returned no rows, known ahead of time
            numRetrievedRows = 0;
        }
        else {
            // Total number of rows returned from the database is known ahead of time,
            // retrieve remaining, max rows to retrieve or as many as there is room for in the buffer
            final int numRowsToRetrieve = (int)Math.min(remainingRowsOfResult, maxRowsToRetrieve);

            retrieval.retrieveResultRows(resultRowBytes, numRowsToRetrieve, maxBytesPerResultRow, retrievedRows);

            numRetrievedRows = retrievedRows.getNumRows();

            if (numRetrievedRows == 0) {

                throw new ProtocolErrorException();
            }

            this.remainingRowsOfResult -= numRetrievedRows;
        }

        if (DEBUG) {

            System.out.println("exit result set retrieve more rows numRetrievedRows=" + numRetrievedRows + " remainingRowsOfResult=" + remainingRowsOfResult);
        }

        return numRetrievedRows;
    }

    @Override
    int getRowLength(long rowIndex) {

        if (rowIndex > Integer.MAX_VALUE) {

            throw new IllegalArgumentException();
        }

        return rowLengths[(int)rowIndex];
    }

    private static int checkCastToInt(long value) {

        if (value > Integer.MAX_VALUE) {

            throw new IllegalStateException();
        }

        return (int)value;
    }
}
