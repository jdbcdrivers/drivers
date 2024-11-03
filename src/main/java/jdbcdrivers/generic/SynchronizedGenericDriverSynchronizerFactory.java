package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.generic.api.IGenericDriver;
import jdbcdrivers.generic.threadsafe.SynchronizedGenericDriver;

public final class SynchronizedGenericDriverSynchronizerFactory extends GenericDriverSynchronizerFactory {

    public static final SynchronizedGenericDriverSynchronizerFactory INSTANCE = new SynchronizedGenericDriverSynchronizerFactory();

    private SynchronizedGenericDriverSynchronizerFactory() {

    }

    @Override
    public IGenericDriver synchronizedDriver(IGenericDriver genericDriver) {

        return new SynchronizedGenericDriver(genericDriver);
    }

    @Override
    <PREPARED_STATEMENT, DATA_TYPE> IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> synchronizedConnectionProtocol(
            IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> connectionProtocol) {

        Objects.requireNonNull(connectionProtocol);

        return new SynchronizedConnectionProtocol<>(connectionProtocol);
    }

    @Override
    protected SwappableResultSetFactory getSwappableResultSetFactory() {

        return SynchronizedSwappableResultSetFactory.INSTANCE;
    }
}
