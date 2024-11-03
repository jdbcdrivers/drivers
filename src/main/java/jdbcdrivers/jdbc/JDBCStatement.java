package jdbcdrivers.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Objects;

import jdbcdrivers.generic.api.ExecuteResult;
import jdbcdrivers.generic.api.IGenericResultSet;
import jdbcdrivers.generic.api.IGenericStatement;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * JDBC {@link Statement} implementation, delegates to the generic driver statement implementation.
 */
class JDBCStatement extends BaseJDBC<JDBCStatement.State, SQLException> implements Statement {

    private static final boolean DEBUG = Boolean.FALSE;

    enum State {

        IDLE(true, true, true, false, false),

        EXECUTING_QUERY(false, false, false, false, false),
        EXECUTING_UPDATE(false, false, false, false, false),
        EXECUTING(false, false, false, false, false),

        EXECUTE_QUERY_SUCCESS(true, true, true, false, false),
        EXECUTE_UPDATE_SUCCESS(true, true, true, false, false),
        EXECUTE_SUCCESS_RESULT_SET(true, true, true, true, false),
        EXECUTE_SUCCESS_UPDATE_COUNT(true, true, true, false, true),
        EXECUTE_SUCCESS_NONE(true, true, true, false, false),

        EXECUTE_QUERY_ERROR(true, true, true, false, false),
        EXECUTE_UPDATE_ERROR(true, true, true, false, false),
        EXECUTE_ERROR(true, true, true, false, false),

        CLOSED(false, false, false, false, false);

        private State(boolean canExecuteQuery, boolean canExecuteUpdate, boolean canExecute, boolean canGetResultSet, boolean canGetUpdateCount) {

            this.canExecuteQuery = canExecuteQuery;
            this.canExecuteUpdate = canExecuteUpdate;
            this.canExecute = canExecute;
            this.canGetResultSet = canGetResultSet;
            this.canGetUpdateCount = canGetUpdateCount;
        }

        private final boolean canExecuteQuery;
        private final boolean canExecuteUpdate;
        private final boolean canExecute;
        private final boolean canGetResultSet;
        private final boolean canGetUpdateCount;
    }

    private final IGenericStatement genericStatement;

    private JDBCResultSet resultSet;
    private int updateCount;

    JDBCStatement(IGenericStatement genericStatement) {
        super(State.IDLE, State.CLOSED, SQLException::new);

        this.genericStatement = Objects.requireNonNull(genericStatement);
    }

    @Override
    public void close() throws SQLException {

        setState(State.CLOSED);
    }

    @Override
    public final ResultSet executeQuery(String sql) throws SQLException {

        return executeForStates(State.EXECUTE_QUERY_SUCCESS, State.EXECUTE_QUERY_ERROR, s -> s.canExecuteQuery, () -> {

            final ResultSet resultSet;

            try {
                setState(State.EXECUTING_QUERY);

                final IGenericResultSet genericResultSet = genericStatement.executeQuery(sql);

                resultSet = new JDBCResultSet(genericResultSet);
            }
            catch (GenericProtocolException ex) {

                throw convert(ex);
            }

            return resultSet;
        });
    }

    @Override
    public final int executeUpdate(String sql) throws SQLException {

        return executeForStates(State.EXECUTE_UPDATE_SUCCESS, State.EXECUTE_UPDATE_ERROR, s -> s.canExecuteUpdate, () -> {

            final int updateCount;

            try {
                updateCount = genericStatement.executeUpdate(sql);
            }
            catch (GenericProtocolException ex) {

                throw convert(ex);
            }

            return updateCount;
        });
    }

    @Override
    public final int getMaxFieldSize() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setMaxFieldSize(int max) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getMaxRows() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setMaxRows(int max) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setEscapeProcessing(boolean enable) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getQueryTimeout() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setQueryTimeout(int seconds) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void cancel() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void clearWarnings() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setCursorName(String name) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean execute(String sql) throws SQLException {

        if (DEBUG) {

            System.out.println("execute sql '" + sql + '\'');
        }

        ExecuteResult executeResult = null;
        final State nextState;
        final boolean result;

        boolean ok = false;

        try {
            executeResult = executeForStates(s -> s.canExecute, () -> {

                final ExecuteResult statementResult;

                try {
                    statementResult = genericStatement.execute(sql);
                }
                catch (GenericProtocolException ex) {

                    throw convert(ex);
                }

                return statementResult;
            });

            ok = true;
        }
        finally {

            if (ok) {
                switch (executeResult.getResultType()) {

                case RESULT_SET:

                    nextState = State.EXECUTE_SUCCESS_RESULT_SET;
                    result = true;
                    break;

                case UPDATE_COUNT:

                    nextState = State.EXECUTE_SUCCESS_UPDATE_COUNT;
                    result = false;
                    break;

                case NONE:

                    nextState = State.EXECUTE_SUCCESS_NONE;
                    result = false;
                    break;

                default:
                    throw new UnsupportedOperationException();
                }
            }
            else {
                nextState = State.EXECUTE_ERROR;
                result = false;
            }
        }

        setState(nextState);

        return result;
    }

    @Override
    public final ResultSet getResultSet() throws SQLException {

        return executeForStates(s -> s.canGetResultSet, () -> resultSet);
    }

    @Override
    public final int getUpdateCount() throws SQLException {

        return executeForStates(s -> s.canGetUpdateCount, () -> updateCount);
    }

    @Override
    public final boolean getMoreResults() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setFetchDirection(int direction) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getFetchDirection() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setFetchSize(int rows) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getFetchSize() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getResultSetConcurrency() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getResultSetType() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void addBatch(String sql) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void clearBatch() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int[] executeBatch() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final Connection getConnection() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean getMoreResults(int current) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int executeUpdate(String sql, String[] columnNames) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean execute(String sql, int[] columnIndexes) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean execute(String sql, String[] columnNames) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final int getResultSetHoldability() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isClosed() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void setPoolable(boolean poolable) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isPoolable() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final void closeOnCompletion() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isCloseOnCompletion() throws SQLException {

        throw new UnsupportedOperationException();
    }
}
