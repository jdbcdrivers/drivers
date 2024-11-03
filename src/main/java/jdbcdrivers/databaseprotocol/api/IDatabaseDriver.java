package jdbcdrivers.databaseprotocol.api;

import java.nio.charset.Charset;

/**
 * Interface for database driver implementation.
 *
 * @param <PREPARED_STATEMENT> database protocol prepared statement type
 * @param <DATA_TYPE> database protocol datatype
 */
public interface IDatabaseDriver<PREPARED_STATEMENT, DATA_TYPE> {

    /**
     * Get the maximum number of bytes for strings sent from the database.
     *
     * @return maximum length of strings in bytes
     */
    int getMaxStringBytes();

    /**
     * Create database protocol instance. One will be created per connection.
     *
     * @param charset preferred protocol {@link Charset}
     *
     * @return database protocol implementation
     */
    IDatabaseProtocol<PREPARED_STATEMENT, DATA_TYPE> createDatabaseProtocol(Charset charset);
}
