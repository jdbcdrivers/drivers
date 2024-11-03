package jdbcdrivers.generic.threadsafe;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Properties;

import jdbcdrivers.generic.api.IGenericDriver;
import jdbcdrivers.generic.api.IGenericDriverConnection;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

public final class SynchronizedGenericDriver implements IGenericDriver {

    private final IGenericDriver delegate;

    public SynchronizedGenericDriver(IGenericDriver delegate) {

        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public synchronized IGenericDriverConnection connect(URI uri, Properties info, Charset charset) throws GenericProtocolException {

        return new SynchronizedDriverConnection(delegate.connect(uri, info, charset));
    }
}
