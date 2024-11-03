package jdbcdrivers.generic.api;

import java.math.BigDecimal;

import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;
import jdbcdrivers.generic.exceptions.WrongColumnTypeException;

public interface IGenericResultSet {

    /**
     * Close an open connection, and free up any related resources.
     *
     * @throws AlreadyClosedException thrown if connection has already been closed
     * @throws GenericProtocolException thrown if a database protocol communication error occured
     */
    void close() throws AlreadyClosedException, GenericProtocolException;

    /**
     * Move to next result row.
     *
     * @return {@code true} if there was a next result row, {@code false} otherwise
     *
     * @throws GenericProtocolException if any protocol communication error occurred
     */
    boolean next() throws GenericProtocolException;

    String getString(int index) throws ResultSetClosedException, WrongColumnTypeException;
    boolean getBoolean(int index) throws ResultSetClosedException, WrongColumnTypeException;
    byte getByte(int index) throws ResultSetClosedException, WrongColumnTypeException;
    short getShort(int index) throws ResultSetClosedException, WrongColumnTypeException;
    int getInt(int index) throws ResultSetClosedException, WrongColumnTypeException;
    long getLong(int index) throws ResultSetClosedException, WrongColumnTypeException;
    float getFloat(int index) throws ResultSetClosedException, WrongColumnTypeException;
    double getDouble(int index) throws ResultSetClosedException, WrongColumnTypeException;
    BigDecimal getBigDecimal(int index, int scale) throws ResultSetClosedException, WrongColumnTypeException;
    byte[] getBytes(int index) throws ResultSetClosedException, WrongColumnTypeException;
    int getDate(int index) throws ResultSetClosedException, WrongColumnTypeException;
    long getTime(int index) throws ResultSetClosedException, WrongColumnTypeException;
    long getTimestamp(int index) throws ResultSetClosedException, WrongColumnTypeException;
    Object getObject(int index) throws ResultSetClosedException, WrongColumnTypeException;

    IGenericResultSetMetaData getResultSetMetaData();
}
