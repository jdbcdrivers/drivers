package jdbcdrivers.databaseprotocol.vendor.informix;

import java.nio.charset.Charset;

import jdbcdrivers.databaseprotocol.api.IDatabaseDriver;
import jdbcdrivers.databaseprotocol.api.IDatabaseProtocol;

/**
 * Informix JDBC independent driver implementation, so that the code is independent from JDBC API.
 */
final class InformixDatabaseDriver implements IDatabaseDriver<InformixPreparedStatement, InformixDataType> {

    static final InformixDatabaseDriver INSTANCE = new InformixDatabaseDriver();

    private InformixDatabaseDriver() {

    }

    @Override
    public int getMaxStringBytes() {

        return InformixProtocolConstants.MAX_STRING_BYTES;
    }

    @Override
    public IDatabaseProtocol<InformixPreparedStatement, InformixDataType> createDatabaseProtocol(Charset charset) {

        return new InformixDatabaseProtocol(charset);
    }
}
