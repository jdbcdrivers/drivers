package jdbcdrivers.generic.api;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Properties;

import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Interface for generic driver.
 */
public interface IGenericDriver {

    /**
     * Open a connection to a database server.
     *
     * @param uri the {@link URI} to connect to
     * @param properties connection properties
     *
     * @return an {@link IGenericDriverConnection} for the opened connection
     *
     * @throws GenericProtocolException thrown if there was an issue with connecting
     */
    IGenericDriverConnection connect(URI uri, Properties properties, Charset charset) throws GenericProtocolException;
}
