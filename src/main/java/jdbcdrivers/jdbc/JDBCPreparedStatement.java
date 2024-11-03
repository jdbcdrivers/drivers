package jdbcdrivers.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericPreparedStatement;
import jdbcdrivers.generic.api.IGenericPreparedStatementParameters;
import jdbcdrivers.generic.api.IGenericResultSet;
import jdbcdrivers.generic.api.IGenericStatement;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.PreparedStatementClosedException;

/**
 * JDBC {@link PreparedStatement} implementation, delegates to the generic driver prepared statement set implementation.
 */
final class JDBCPreparedStatement extends JDBCStatement implements PreparedStatement {

    private final IGenericPreparedStatement genericPreparedStatement;

    private final IGenericPreparedStatementParameters parameters;

    JDBCPreparedStatement(IGenericStatement genericStatement, IGenericPreparedStatement genericPreparedStatement) {
        super(genericStatement);

        this.genericPreparedStatement = Objects.requireNonNull(genericPreparedStatement);

        this.parameters = genericPreparedStatement.allocateParameters();
    }

    @Override
    public void close() throws SQLException {

        try {
            genericPreparedStatement.close();
        }
        catch (AlreadyClosedException ex) {

            throw convert(ex);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
        finally {

            genericPreparedStatement.freeParameters(parameters);

            super.close();
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {

        final ResultSet resultSet;

        try {
            final IGenericResultSet genericResultSet = genericPreparedStatement.exeuteQuery(parameters);

            resultSet = new JDBCResultSet(genericResultSet);
        }
        catch (PreparedStatementClosedException ex) {

            throw convert(ex);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
        finally {
            parameters.clear();
        }

        return resultSet;
    }

    @Override
    public int executeUpdate() throws SQLException {

        final int updateCount;

        try {
            updateCount = genericPreparedStatement.exeuteUpdate(parameters);
        }
        catch (PreparedStatementClosedException ex) {

            throw convert(ex);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
        finally {
            parameters.clear();
        }

        return updateCount;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {

        parameters.setNull(getIndex(parameterIndex), JDBCType.valueOf(sqlType));
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {

        parameters.setBoolean(getIndex(parameterIndex), x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {

        parameters.setByte(getIndex(parameterIndex), x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {

        parameters.setShort(getIndex(parameterIndex), x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {

        parameters.setInt(getIndex(parameterIndex), x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {

        parameters.setLong(getIndex(parameterIndex), x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {

        parameters.setFloat(getIndex(parameterIndex), x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {

        parameters.setDouble(getIndex(parameterIndex), x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

        parameters.setObject(getIndex(parameterIndex), x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {

        parameters.setObject(getIndex(parameterIndex), x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {

        parameters.setObject(getIndex(parameterIndex), x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {

        parameters.setObject(getIndex(parameterIndex), x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

        parameters.setObject(getIndex(parameterIndex), x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void clearParameters() throws SQLException {

        parameters.clear();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

        parameters.setObject(getIndex(parameterIndex), x, JDBCType.valueOf(targetSqlType));
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {

        parameters.setObject(getIndex(parameterIndex), x);
    }

    @Override
    public boolean execute() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void addBatch() throws SQLException {

        try {
            genericPreparedStatement.addBatch(parameters);
        }
        catch (PreparedStatementClosedException ex) {

            throw convert(ex);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {

        final int[] updateCounts;

        try {
            updateCounts = genericPreparedStatement.executeBatches();
        }
        catch (PreparedStatementClosedException ex) {

            throw convert(ex);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }

        return updateCounts;
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    private static int getIndex(int parameterIndex) throws SQLException {

        if (parameterIndex <= 0) {

            throw new SQLException();
        }

        return parameterIndex - 1;
    }
}
