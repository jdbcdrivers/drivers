package jdbcdrivers.jdbc;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import jdbcdrivers.databaseprotocol.api.IDatabaseDriver;
import jdbcdrivers.generic.GenericDriver;
import jdbcdrivers.generic.GenericDriverSynchronizerFactory;
import jdbcdrivers.generic.NonSynchronizedGenericDriverSynchronizerFactory;
import jdbcdrivers.generic.SynchronizedGenericDriverSynchronizerFactory;
import jdbcdrivers.generic.api.IGenericDriver;
import jdbcdrivers.generic.api.IGenericDriverConnection;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Base class for JDBC driver implementations. Implements the JDBC {@link Driver} API.
 */
public abstract class JDBCDriver<PREPARED_STATEMENT, DATA_TYPE> extends BaseJDBC<Void, RuntimeException> implements Driver {

    private static final boolean DEBUG = Boolean.FALSE;

    protected abstract String getURLPrefix();

    protected abstract Charset getCharset();

    private final IGenericDriver genericDriver;

    protected JDBCDriver(IDatabaseDriver<PREPARED_STATEMENT, DATA_TYPE> databaseDriver) {
        this(databaseDriver, true);
    }

    private JDBCDriver(IDatabaseDriver<PREPARED_STATEMENT, DATA_TYPE> databaseDriver, boolean threadsafe) {
        this(databaseDriver, threadsafe ? SynchronizedGenericDriverSynchronizerFactory.INSTANCE : NonSynchronizedGenericDriverSynchronizerFactory.INSTANCE);
    }

    private JDBCDriver(IDatabaseDriver<PREPARED_STATEMENT, DATA_TYPE> databaseDriver, GenericDriverSynchronizerFactory genericDriverSynchronizerFactory) {

        Objects.requireNonNull(databaseDriver);
        Objects.requireNonNull(genericDriverSynchronizerFactory);

        this.genericDriver = genericDriverSynchronizerFactory.synchronizedDriver(new GenericDriver<>(databaseDriver, genericDriverSynchronizerFactory));
    }

    @Override
    public final Connection connect(String url, Properties info) throws SQLException {

        final String prefix = "jdbc:";

        if (!url.startsWith(prefix)) {

            throw new IllegalArgumentException();
        }

        final URI parsedURI;

        try {
            parsedURI = new URI(url.substring(prefix.length()));
        }
        catch (URISyntaxException ex) {

            throw convert(ex);
        }

        if (DEBUG) {

            System.out.println("uri " + parsedURI + ' ' + parsedURI.getHost() + ' ' + parsedURI.getPort() + ' ' + parsedURI.getPath());
        }

        final IGenericDriverConnection genericDriverConnection;

        try {
            genericDriverConnection = genericDriver.connect(parsedURI, info, getCharset());
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }

        return new JDBCConnection(genericDriverConnection);
    }

    @Override
    public final boolean acceptsURL(String url) throws SQLException {

        return url.startsWith(getURLPrefix());
    }

    @Override
    public final DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final Logger getParentLogger() throws SQLFeatureNotSupportedException {

        throw new UnsupportedOperationException();
    }
}
