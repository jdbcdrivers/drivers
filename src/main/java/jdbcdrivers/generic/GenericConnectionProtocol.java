package jdbcdrivers.generic;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

import jdbcdrivers.databaseprotocol.api.IDatabaseProtocol;
import jdbcdrivers.databaseprotocol.api.IDatabaseProtocol.PreparedStatementResult;
import jdbcdrivers.databaseprotocol.api.IGenericPreparedStatementParameterGetters;
import jdbcdrivers.databaseprotocol.api.IRetrievedRows;
import jdbcdrivers.generic.api.ExecuteResult;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ProtocolIOException;
import jdbcdrivers.generic.exceptions.ProtocolStateException;
import jdbcdrivers.generic.util.StringEncoder;

/**
 * Base class for database connection protocol. Deals with state machinery for communication towards the database, and forwards to database specific code.
 */
final class GenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> extends StateObject<GenericConnectionProtocol.State, ProtocolStateException> {

    enum State {

        CREATED(),

        PERFORMING_INITIAL_SETUP(),

        IDLE(true, true, true, true, true, true, true),

        PREPARING_STATEMENT(),

        EXECUTING_QUERY(),
        EXECUTED_QUERY(true, true, true, true, true, false, false),
        EXECUTED_QUERY_RETRIEVE_RESULT(true, true, false, false, false, false, false),

        EXECUTING_PREPARED_QUERY(),
        EXECUTED_PREPARED_QUERY(true, true, true, true, true, false, false),

        @Deprecated
        EXECUTED_PREPARED_QUERY_RETRIEVE_RESULT(true, true, false, false, false, false, false),

        RETRIEVING_RESULT_SET_META_DATA(true, true, false, false, false, false, false),
        RETRIEVING_RESULT_SET(true, true, false, false, false, false, false),

        EXECUTING_UPDATE(),
        EXECUTED_UPDATE(true, true, true, true, true, false, false),

        EXECUTING_SQL(),
        EXECUTED_SQL(true, true, true, true, true, true, true),

        EXECUTING_PREPARED_UPDATE(),
        EXECUTED_PREPARED_UPDATE(true, true, true, true, true, false, false),

        EXECUTING_BATCHES(),
        EXECUTED_BATCHES(true, true, true, true, true, true, true),

        RETRIEVING_KEYS_RESULT_SET_META_DATA(true, true, false, false, false, false, false),
        RETRIEVING_KEYS_RESULT_SET(true, true, false, false, false, false, false),

        CLOSED();

        private State() {
            this(false, false, false, false, false, false, false);
        }

        private State(boolean canPrepareStatements, boolean canClosePreparedStatement, boolean canExecuteSQL, boolean canExecutePreparedQuery, boolean canExecutePreparedUpdate,
                boolean canAddBatches, boolean canExecuteBatch) {

            this.canPrepareStatements = canPrepareStatements;
            this.canClosePreparedStatement = canClosePreparedStatement;
            this.canExecuteSQL = canExecuteSQL;
            this.canExecutePreparedQuery = canExecutePreparedQuery;
            this.canExecutePreparedUpdate = canExecutePreparedUpdate;
            this.canAddBatches = canAddBatches;
            this.canExecuteBatch = canExecuteBatch;
        }

        private final boolean canPrepareStatements;
        private final boolean canClosePreparedStatement;
        private final boolean canExecuteSQL;
        private final boolean canExecutePreparedQuery;
        private final boolean canExecutePreparedUpdate;
        private final boolean canAddBatches;
        private final boolean canExecuteBatch;
    }

    private static final int CACHED_RESULT_SET_CHUNK_SIZE = 10 * 1024;

    private final IDatabaseProtocol<PREPARED_STATEMENT, DATA_TYPE> databaseProtocol;
    private final StringEncoder stringEncoder;
    private final SwappableResultSetFactory swappableResultSetFactory;

    private final DataInput dataInput;
    private final DataOutputStream dataOutput;

    private final ProtocolData<PREPARED_STATEMENT> protocolData;

    private final IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> connectionProtocol;

    private boolean autoCommit;
    private boolean withinTransaction;

