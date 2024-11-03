package jdbcdrivers.generic;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

import jdbcdrivers.databaseprotocol.api.IDatabaseDriver;
import jdbcdrivers.generic.api.IGenericDriver;
import jdbcdrivers.generic.api.IGenericDriverConnection;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ProtocolIOException;
import jdbcdrivers.generic.util.StringEncoder;

/**
 * Generic driver implementation, keeps main logic separate from JDBC API.
 */
public final class GenericDriver<PREPARED_STATEMENT, DATA_TYPE> implements IGenericDriver {

    private final IDatabaseDriver<PREPARED_STATEMENT, DATA_TYPE> databaseDriver;
    private final GenericDriverSynchronizerFactory genericDriverSynchronizerFactory;

    private final Collection<GenericDriverConnection<PREPARED_STATEMENT, DATA_TYPE>> connections;

    public GenericDriver(IDatabaseDriver<PREPARED_STATEMENT, DATA_TYPE> databaseDriver, GenericDriverSynchronizerFactory genericDriverSynchronizerFactory) {

        Objects.requireNonNull(databaseDriver);
        Objects.requireNonNull(genericDriverSynchronizerFactory);

        this.databaseDriver = Objects.requireNonNull(databaseDriver);
        this.genericDriverSynchronizerFactory = Objects.requireNonNull(genericDriverSynchronizerFactory);

        this.connections = new ArrayList<>();
    }

    @Override
    public final IGenericDriverConnection connect(URI uri, Properties info, Charset charset) throws GenericProtocolException {

        final int mb = 1024 * 1024;

        final int sendBufferSize = 10 * mb;
        final int receiveBufferSize = 10 * mb;

        final GenericDriverConnection<PREPARED_STATEMENT, DATA_TYPE> connection;

        try {
            final DriverSocket socket = new DriverSocket(sendBufferSize, receiveBufferSize);

            socket.connect(uri.getHost(), uri.getPort());

            connection = new GenericDriverConnection<>(this, uri, info, socket, createConnectionProtocol(socket, charset));

            connection.getProtocol().performInitialSetup(uri, info);
        }
        catch (IOException ex) {

            throw new ProtocolIOException(ex);
        }

        addConnection(connection);

        return connection;
    }

    private IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> createConnectionProtocol(DriverSocket socket, Charset charset) {

        final StringEncoder stringEncoder = new StringEncoder(charset, databaseDriver.getMaxStringBytes());

        final GenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> protocol = new GenericConnectionProtocol<>(socket, databaseDriver.createDatabaseProtocol(charset),
                stringEncoder, genericDriverSynchronizerFactory);

        return protocol.getConnectionProtocolInterface();
    }

    private void addConnection(GenericDriverConnection<PREPARED_STATEMENT, DATA_TYPE> connection) {

        Objects.requireNonNull(connection);

        if (connections.contains(connection)) {

            throw new IllegalStateException();
        }

        connections.add(connection);
    }

    final void removeConnection(GenericDriverConnection<PREPARED_STATEMENT, DATA_TYPE> connection) {

        Objects.requireNonNull(connection);

        if (!connections.contains(connection)) {

            throw new IllegalStateException();
        }

        connections.remove(connection);
    }
}
