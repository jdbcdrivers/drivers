package jdbcdrivers.generic;

import jdbcdrivers.generic.api.ExecuteResult;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Interface for executing SQL statements.
 *
 * @param <DATA_TYPE> database protocol datatype
 */
interface SQLExecutor {

    GenericResultSet executeQuery(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException;

    int executeUpdate(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException;

    ExecuteResult execute(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException;
}
