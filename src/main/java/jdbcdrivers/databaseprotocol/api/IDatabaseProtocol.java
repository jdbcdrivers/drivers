package jdbcdrivers.databaseprotocol.api;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

import jdbcdrivers.generic.GenericResultSetMetaData;
import jdbcdrivers.generic.ResultRowDecoder;
import jdbcdrivers.generic.api.ExecuteResult;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.exceptions.ProtocolErrorCodeException;
import jdbcdrivers.generic.exceptions.ProtocolErrorException;
import jdbcdrivers.generic.util.StringEncoder;

/**
 * Interface implemented by database protocols.
 *
 * @param <PREPARED_STATEMENT> database protocol prepared statement type
 * @param <DATA_TYPE> database protocol datatype
 */
public interface IDatabaseProtocol<PREPARED_STATEMENT, DATA_TYPE> {

    void performInitialSetup(DataOutputStream dataOutput, DataInput dataInput, URI uri, Properties properties)
            throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    void setAutoCommit(DataOutputStream dataOutput, DataInput dataInput, boolean on) throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    void executeQuery(DataOutputStream dataOutput, DataInput dataInput, String sql) throws ProtocolErrorException, IOException;

    int executeUpdate(DataOutputStream dataOutput, DataInput dataInput, String sql) throws ProtocolErrorException, IOException;

    ExecuteResult executeSQL(DataOutputStream dataOutput, DataInput dataInput, String sql)
            throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    public static final class PreparedStatementResult<PREPARED_STATEMENT> {

        private final PREPARED_STATEMENT preparedStatement;
        private final int numParameters;

        public PreparedStatementResult(PREPARED_STATEMENT preparedStatement, int numParameters) {

            Objects.requireNonNull(preparedStatement);

            if (numParameters < 0) {

                throw new IllegalArgumentException();
            }

            this.preparedStatement = preparedStatement;
            this.numParameters = numParameters;
        }

        public PREPARED_STATEMENT getPreparedStatement() {
            return preparedStatement;
        }

        public int getNumParameters() {
            return numParameters;
        }
    }

    PreparedStatementResult<PREPARED_STATEMENT> prepareStatement(DataOutputStream dataOutput, DataInput dataInput, String sql, GenericStatementExecutionOptions statementParameters)
            throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    void executePreparedQuery(PREPARED_STATEMENT preparedStatement, DataOutputStream dataOutput, DataInput dataInput,
            IGenericPreparedStatementParameterGetters preparedStatementParameters, StringEncoder stringEncoder)
                    throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    int executePreparedUpdate(PREPARED_STATEMENT preparedStatement, DataOutputStream dataOutput, DataInput dataInput,
            IGenericPreparedStatementParameterGetters preparedStatementParameters, StringEncoder stringEncoder)
                    throws ProtocolErrorException, ProtocolErrorCodeException,  IOException;

    void closePreparedStatement(PREPARED_STATEMENT preparedStatement, DataOutputStream dataOutput, DataInput dataInput)
            throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    int[] executeBatches(PREPARED_STATEMENT preparedStatement, Collection<? extends IGenericPreparedStatementParameterGetters> batches, DataOutputStream dataOutput,
            DataInput dataInput, StringEncoder stringEncoder) throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    void sendBegin(DataOutputStream dataOutput, DataInput dataInput) throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    void sendCommit(DataOutputStream dataOutput, DataInput dataInput) throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    GenericResultSetMetaData<DATA_TYPE> retrieveResultMetaData(DataInput dataInput) throws ProtocolErrorException, IOException;

    GenericResultSetMetaData<DATA_TYPE> retrievePreparedResultMetaData(DataInput dataInput, PREPARED_STATEMENT preparedStatement)
            throws ProtocolErrorException, IOException;

    void retrieveResultRows(DataInput dataInput, byte[] dst, int maxRowsToRetrieve, int numBytesPerRow, IRetrievedRows retrievedRows)
            throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    @Deprecated // currently not in use
    void closeResultSet(DataOutputStream dataOutput, DataInput dataInput) throws ProtocolErrorException, ProtocolErrorCodeException, IOException;

    void sendClose(DataOutputStream dataOutput, DataInput dataInput) throws ProtocolErrorException, IOException;

    Object getPreparedStatementIdentifier(PREPARED_STATEMENT preparedStatement);

    ResultRowDecoder<DATA_TYPE> getResultRowDecoder();
}
