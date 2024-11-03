package jdbcdrivers.generic.threadsafe;

import java.util.Objects;

import jdbcdrivers.generic.api.ExecuteResult;
import jdbcdrivers.generic.api.IGenericResultSet;
import jdbcdrivers.generic.api.IGenericStatement;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

final class SynchronizedGenericStatement implements IGenericStatement {

    private final IGenericStatement delegate;

    SynchronizedGenericStatement(IGenericStatement delegate) {

        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public synchronized IGenericResultSet executeQuery(String sql) throws GenericProtocolException {

        return new SynchronizedResultSet(delegate.executeQuery(sql));
    }

    @Override
    public synchronized int executeUpdate(String sql) throws GenericProtocolException {

        return delegate.executeUpdate(sql);
    }

    @Override
    public synchronized ExecuteResult execute(String sql) throws GenericProtocolException {

        return delegate.execute(sql);
    }
}
