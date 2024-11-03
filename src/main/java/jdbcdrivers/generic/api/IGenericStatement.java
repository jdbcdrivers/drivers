package jdbcdrivers.generic.api;

import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Interface for executing SQL statements.
 */
public interface IGenericStatement {

    /**
     * Execute an SQL select query and return resulting rows.
     *
     * @param sql the SQL to execute
     *
     * @return a {@link IGenericResultSet} for retrieving any result rows
     *
     * @throws GenericProtocolException if any database protocol communication error occurred
     */
    IGenericResultSet executeQuery(String sql) throws GenericProtocolException;

    /**
     * Execute an SQL insert, update or delete statement.
     *
     * @param sql the SQL to execute
     *
     * @return a {@link IGenericResultSet} for retrieving any result rows
     *
     * @throws GenericProtocolException if any database protocol communication error occurred
     */
    int executeUpdate(String sql) throws GenericProtocolException;

    /**
     * Execute an SQL.
     *
     * @param sql the SQL to execute
     *
     * @return result of execution
     *
     * @throws GenericProtocolException if any database protocol communication error occurred
     */
    ExecuteResult execute(String sql) throws GenericProtocolException;
}
