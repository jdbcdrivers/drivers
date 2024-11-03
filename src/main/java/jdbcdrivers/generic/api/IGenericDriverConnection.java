package jdbcdrivers.generic.api;

import java.sql.Connection;

import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Interface for connection to a database.
 */
public interface IGenericDriverConnection {

    /**
     * Close an open connection, and free up any related resources.
     *
     * @throws AlreadyClosedException thrown if connection has already been closed
     * @throws GenericProtocolException thrown if a database protocol communication error occured
     */
    void close() throws AlreadyClosedException, GenericProtocolException;

    /**
     * Set auto commit mode for transactions. If set to {@code true} then the active transaction will be committed after every SQL statement
     * executed. If {@code false}, the transaction will not be committed until calling {@link #commit()}.
     *
     * @see {@link Connection#setAutoCommit(boolean)}
     *
     * @param on {@code true} if autocommit mode should be switched on, {@code false} if to be switched off
     *
     * @throws GenericProtocolException for any protocol communication error
     */
    void setAutoCommit(boolean on) throws GenericProtocolException;

    /**
     * Get the current autocommit state.
     *
     * @return current autocommit state
     *
     * @throws GenericProtocolException for any protocol communication error
     */
    boolean getAutoCommit() throws GenericProtocolException;

    /**
     * Create a {@link IGenericStatement} for executing SQLs on the database server.
     *
     * @param statementParameters various parameters
     *
     * @return an {@link IGenericStatement} for executing queries
     */
    IGenericStatement createStatement(GenericStatementExecutionOptions statementParameters);

    /**
     * Prepare for executing an SQL on the database server and return a {@link IGenericPreparedStatement}.
     *
     * @param statementExecutionOptions various options for SQL statement execution
     *
     * @return an {@link IGenericPreparedStatement} for executing queries
     *
     * @throws GenericProtocolException for any protocol communication error
     */
    IGenericPreparedStatement createPreparedStatement(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException;

    /**
     * Commit current transaction.
     *
     * @throws GenericProtocolException
     */
    void commit() throws GenericProtocolException;
}
