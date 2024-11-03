package jdbcdrivers.generic.threadsafe;

import java.util.Objects;

import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.api.IGenericDriverConnection;
import jdbcdrivers.generic.api.IGenericPreparedStatement;
import jdbcdrivers.generic.api.IGenericStatement;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

final class SynchronizedDriverConnection implements IGenericDriverConnection {

    private final IGenericDriverConnection delegate;

    SynchronizedDriverConnection(IGenericDriverConnection delegate) {

        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public synchronized void close() throws AlreadyClosedException, GenericProtocolException {

        delegate.close();
    }

    @Override
    public synchronized void setAutoCommit(boolean on) throws GenericProtocolException {

        delegate.setAutoCommit(on);
    }

    @Override
    public synchronized boolean getAutoCommit() throws GenericProtocolException {

        return delegate.getAutoCommit();
    }

    @Override
    public synchronized IGenericStatement createStatement(GenericStatementExecutionOptions statementParameters) {

        return new SynchronizedGenericStatement(delegate.createStatement(statementParameters));
    }

    @Override
    public synchronized IGenericPreparedStatement createPreparedStatement(String sql, GenericStatementExecutionOptions statementParameters) throws GenericProtocolException {

        return new SynchronizedGenericPreparedStatement(delegate.createPreparedStatement(sql, statementParameters));
    }

    @Override
    public synchronized void commit() throws GenericProtocolException {

        delegate.commit();
    }
}