    GenericConnectionProtocol(DriverSocket socket, IDatabaseProtocol<PREPARED_STATEMENT, DATA_TYPE> databaseProtocol, StringEncoder stringEncoder,
            GenericDriverSynchronizerFactory genericDriverSynchronizerFactory) {
        super(State.IDLE, State.CLOSED, ProtocolStateException::new);

        Objects.requireNonNull(databaseProtocol);
        Objects.requireNonNull(stringEncoder);
        Objects.requireNonNull(genericDriverSynchronizerFactory);

        this.databaseProtocol = databaseProtocol;
        this.stringEncoder = stringEncoder;
        this.swappableResultSetFactory = genericDriverSynchronizerFactory.getSwappableResultSetFactory();

        this.dataInput = new DataInputStream(socket.getInputStream());
        this.dataOutput = new DataOutputStream(socket.getOutputStream());

        this.protocolData = new ProtocolData<>(s -> databaseProtocol.getPreparedStatementIdentifier(s));

        // For not exposing public methods
        final IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> connectionProtocol = new IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE>() {

            @Override
            public void closeResultSet(BaseSwappableResultSet resultSet) throws GenericProtocolException {

                GenericConnectionProtocol.this.closeResultSet(resultSet);
            }

            @Override
            public void retrieveResultRows(byte[] dst, int numRows, int maxRetrievedRows, RetrievedRows retrievedRows) throws GenericProtocolException {

                GenericConnectionProtocol.this.retrieveResultRow(dst, numRows, maxRetrievedRows, retrievedRows);
            }

            @Override
            public GenericResultSet executeQuery(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException {

                return GenericConnectionProtocol.this.executeQuery(sql);
            }

            @Override
            public int executeUpdate(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException {

                return GenericConnectionProtocol.this.executeUpdate(sql);
            }

            @Override
            public ExecuteResult execute(String sql, GenericStatementExecutionOptions statementExecutionOptions) throws GenericProtocolException {

                return GenericConnectionProtocol.this.executeSQL(sql);
            }

            @Override
            public void performInitialSetup(URI uri, Properties properties) throws GenericProtocolException {

                GenericConnectionProtocol.this.performInitialSetup(uri, properties);
            }

            @Override
            public void setAutoCommit(boolean on) throws GenericProtocolException, IOException {

                GenericConnectionProtocol.this.setAutoCommit(on);
            }

            @Override
            public PreparedStatementResult<PREPARED_STATEMENT> prepareStatement(String sql, GenericStatementExecutionOptions statementParameters) throws GenericProtocolException {

                return GenericConnectionProtocol.this.prepareStatement(sql, statementParameters);
            }

            @Override
            public GenericResultSet executePreparedQuery(PREPARED_STATEMENT preparedQuery, GenericPreparedStatementParameters parameters) throws GenericProtocolException {

                return GenericConnectionProtocol.this.executePreparedQuery(preparedQuery, parameters);
            }

            @Override
            public int executePreparedUpdate(PREPARED_STATEMENT preparedStatement, GenericPreparedStatementParameters preparedStatementParameters)
                    throws GenericProtocolException {

                return GenericConnectionProtocol.this.executePreparedUpdate(preparedStatement, preparedStatementParameters);
            }

            @Override
            public int[] executeBatches(PREPARED_STATEMENT preparedStatement, Collection<? extends IGenericPreparedStatementParameterGetters> batches)
                    throws GenericProtocolException {

                return GenericConnectionProtocol.this.executeBatches(preparedStatement, batches);
            }

            @Override
            public void closePreparedStatement(PREPARED_STATEMENT preparedStatement) throws GenericProtocolException {

                GenericConnectionProtocol.this.closePreparedStatement(preparedStatement);
            }

            @Override
            public void commit() throws GenericProtocolException, IOException {

                GenericConnectionProtocol.this.commit();
            }

            @Override
            public void sendClose() throws GenericProtocolException {

                GenericConnectionProtocol.this.sendClose();
            }
        };

        this.connectionProtocol = genericDriverSynchronizerFactory.synchronizedConnectionProtocol(connectionProtocol);

        this.withinTransaction = false;

        setState(State.CREATED);
    }

    SQLExecutor getSQLExecutor() {

        return connectionProtocol;
    }

    IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> getConnectionProtocolInterface() {

        return connectionProtocol;
    }

    private void performInitialSetup(URI uri, Properties properties) throws GenericProtocolException {

        checkState(State.CREATED);

        executeWithTemporaryState(State.PERFORMING_INITIAL_SETUP, () -> {

            try {
                databaseProtocol.performInitialSetup(dataOutput, dataInput, uri, properties);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return null;
        });

        setState(State.IDLE);
    }

    private void setAutoCommit(boolean on) throws GenericProtocolException, IOException {

        bufferAnyOngoingResultSet();

        this.autoCommit = on;

        databaseProtocol.setAutoCommit(dataOutput, dataInput, on);
    }

    private GenericResultSet executeQuery(String sql) throws GenericProtocolException {

        checkState(State.IDLE);

        bufferAnyOngoingResultSet();

        beginTransactionIfNotStarted();

        executeWithTemporaryStateAndSetNextState(State.EXECUTING_QUERY, State.EXECUTED_QUERY, () -> {

            try {
                databaseProtocol.executeQuery(dataOutput, dataInput, sql);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return null;
        });

        final GenericResultSet resultSet = retrieveResultMetaDataAndCreateResultSet();

        setState(State.EXECUTED_QUERY_RETRIEVE_RESULT);

        return resultSet;
    }

    private int executeUpdate(String sql) throws GenericProtocolException {

        checkState(State.IDLE);

        bufferAnyOngoingResultSet();

        beginTransactionIfNotStarted();

        final int updateCount = executeWithTemporaryStateAndSetNextState(State.EXECUTING_UPDATE, State.EXECUTED_UPDATE, () -> {

            final int result;

            try {
                result = databaseProtocol.executeUpdate(dataOutput, dataInput, sql);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return result;
        });

        return updateCount;
    }

    private ExecuteResult executeSQL(String sql) throws GenericProtocolException {

        checkState(s -> s.canExecuteSQL);

        bufferAnyOngoingResultSet();

        beginTransactionIfNotStarted();

        final ExecuteResult executeResultType = executeWithTemporaryStateAndSetNextState(State.EXECUTING_SQL, State.EXECUTED_SQL, () -> {

            final ExecuteResult result;

            try {
                result = databaseProtocol.executeSQL(dataOutput, dataInput, sql);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return result;
        });

        return executeResultType;
    }

    private PreparedStatementResult<PREPARED_STATEMENT> prepareStatement(String sql, GenericStatementExecutionOptions statementParameters) throws GenericProtocolException {

        checkState(s -> s.canPrepareStatements);

        bufferAnyOngoingResultSet();

        final PreparedStatementResult<PREPARED_STATEMENT> preparedStatement = executeWithTemporaryState(State.PREPARING_STATEMENT, () -> {

            final PreparedStatementResult<PREPARED_STATEMENT> result;

            try {
                result = databaseProtocol.prepareStatement(dataOutput, dataInput, sql, statementParameters);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return result;
        });

        return preparedStatement;
    }

    private GenericResultSet executePreparedQuery(PREPARED_STATEMENT preparedQuery, GenericPreparedStatementParameters parameters) throws GenericProtocolException {

        Objects.requireNonNull(preparedQuery);
        Objects.requireNonNull(parameters);

        checkState(s -> s.canExecutePreparedQuery);

        bufferAnyOngoingResultSet();

        beginTransactionIfNotStarted();

        executeWithTemporaryStateAndSetNextState(State.EXECUTING_PREPARED_QUERY, State.EXECUTED_PREPARED_QUERY, () -> {

            try {
                databaseProtocol.executePreparedQuery(preparedQuery, dataOutput, dataInput, parameters, stringEncoder);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return null;
        });

        return retrieveResultMetaDataAndCreateResultSet(preparedQuery);
    }

    private int executePreparedUpdate(PREPARED_STATEMENT preparedStatement, GenericPreparedStatementParameters preparedStatementParameters) throws GenericProtocolException {

        Objects.requireNonNull(preparedStatement);
        Objects.requireNonNull(preparedStatementParameters);

        checkState(s -> s.canExecutePreparedUpdate);

        beginTransactionIfNotStarted();

        final int updateCount = executeWithTemporaryStateAndSetNextState(State.EXECUTING_PREPARED_UPDATE, State.EXECUTED_PREPARED_UPDATE, () -> {

            final int result;

            try {
                result = databaseProtocol.executePreparedUpdate(preparedStatement, dataOutput, dataInput, preparedStatementParameters, stringEncoder);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return result;
        });

        return updateCount;
    }

    private int[] executeBatches(PREPARED_STATEMENT preparedStatement, Collection<? extends IGenericPreparedStatementParameterGetters> batches) throws GenericProtocolException {

        Objects.requireNonNull(preparedStatement);

        checkState(s -> s.canExecuteBatch);

        bufferAnyOngoingResultSet();

        beginTransactionIfNotStarted();

        final int[] updateCounts = executeWithTemporaryStateAndSetNextState(State.EXECUTING_BATCHES, State.EXECUTED_BATCHES, () -> {

            final int[] result;

            try {
                result = databaseProtocol.executeBatches(preparedStatement, batches, dataOutput, dataInput, stringEncoder);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return result;
        });

        return updateCounts;
    }

    private void closePreparedStatement(PREPARED_STATEMENT preparedStatement) throws GenericProtocolException {

        Objects.requireNonNull(preparedStatement);

        checkState(s -> s.canClosePreparedStatement);

        bufferAnyOngoingResultSet();

        try {
            databaseProtocol.closePreparedStatement(preparedStatement, dataOutput, dataInput);
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }
    }

    private void bufferAnyOngoingResultSet() throws GenericProtocolException {

        final BaseSwappableResultSet ongoingResultSet = protocolData.findDirectConnectionResultSet();

        if (ongoingResultSet != null) {

            @SuppressWarnings("unchecked")
            final ConnectionDirectResultSet<PREPARED_STATEMENT, DATA_TYPE> columnGenericResultSet
                = (ConnectionDirectResultSet<PREPARED_STATEMENT, DATA_TYPE>)ongoingResultSet.getDelegate();

            final BufferedResultSet<PREPARED_STATEMENT, DATA_TYPE> bufferedResultSet = columnGenericResultSet.readRemainingToBuffer();

            ongoingResultSet.swap(bufferedResultSet);
        }
    }

    private void beginTransactionIfNotStarted() throws GenericProtocolException {

        if (!withinTransaction) {

            try {
                databaseProtocol.sendBegin(dataOutput, dataInput);

                this.withinTransaction = true;
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }
        }
    }

    private void commit() throws GenericProtocolException, IOException {

        databaseProtocol.sendCommit(dataOutput, dataInput);

        this.withinTransaction = false;
    }

    private GenericResultSet retrieveResultMetaDataAndCreateResultSet() throws GenericProtocolException {

        checkState(State.EXECUTED_QUERY_RETRIEVE_RESULT);

        final GenericResultSetMetaData<DATA_TYPE> resultSetMetaData = executeWithTemporaryStateAndSetNextState(State.RETRIEVING_RESULT_SET_META_DATA, State.RETRIEVING_RESULT_SET,
                () -> {

            final GenericResultSetMetaData<DATA_TYPE> result;

            try {
                result = databaseProtocol.retrieveResultMetaData(dataInput);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return result;
        });

        return addResultSet(null, resultSetMetaData);
    }

    private GenericResultSet retrieveResultMetaDataAndCreateResultSet(PREPARED_STATEMENT preparedQuery) throws GenericProtocolException {

        checkState(State.EXECUTED_PREPARED_QUERY);

        final GenericResultSetMetaData<DATA_TYPE> resultSetMetaData = executeWithTemporaryStateAndSetNextState(State.RETRIEVING_RESULT_SET_META_DATA, State.RETRIEVING_RESULT_SET,
                () -> {

            final GenericResultSetMetaData<DATA_TYPE> result;

            try {
                result = databaseProtocol.retrievePreparedResultMetaData(dataInput, preparedQuery);
            }
            catch (IOException ex) {

                throw new ProtocolIOException(ex);
            }

            return result;
        });

        return addResultSet(preparedQuery, resultSetMetaData);
    }

    private BaseSwappableResultSet addResultSet(PREPARED_STATEMENT preparedQuery, GenericResultSetMetaData<DATA_TYPE> resultSetMetaData) {

        final ResultRowDecoder<DATA_TYPE> resultRowDecoder = databaseProtocol.getResultRowDecoder();

        final RowDataResultSet rowDataResultSet = new ConnectionDirectResultSet<>(preparedQuery, connectionProtocol, resultSetMetaData, resultRowDecoder,
                CACHED_RESULT_SET_CHUNK_SIZE);

        final BaseSwappableResultSet swappableResultSet = swappableResultSetFactory.createSwappableResultSet(connectionProtocol, rowDataResultSet);

        protocolData.addResultSet(preparedQuery, swappableResultSet);

        return swappableResultSet;
    }

    private void retrieveResultRow(byte[] dst, int maxRowsToRetrieve, int maxBytesPerRow, IRetrievedRows retrievedRows) throws GenericProtocolException {

        checkState(State.RETRIEVING_RESULT_SET);

        try {
            databaseProtocol.retrieveResultRows(dataInput, dst, maxRowsToRetrieve, maxBytesPerRow, retrievedRows);
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }
    }

    private void closeResultSet(BaseSwappableResultSet resultSet) throws GenericProtocolException {

        Objects.requireNonNull(resultSet);

        checkState(State.RETRIEVING_RESULT_SET);

        try {
            databaseProtocol.closeResultSet(dataOutput, dataInput);
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }
        finally {

            @SuppressWarnings("unchecked")
            final ColumnGenericResultSet<PREPARED_STATEMENT, DATA_TYPE> columnGenericResultSet = (ColumnGenericResultSet<PREPARED_STATEMENT, DATA_TYPE>)resultSet.getDelegate();

            protocolData.removeResultSet(columnGenericResultSet.getPreparedStatement(), resultSet);
        }

        setState(State.IDLE);
    }

    private void sendClose() throws GenericProtocolException {

        try {
            databaseProtocol.sendClose(dataOutput, dataInput);
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }
    }
}
