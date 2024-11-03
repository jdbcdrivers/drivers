package jdbcdrivers.generic;

import java.math.BigDecimal;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericResultSetMetaData;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;
import jdbcdrivers.generic.exceptions.WrongColumnTypeException;

/**
 * Base class for swapping out result sets, e.g. from direct connection to buffered.
 */
abstract class BaseSwappableResultSet extends GenericResultSet {

    abstract RowDataResultSet getDelegate();

    abstract void swap(RowDataResultSet newResultSet);

    private final ResultSetClosing resultSetClosing;

    BaseSwappableResultSet(ResultSetClosing resultSetClosing) {

        this.resultSetClosing = Objects.requireNonNull(resultSetClosing);
    }

    @Override
    public final void close() throws AlreadyClosedException, GenericProtocolException {

        try {
            getDelegate().close();
        }
        finally {
            resultSetClosing.closeResultSet(this);
        }
    }

    @Override
    public final boolean next() throws GenericProtocolException {

        return getDelegate().next();
    }

    @Override
    public final String getString(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getString(index);
    }

    @Override
    public final boolean getBoolean(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getBoolean(index);
    }

    @Override
    public final byte getByte(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getByte(index);
    }

    @Override
    public final short getShort(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getShort(index);
    }

    @Override
    public final int getInt(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getInt(index);
    }

    @Override
    public final long getLong(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getLong(index);
    }

    @Override
    public final float getFloat(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getFloat(index);
    }

    @Override
    public final double getDouble(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getDouble(index);
    }

    @Override
    public final BigDecimal getBigDecimal(int index, int scale) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getBigDecimal(index, scale);
    }

    @Override
    public final byte[] getBytes(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getBytes(index);
    }

    @Override
    public final int getDate(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getDate(index);
    }

    @Override
    public final long getTime(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getTime(index);
    }

    @Override
    public final long getTimestamp(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getTimestamp(index);
    }

    @Override
    public final Object getObject(int index) throws ResultSetClosedException, WrongColumnTypeException {

        return getDelegate().getObject(index);
    }

    @Override
    public final IGenericResultSetMetaData getResultSetMetaData() {

        return getDelegate().getResultSetMetaData();
    }
}
