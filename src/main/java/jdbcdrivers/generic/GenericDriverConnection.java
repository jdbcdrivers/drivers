package jdbcdrivers.generic;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;

import jdbcdrivers.databaseprotocol.api.IDatabaseProtocol.PreparedStatementResult;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.api.IGenericDriverConnection;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.ConnectionClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ProtocolIOException;

final class GenericDriverConnection<PREPARED_STATEMENT, DATA_TYPE> extends GenericCloseable<ConnectionClosedException>
        implements IGenericDriverConnection, AutoCloseable {

    private static final long MAX_EVICT_DELTA_MILLIS = 60 * 60 * 1000;

    private final GenericDriver<PREPARED_STATEMENT, DATA_TYPE> driver;
/*
    private final URL url;
    private final Properties properties;
*/
    private final DriverSocket socket;
    private final IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> protocol;

    private PreparedStatementParametersPool preparedStatementParametersPool;
    private boolean autoCommit;

    GenericDriverConnection(GenericDriver<PREPARED_STATEMENT, DATA_TYPE> driver, URI uri, Properties properties, DriverSocket socket,
            IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> protocol) {
        super(ConnectionClosedException::new);

        this.driver = Objects.requireNonNull(driver);
/*
        this.url = Objects.requireNonNull(url);
        this.properties = properties;
*/
        this.socket = Objects.requireNonNull(socket);
        this.protocol = Objects.requireNonNull(protocol);

        this.preparedStatementParametersPool = null;

        this.autoCommit = true;
    }

    @Override
    public void close() throws AlreadyClosedException, GenericProtocolException {

        try {
            protocol.sendClose();

            socket.close();
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }
        finally {

            try {
                driver.removeConnection(this);
            }
            finally {

                closeGeneric();
            }
        }
    }

    @Override
    public void setAutoCommit(boolean on) throws GenericProtocolException {

        try {
            protocol.setAutoCommit(on);
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }

        this.autoCommit = on;
    }

    @Override
    public boolean getAutoCommit() throws GenericProtocolException {

        return autoCommit;
    }

    @Override
    public GenericStatement createStatement(GenericStatementExecutionOptions statementParameters) {

        return new GenericStatement(protocol, statementParameters);
    }

    @Override
    public GenericPreparedStatement<PREPARED_STATEMENT, DATA_TYPE> createPreparedStatement(String sql, GenericStatementExecutionOptions statementParameters)
            throws GenericProtocolException {

        Objects.requireNonNull(sql);
        Objects.requireNonNull(statementParameters);

        final PreparedStatementResult<PREPARED_STATEMENT> preparedStatementResult = protocol.prepareStatement(sql, statementParameters);

        return new GenericPreparedStatement<>(this, preparedStatementResult.getPreparedStatement(), preparedStatementResult.getNumParameters());
    }

    @Override
    public void commit() throws GenericProtocolException {

        try {
            protocol.commit();
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }
    }

    IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> getProtocol() {
        return protocol;
    }

    GenericPreparedStatementParameters allocateParameters(int numParameters) {

        if (numParameters < 1) {

            throw new IllegalArgumentException();
        }

        final GenericPreparedStatementParameters result;

        synchronized (this) {

            if (preparedStatementParametersPool == null) {

                this.preparedStatementParametersPool = new PreparedStatementParametersPool(MAX_EVICT_DELTA_MILLIS);
            }

            result = preparedStatementParametersPool.allocateParameters(numParameters);
        }

        return result;
    }

    GenericPreparedStatementParameters allocateParametersCopy(GenericPreparedStatementParameters toCopy) {

        Objects.requireNonNull(toCopy);

        return preparedStatementParametersPool.allocateCopy(toCopy);
    }

    void freeParameters(GenericPreparedStatementParameters preparedStatementParameters) {

        Objects.requireNonNull(preparedStatementParameters);

        preparedStatementParametersPool.freeParameters(preparedStatementParameters, MAX_EVICT_DELTA_MILLIS);
    }
}
