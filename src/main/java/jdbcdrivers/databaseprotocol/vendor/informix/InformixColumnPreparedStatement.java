package jdbcdrivers.databaseprotocol.vendor.informix;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jdbcdrivers.generic.GenericResultSetMetaData;
import jdbcdrivers.generic.GenericResultSetMetaData.ResultSetColumn;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.util.DriverUtil;

/**
 * Informix prepared statement for operations on columns.
 */
final class InformixColumnPreparedStatement extends InformixPreparedStatement {

    static final class PreparedColumn {

        private final String columnName;
        private final InformixDataType dataType;
        private final int indexIntoRow;
        private final int byteArrayRowOffset;
        private final int numRowBytesForColumn;

        PreparedColumn(String columnName, InformixDataType dataType, int indexIntoRow, int byteArayRowOffset, int numRowBytesForColumn) {

            Objects.requireNonNull(columnName);
            Objects.requireNonNull(dataType);

            if (indexIntoRow < 0) {

                throw new IllegalArgumentException();
            }

            if (byteArayRowOffset < 0) {

                throw new IllegalArgumentException();
            }

            if (numRowBytesForColumn < 0) {

                throw new IllegalArgumentException();
            }

            this.columnName = columnName;
            this.dataType = dataType;
            this.indexIntoRow = indexIntoRow;
            this.byteArrayRowOffset = byteArayRowOffset;
            this.numRowBytesForColumn = numRowBytesForColumn;
        }

        InformixDataType getDataType() {
            return dataType;
        }

        int getNumRowBytes() {
            return numRowBytesForColumn;
        }

        ResultSetColumn<InformixDataType> toResultSetColumn() {

            @Deprecated
            final boolean nullable = false;

            return new ResultSetColumn<>(dataType, numRowBytesForColumn, numRowBytesForColumn, columnName, columnName, dataType.getJDBCType(), nullable, indexIntoRow,
                    byteArrayRowOffset);
        }
    }

    private final int maxRowSize;
    private final GenericStatementExecutionOptions statementExecutionOptions;
    private final List<PreparedColumn> columns;

    InformixColumnPreparedStatement(int identifier, GenericStatementExecutionOptions statementExecutionOptions, int maxRowSize, List<PreparedColumn> columns) {
        super(identifier);

        Objects.requireNonNull(columns);

        this.statementExecutionOptions = Objects.requireNonNull(statementExecutionOptions);
        this.maxRowSize = maxRowSize;
        this.columns = DriverUtil.unmodifiableCopyOf(columns);
    }

    GenericStatementExecutionOptions getStatementExecutionOptions() {
        return statementExecutionOptions;
    }

    int getMaxRowSize() {
        return maxRowSize;
    }

    int getNumColumns() {

        return columns.size();
    }

    List<PreparedColumn> getColumns() {
        return columns;
    }

    GenericResultSetMetaData<InformixDataType> toResultSetMetaData() {

        final List<ResultSetColumn<InformixDataType>> resultSetColumns = columns.stream()
                .map(c -> c.toResultSetColumn())
                .collect(Collectors.toUnmodifiableList());

        return new GenericResultSetMetaData<>(maxRowSize, maxRowSize, resultSetColumns);
    }
}
