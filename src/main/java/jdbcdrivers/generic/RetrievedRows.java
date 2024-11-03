package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.databaseprotocol.api.IRetrievedRows;

/**
 * Reusable result objects for information about rows retrieved from the database protocol.
 */
final class RetrievedRows implements IRetrievedRows{

    private final int[] rowLengths;

    private int numRows;
    private int numBytes;

    RetrievedRows(int[] rowLengths) {

        Objects.requireNonNull(rowLengths);

        if (rowLengths.length == 0) {

            throw new IllegalArgumentException();
        }

        this.rowLengths = rowLengths;
    }

    @Override
    public void addRow(int numRowBytes) {

        if (numRowBytes < 1) {

            throw new IllegalArgumentException();
        }

        rowLengths[numRows ++] = numRowBytes;

        numBytes += numRowBytes;
    }

    int getNumRows() {
        return numRows;
    }

    int getNumBytes() {
        return numBytes;
    }

    void reset() {

        this.numRows = 0;
        this.numBytes = 0;
    }
}
