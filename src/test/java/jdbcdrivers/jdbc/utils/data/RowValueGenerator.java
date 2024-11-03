package jdbcdrivers.jdbc.utils.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jdbcdrivers.jdbc.utils.schema.Column;
import jdbcdrivers.jdbc.utils.schema.Table;

public final class RowValueGenerator {

    private final Table table;

    private final ArrayList<Object[]> rows;

    private int sequenceNoGenerator;

    public RowValueGenerator(Table table) {

        this.table = Objects.requireNonNull(table);

        this.rows = new ArrayList<Object[]>();

        this.sequenceNoGenerator = 0;
    }

    public void add(int numRows) {

        if (numRows <= 0) {

            throw new IllegalArgumentException();
        }

        rows.ensureCapacity(rows.size() + numRows);

        final List<Column> columns = table.getColumns();

        final int numColumns = columns.size();

        for (int i = 0; i < numRows; ++ i) {

            final Object[] row = new Object[numColumns];

            for (int columnIndex = 0; columnIndex < numColumns; ++ columnIndex) {

                final int sequenceNo = sequenceNoGenerator ++;

                row[columnIndex] = generateValue(columns.get(columnIndex), sequenceNo);
            }

            rows.add(row);
        }
    }

    public List<Object[]> getRows() {

        return Collections.unmodifiableList(new ArrayList<>(rows));
    }

    private static Object generateValue(Column column, int sequenceNo) {

        final Object value;

        switch (column.getJDBCType()) {

        case INTEGER:

            value = sequenceNo;
            break;

        case CHAR:
        case VARCHAR:
        case LONGVARCHAR:

            final int length = column.getLength();

            final StringBuilder sb = new StringBuilder(length);

            final String sequenceNoString = String.valueOf(sequenceNo);
            final int sequenceNoStringLength = sequenceNoString.length();

            int remaining = 3; //length - 1;

            do {
                if (remaining == 0) {

                    throw new IllegalStateException();
                }

                final String toAppend;

                if (remaining < sequenceNoStringLength) {

                    toAppend = sequenceNoString.substring(0, remaining);

                    remaining = 0;
                }
                else {
                    toAppend = sequenceNoString;

                    remaining -= sequenceNoStringLength;
                }

                sb.append(toAppend);

            } while (remaining != 0);

            value = sb.toString();
            break;

        default:
            throw new UnsupportedOperationException();
        }

        return value;
    }
}
