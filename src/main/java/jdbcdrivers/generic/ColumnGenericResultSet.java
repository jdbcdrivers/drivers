package jdbcdrivers.generic;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import jdbcdrivers.generic.GenericResultSetMetaData.ResultSetColumn;
import jdbcdrivers.generic.api.IGenericResultSetMetaData;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;
import jdbcdrivers.generic.exceptions.WrongColumnTypeException;

/**
 * Base class for result sets that deal with columns.
 *
 * @param <PREPARED_STATEMENT> database protocol prepared statement type
 * @param <DATA_TYPE> database protocol datatype
 */
abstract class ColumnGenericResultSet<PREPARED_STATEMENT, DATA_TYPE> extends RowDataResultSet {

    /**
     * Result set column meta data.
     *
     * @param <DATA_TYPE> database protocol datatype
     */
    static final class Column<DATA_TYPE> {

        private final JDBCType jdbcType;
        private final DATA_TYPE dataType;
        private final int offset;
        private final int byteArrayRowOffset;
        private final int length;
        private final boolean retrieveByOffset;

        private Column(ResultSetColumn<DATA_TYPE> resultSetColumn, boolean retrieveByOffset) {

            Objects.requireNonNull(resultSetColumn);

            this.jdbcType = resultSetColumn.getJDBCType();
            this.dataType = resultSetColumn.getDataType();
            this.offset = resultSetColumn.getOffset();
            this.length = resultSetColumn.getMaxLength();
            this.byteArrayRowOffset = resultSetColumn.getByteArrayRowOffset();
            this.retrieveByOffset = retrieveByOffset;
        }

        JDBCType getJDBCType() {
            return jdbcType;
        }

        DATA_TYPE getDataType() {
            return dataType;
        }

        int getOffset() {
            return offset;
        }

        int getByteArrayRowOffset() {
            return byteArrayRowOffset;
        }

        int getLength() {
            return length;
        }

        boolean isRetrieveByOffset() {
            return retrieveByOffset;
        }

        @Override
        public String toString() {

            return getClass().getSimpleName() + " [jdbcType=" + jdbcType + ", dataType=" + dataType + ", offset=" + offset + ", length=" + length
                    + ", byteArrayOffset=" + byteArrayRowOffset + ", retrieveByOffset=" + retrieveByOffset + "]";
        }
    }

    private final PREPARED_STATEMENT preparedStatement;
    private final GenericResultSetMetaData<DATA_TYPE> metaData;

    private final List<Column<DATA_TYPE>> columns;

    ColumnGenericResultSet(PREPARED_STATEMENT preparedStatement, ResultSetClosing resultSetClosing, GenericResultSetMetaData<DATA_TYPE> metaData,
            ResultRowDecoder<DATA_TYPE> resultRowDecoder) {
        super(resultSetClosing);

        this.preparedStatement = Objects.requireNonNull(preparedStatement);
        this.metaData = Objects.requireNonNull(metaData);

        final int numColumns = metaData.getNumColumns();

        this.columns = new ArrayList<>(numColumns);

        boolean retrieveByOffset = true;

        for (int i = 0; i < numColumns; ++ i) {

            final ResultSetColumn<DATA_TYPE> resultSetColumn = metaData.getColumn(i);

            final DATA_TYPE dataType = resultSetColumn.getDataType();

            if (!resultRowDecoder.isConstantLength(dataType)) {

                retrieveByOffset = false;
            }

            final Column<DATA_TYPE> column = new Column<>(resultSetColumn, retrieveByOffset);

            columns.add(column);
        }
    }

    @Override
    public final IGenericResultSetMetaData getResultSetMetaData() {

        return metaData;
    }

    final PREPARED_STATEMENT getPreparedStatement() {
        return preparedStatement;
    }

    final GenericResultSetMetaData<DATA_TYPE> getMetaData() {
        return metaData;
    }

    final Column<DATA_TYPE> checkNotClosedAndGetColumn(int index, JDBCType jdbcType) throws ResultSetClosedException, WrongColumnTypeException {

        Objects.requireNonNull(jdbcType);

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index);

        if (column.jdbcType != jdbcType) {

            throw new WrongColumnTypeException();
        }

        return column;
    }

    final Column<DATA_TYPE> checkNotClosedAndGetColumn(int index, EnumSet<JDBCType> jdbcTypes) throws ResultSetClosedException, WrongColumnTypeException {

        Objects.requireNonNull(jdbcTypes);

        final Column<DATA_TYPE> column = checkNotClosedAndGetColumn(index);

        if (!jdbcTypes.contains(column.jdbcType)) {

            throw new WrongColumnTypeException();
        }

        return column;
    }

    final Column<DATA_TYPE> checkNotClosedAndGetColumn(int index) throws ResultSetClosedException {

        checkNotClosed();

        return columns.get(index);
    }
}
