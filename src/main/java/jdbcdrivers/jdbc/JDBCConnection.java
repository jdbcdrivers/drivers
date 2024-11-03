package jdbcdrivers.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;

import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.api.IGenericDriverConnection;
import jdbcdrivers.generic.api.IGenericPreparedStatement;
import jdbcdrivers.generic.api.IGenericStatement;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * JDBC {@link Connection} implementation, delegates to the generic driver connection implementation.
 */
final class JDBCConnection extends BaseJDBC<Void, RuntimeException> implements Connection {

    private final IGenericDriverConnection genericDriverConnection;

    JDBCConnection(IGenericDriverConnection genericDriverConnection) {

        this.genericDriverConnection = Objects.requireNonNull(genericDriverConnection);
    }

    @Override
    public Statement createStatement() throws SQLException {

        final IGenericStatement genericStatement = genericDriverConnection.createStatement(JDBCStatementExecutionOptions.getDefaultStatementExecutionOptions());

        return new JDBCStatement(genericStatement);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {

        return prepareStatement(sql, JDBCStatementExecutionOptions.getDefaultStatementExecutionOptions());
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

        try {
            genericDriverConnection.setAutoCommit(autoCommit);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {

        final boolean result;

        try {
            result = genericDriverConnection.getAutoCommit();
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public void commit() throws SQLException {

        try {
            genericDriverConnection.commit();
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
    }

    @Override
    public void rollback() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws SQLException {

        try {
            genericDriverConnection.close();
        }
        catch (AlreadyClosedException ex) {

            throw convert(ex);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getCatalog() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {

        return createStatement(JDBCStatementExecutionOptions.getStatementExecutionOptions(resultSetType, resultSetConcurrency));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        return prepareStatement(sql, JDBCStatementExecutionOptions.getStatementExecutionOptions(resultSetType, resultSetConcurrency));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getHoldability() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        return prepareStatement(sql, JDBCStatementExecutionOptions.getStatementExecutionOptions(resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {

        return prepareStatement(sql, JDBCStatementExecutionOptions.getStatementExecutionOptions(autoGeneratedKeys));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Clob createClob() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Blob createBlob() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public NClob createNClob() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getClientInfo() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setSchema(String schema) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getSchema() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void abort(Executor executor) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {

        throw new UnsupportedOperationException();
    }

    private Statement createStatement(GenericStatementExecutionOptions statementParameters) {

        Objects.requireNonNull(statementParameters);

        return createStatement(statementParameters);
    }

    private PreparedStatement prepareStatement(String sql, GenericStatementExecutionOptions statementParameters) throws SQLException {

        Objects.requireNonNull(statementParameters);

        final IGenericStatement genericStatement = genericDriverConnection.createStatement(statementParameters);

        final IGenericPreparedStatement genericPreparedStatement;

        try {
            genericPreparedStatement = genericDriverConnection.createPreparedStatement(sql, statementParameters);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }

        return new JDBCPreparedStatement(genericStatement, genericPreparedStatement);
    }
}
