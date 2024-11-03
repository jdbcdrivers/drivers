package jdbcdrivers.generic;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

import jdbcdrivers.databaseprotocol.api.IDatabaseProtocol.PreparedStatementResult;
import jdbcdrivers.databaseprotocol.api.IGenericPreparedStatementParameterGetters;
import jdbcdrivers.generic.api.ExecuteResult;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

final class SynchronizedConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> implements IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> {

    private final IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> delegate;

    SynchronizedConnectionProtocol(IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> delegate) {

        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public synchronized void closeResultSet(BaseSwappableResultSet resultSet) throws GenericProtocolException {

        delegate.closeResultSet(resultSet);
    }

    @Override
    public synchronized void retrieveResultRows(byte[] dst, int maxRows, int numBytesPerRow, RetrievedRows retrievedRows) throws GenericProtocolException {

        delegate.retrieveResultRows(dst, maxRows, numBytesPerRow, retrievedRows);
    }

    @Override
    public synchronized GenericResultSet executeQuery(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException {

        return delegate.executeQuery(sql, statementExecutionOptions);
    }

    @Override
    public synchronized int executeUpdate(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException {

        return delegate.executeUpdate(sql, statementExecutionOptions);
    }

    @Override
    public synchronized ExecuteResult execute(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException {

        return delegate.execute(sql, statementExecutionOptions);
    }

    @Override
    public synchronized void performInitialSetup(URI uri, Properties properties) throws GenericProtocolException {

        delegate.performInitialSetup(uri, properties);
    }

    @Override
    public synchronized void setAutoCommit(boolean on) throws GenericProtocolException, IOException {

        delegate.setAutoCommit(on);
    }

    @Override
    public synchronized PreparedStatementResult<PREPARED_STATEMENT> prepareStatement(String sql, GenericStatementExecutionOptions statementParameters) throws GenericProtocolException {

        return delegate.prepareStatement(sql, statementParameters);
    }

    @Override
    public synchronized GenericResultSet executePreparedQuery(PREPARED_STATEMENT preparedQuery, GenericPreparedStatementParameters parameters) throws GenericProtocolException {

        return delegate.executePreparedQuery(preparedQuery, parameters);
    }

    @Override
    public synchronized int executePreparedUpdate(PREPARED_STATEMENT preparedStatement, GenericPreparedStatementParameters preparedStatementParameters)
            throws GenericProtocolException {

        return delegate.executePreparedUpdate(preparedStatement, preparedStatementParameters);
    }

    @Override
    public synchronized int[] executeBatches(PREPARED_STATEMENT preparedStatement, Collection<? extends IGenericPreparedStatementParameterGetters> batches)
            throws GenericProtocolException {

        return delegate.executeBatches(preparedStatement, batches);
    }

    @Override
    public synchronized void closePreparedStatement(PREPARED_STATEMENT preparedStatement) throws GenericProtocolException {

        delegate.closePreparedStatement(preparedStatement);
    }

    @Override
    public synchronized void commit() throws GenericProtocolException, IOException {

        delegate.commit();
    }

    @Override
    public synchronized void sendClose() throws GenericProtocolException {

        delegate.sendClose();
    }
}
