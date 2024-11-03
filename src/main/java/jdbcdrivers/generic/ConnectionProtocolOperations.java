package jdbcdrivers.generic;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Properties;

import jdbcdrivers.databaseprotocol.api.IDatabaseProtocol.PreparedStatementResult;
import jdbcdrivers.databaseprotocol.api.IGenericPreparedStatementParameterGetters;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

interface ConnectionProtocolOperations<PREPARED_STATEMENT, DATA_TYPE> {

    void performInitialSetup(URI uri, Properties properties) throws GenericProtocolException;

    void setAutoCommit(boolean on) throws GenericProtocolException, IOException;

    PreparedStatementResult<PREPARED_STATEMENT> prepareStatement(String sql, GenericStatementExecutionOptions statementParameters) throws GenericProtocolException;

    GenericResultSet executePreparedQuery(PREPARED_STATEMENT preparedQuery, GenericPreparedStatementParameters parameters) throws GenericProtocolException;

    int executePreparedUpdate(PREPARED_STATEMENT preparedStatement, GenericPreparedStatementParameters preparedStatementParameters) throws GenericProtocolException;

    int[] executeBatches(PREPARED_STATEMENT preparedStatement, Collection<? extends IGenericPreparedStatementParameterGetters> batches) throws GenericProtocolException;

    void closePreparedStatement(PREPARED_STATEMENT preparedStatement) throws GenericProtocolException;

    void commit() throws GenericProtocolException, IOException;

    void sendClose() throws GenericProtocolException;
}
