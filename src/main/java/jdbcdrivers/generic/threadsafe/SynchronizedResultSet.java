package jdbcdrivers.generic.threadsafe;

import java.math.BigDecimal;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericResultSet;
import jdbcdrivers.generic.api.IGenericResultSetMetaData;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;
import jdbcdrivers.generic.exceptions.WrongColumnTypeException;

final class SynchronizedResultSet implements IGenericResultSet {

    private final IGenericResultSet delegate;

    SynchronizedResultSet(IGenericResultSet delegate) {

        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public synchronized void close() throws AlreadyClosedException, GenericProtocolException {

        delegate.close();
    }

    @Override
    public synchronized boolean next() throws GenericProtocolException {

        return delegate.next();
    }

    @Override
    public synchronized String getString(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getString(index);
    }

    @Override
    public synchronized boolean getBoolean(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getBoolean(index);
    }

    @Override
    public synchronized byte getByte(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getByte(index);
    }

    @Override
    public synchronized short getShort(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getShort(index);
    }

    @Override
    public synchronized int getInt(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getInt(index);
    }

    @Override
    public synchronized long getLong(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getLong(index);
    }

    @Override
    public synchronized float getFloat(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getFloat(index);
    }

    @Override
    public synchronized double getDouble(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getDouble(index);
    }

    @Override
    public synchronized BigDecimal getBigDecimal(int index, int scale) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getBigDecimal(index, scale);
    }

    @Override
    public synchronized byte[] getBytes(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getBytes(index);
    }

    @Override
    public synchronized int getDate(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getDate(index);
    }

    @Override
    public synchronized long getTime(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getTime(index);
    }

    @Override
    public synchronized long getTimestamp(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getTimestamp(index);
    }

    @Override
    public synchronized Object getObject(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return delegate.getObject(index);
    }

    @Override
    public synchronized IGenericResultSetMetaData getResultSetMetaData() {

        return delegate.getResultSetMetaData();
    }
}
