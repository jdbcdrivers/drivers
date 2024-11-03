package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.generic.api.IGenericDriver;

public final class NonSynchronizedGenericDriverSynchronizerFactory extends GenericDriverSynchronizerFactory {

    public static final NonSynchronizedGenericDriverSynchronizerFactory INSTANCE = new NonSynchronizedGenericDriverSynchronizerFactory();

    private NonSynchronizedGenericDriverSynchronizerFactory() {

    }

    @Override
    public IGenericDriver synchronizedDriver(IGenericDriver genericDriver) {

        Objects.requireNonNull(genericDriver);

        return genericDriver;
    }

    @Override
    <PREPARED_STATEMENT, DATA_TYPE> IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> synchronizedConnectionProtocol(
            IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> connectionProtocol) {

        Objects.requireNonNull(connectionProtocol);

        return connectionProtocol;
    }

    @Override
    protected SwappableResultSetFactory getSwappableResultSetFactory() {

        return NonSynchronizedSwappableResultSetFactory.INSTANCE;
    }
}
