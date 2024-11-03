package jdbcdrivers.databaseprotocol.vendor.informix;

import java.nio.charset.Charset;

import jdbcdrivers.jdbc.JDBCDriver;

/**
 * Informix JDBC driver main class, extends generic JDBC driver.
 */
public final class InformixJDBCDriver extends JDBCDriver<InformixPreparedStatement, InformixDataType> {

    public InformixJDBCDriver() {
        super(InformixDatabaseDriver.INSTANCE);
    }

    @Override
    public int getMajorVersion() {

        return 0;
    }

    @Override
    public int getMinorVersion() {

        return 1;
    }

    @Override
    public boolean jdbcCompliant() {

        return true;
    }

    @Override
    protected String getURLPrefix() {

        return "jdbc:informix-sqli://";
    }

    @Override
    protected Charset getCharset() {

        return Charset.defaultCharset();
    }
}
