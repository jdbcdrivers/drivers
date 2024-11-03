package jdbcdrivers.generic.threadsafe;

import java.util.Objects;

import jdbcdrivers.generic.api.IGenericPreparedStatement;
import jdbcdrivers.generic.api.IGenericPreparedStatementParameters;
import jdbcdrivers.generic.api.IGenericResultSet;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.PreparedStatementClosedException;

final class SynchronizedGenericPreparedStatement implements IGenericPreparedStatement {

    private final IGenericPreparedStatement delegate;

    SynchronizedGenericPreparedStatement(IGenericPreparedStatement delegate) {

        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public synchronized void close() throws AlreadyClosedException, GenericProtocolException {

        delegate.close();
    }

    @Override
    public synchronized IGenericPreparedStatementParameters allocateParameters() {

        return delegate.allocateParameters();
    }

    @Override
    public synchronized void freeParameters(IGenericPreparedStatementParameters preparedStatementParameters) {

        delegate.freeParameters(preparedStatementParameters);
    }

    @Override
    public synchronized IGenericResultSet exeuteQuery(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException {

        return new SynchronizedResultSet(delegate.exeuteQuery(parameters));
    }

    @Override
    public synchronized int exeuteUpdate(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException {

        return exeuteUpdate(parameters);
    }

    @Override
    public synchronized void addBatch(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException {

        delegate.addBatch(parameters);
    }

    @Override
    public synchronized int[] executeBatches() throws PreparedStatementClosedException, GenericProtocolException {

        return delegate.executeBatches();
    }
}
