package jdbcdrivers.generic;

import java.sql.JDBCType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericResultSetMetaData;
import jdbcdrivers.generic.api.IResultSetColumn;

/**
 * Generic result set meta data implementation.
 */
public final class GenericResultSetMetaData<DATA_TYPE> extends ResultColumns<DATA_TYPE> implements IGenericResultSetMetaData {

    public static final class ResultSetColumn<DATA_TYPE> extends ResultColumn<DATA_TYPE> implements IResultSetColumn {

        private final String name;
        private final String label;
        private final JDBCType type;
        private final boolean nullable;
        private final int offset;
        private final int byteArrayRowOffset;

        public ResultSetColumn(DATA_TYPE dataType, int maxLength, int numRowBytesForColumn, String name, String label, JDBCType type, boolean nullable, int offset,
                int byteArrayRowOffset) {
            super(dataType, maxLength, numRowBytesForColumn);

            Objects.requireNonNull(name);
            Objects.requireNonNull(label);
            Objects.requireNonNull(type);

            if (offset < 0) {

                throw new IllegalArgumentException();
            }

            if (byteArrayRowOffset < 0) {

                throw new IllegalArgumentException();
            }

            this.name = name;
            this.label = label;
            this.type = type;
            this.nullable = nullable;
            this.offset = offset;
            this.byteArrayRowOffset = byteArrayRowOffset;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public JDBCType getJDBCType() {
            return type;
        }

        public boolean isNullable() {
            return nullable;
        }

        public int getOffset() {
            return offset;
        }

        int getByteArrayRowOffset() {
            return byteArrayRowOffset;
        }
    }

    public static final long NO_TOTAL_NUM_ROWS = -1;

    private final long totalNumRows;
    private final int maxDataBytesPerRow;
    private final int maxBytesPerResultRow;
    private final ResultSetColumn<DATA_TYPE>[] columns;

    private final Map<String, Integer> indexByColumnLabel;

    public GenericResultSetMetaData(int maxDataBytesPerRow, int maxBytesPerResultRow, List<ResultSetColumn<DATA_TYPE>> columns) {
        this(NO_TOTAL_NUM_ROWS, maxDataBytesPerRow, maxBytesPerResultRow, columns);
    }

    public GenericResultSetMetaData(long totalNumRows, int maxDataBytesPerRow, int maxBytesPerResultRow, List<ResultSetColumn<DATA_TYPE>> columns) {
        super(columns);

        if (maxDataBytesPerRow < 1) {

            throw new IllegalArgumentException();
        }

        if (maxBytesPerResultRow < 1) {

            throw new IllegalArgumentException();
        }

        this.totalNumRows = totalNumRows;
        this.maxDataBytesPerRow = maxDataBytesPerRow;
        this.maxBytesPerResultRow = maxBytesPerResultRow;

        this.columns = toArray(columns);

        final int numColumns = columns.size();

        this.indexByColumnLabel = new HashMap<String, Integer>(numColumns);

        for (int i = 0; i < numColumns; ++ i) {

            final ResultSetColumn<DATA_TYPE> resultColumn = this.columns[i];

            indexByColumnLabel.put(columnLabelToKey(resultColumn.getLabel()), i);
        }
    }

    @SuppressWarnings("unchecked")
    private static <DATA_TYPE> ResultSetColumn<DATA_TYPE>[] toArray(List<ResultSetColumn<DATA_TYPE>> columns) {

        return columns.toArray(ResultSetColumn[]::new);
    }

    public long getTotalNumRows() {
        return totalNumRows;
    }

    @Deprecated
    public int getMaxDataBytesPerRow() {
        return maxDataBytesPerRow;
    }

    public int getMaxBytesPerResultRow() {
        return maxBytesPerResultRow;
    }

    @Override
    public IResultSetColumn getResultSetColumn(int index) {

        return columns[index];
    }

    @Override
    public Integer getColumnIndex(String columnName) {

        Objects.requireNonNull(columnName);

        return indexByColumnLabel.get(columnLabelToKey(columnName));
    }

    final ResultSetColumn<DATA_TYPE> getColumn(int index) {

        return columns[index];
    }

    private static String columnLabelToKey(String columnLabel) {

        return columnLabel.toLowerCase();
    }
}
