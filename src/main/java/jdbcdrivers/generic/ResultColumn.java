package jdbcdrivers.generic;

import java.util.Objects;

/**
 * Base class for result column information.
 *
 * @param <DATA_TYPE> database protocol datatype
 */
public abstract class ResultColumn<DATA_TYPE> {

    private final DATA_TYPE dataType;
    private final int maxLength;
    private final int numRowBytesForColumn;

    ResultColumn(DATA_TYPE dataType, int maxLength, int numRowBytesForColumn) {

        Objects.requireNonNull(dataType);

        if (numRowBytesForColumn < 1) {

            throw new IllegalArgumentException();
        }

        if (maxLength < 1) {

            throw new IllegalArgumentException();
        }

        this.dataType = dataType;
        this.maxLength = maxLength;
        this.numRowBytesForColumn = numRowBytesForColumn;
    }

    public final DATA_TYPE getDataType() {
        return dataType;
    }

    @Deprecated
    public final int getMaxLength() {
        return maxLength;
    }

    public final int getNumRowBytesForColumn() {
        return numRowBytesForColumn;
    }
}
