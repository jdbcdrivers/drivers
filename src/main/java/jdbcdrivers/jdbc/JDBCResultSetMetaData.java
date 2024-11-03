package jdbcdrivers.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericResultSetMetaData;
import jdbcdrivers.generic.api.IResultSetColumn;

/**
 * JDBC {@link ResultSetMetaData} implementation, delegates to the generic driver result set meta data implementation.
 */
final class JDBCResultSetMetaData extends JDBCResultEntity implements ResultSetMetaData {

    private final IGenericResultSetMetaData genericResultSetMetaData;

    JDBCResultSetMetaData(IGenericResultSetMetaData genericResultSetMetaData) {
        super(genericResultSetMetaData);

        this.genericResultSetMetaData = Objects.requireNonNull(genericResultSetMetaData);
    }

    @Override
    public int getColumnCount() throws SQLException {

        return genericResultSetMetaData.getNumColumns();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int isNullable(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSigned(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {

        final IResultSetColumn resultColumn = genericResultSetMetaData.getResultSetColumn(toIndex(column));

        return resultColumn.getLabel();
    }

    @Override
    public String getColumnName(int column) throws SQLException {

        final IResultSetColumn resultColumn = genericResultSetMetaData.getResultSetColumn(toIndex(column));

        return resultColumn.getName();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getPrecision(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getScale(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnType(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWritable(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {

        throw new UnsupportedOperationException();
    }
}
