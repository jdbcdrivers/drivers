package jdbcdrivers.jdbc;

import java.sql.SQLException;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericResultSetMetaData;

public abstract class JDBCResultEntity extends BaseJDBC<Void, RuntimeException> {

    private final IGenericResultSetMetaData metaData;

    JDBCResultEntity(IGenericResultSetMetaData metaData) {

        this.metaData = Objects.requireNonNull(metaData);
    }

    final int toColumnIndex(String columnName) throws SQLException {

        Objects.requireNonNull(columnName);

        final Integer index = metaData.getColumnIndex(columnName);

        if (index == null) {

            throw new SQLException();
        }

        return index + 1;
    }

    final int toIndex(int columnIndex) throws SQLException {

        if (columnIndex <= 0) {

            throw new SQLException();
        }

        if (columnIndex > metaData.getNumColumns()) {

            throw new SQLException();
        }

        return columnIndex - 1;
    }
}
