package jdbcdrivers.generic.api;

import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.PreparedStatementClosedException;

/**
 * Interface for executing prepared statements.
 */
public interface IGenericPreparedStatement {

    /**
     * Close an open prepared statement, and free up any related resources.
     *
     * @throws AlreadyClosedException thrown if connection has already been closed
     * @throws GenericProtocolException thrown if a database protocol communication error occured
     */
    void close() throws AlreadyClosedException, GenericProtocolException;

    /**
     * Allocate a {@link IGenericPreparedStatementParameters} parameter object to pass parameters to query execution.
     *
     * @return an allocated {@link IGenericPreparedStatementParameters}
     */
    IGenericPreparedStatementParameters allocateParameters();

    /**
     * Free up a {@link IGenericPreparedStatementParameters}.
     *
     * @param parameters the {@link IGenericPreparedStatementParameters} to free up
     */
    void freeParameters(IGenericPreparedStatementParameters parameters);

    /**
     * Execute the prepared query with the supplied parameters.
     *
     * @param parameters the parameters passed to the query
     *
     * @return a {@link IGenericResultSet} for iterating result rows
     *
     * @throws PreparedStatementClosedException if the prepared statement has been closed
     * @throws GenericProtocolException if any database communication error occurred
     */
    IGenericResultSet exeuteQuery(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException;

    /**
     * Execute the prepared update with the supplied parameters.
     *
     * @param parameters the parameters passed to the SQL statement
     *
     * @return the number of rows inserted, updated or deleted
     *
     * @throws PreparedStatementClosedException if the prepared statement has been closed
     * @throws GenericProtocolException if any database communication error occurred
     */
    int exeuteUpdate(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException;

    /**
     * Add parameters for batch execution of the prepared SQL.
     *
     * @param parameters the parameters passed to the SQL statement
     *
     * @return the number of rows inserted, updated or deleted
     *
     * @throws PreparedStatementClosedException if the prepared statement has been closed
     * @throws GenericProtocolException if any database communication error occurred
     */
    void addBatch(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException;

    /**
     * Execute a batch the prepared SQL.
     *
     * @return an array of the number of rows inserted, updated or deleted, in the same order as batch parameters were added
     *
     * @throws PreparedStatementClosedException if the prepared statement has been closed
     * @throws GenericProtocolException if any database communication error occurred
     */
    int[] executeBatches() throws PreparedStatementClosedException, GenericProtocolException;
}
