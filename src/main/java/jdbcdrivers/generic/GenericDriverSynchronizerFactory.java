package jdbcdrivers.generic;

import jdbcdrivers.generic.api.IGenericDriver;

public abstract class GenericDriverSynchronizerFactory {

    public abstract IGenericDriver synchronizedDriver(IGenericDriver genericDriver);

    abstract <PREPARED_STATEMENT, DATA_TYPE> IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> synchronizedConnectionProtocol(
            IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> connectionProtocol);

    abstract SwappableResultSetFactory getSwappableResultSetFactory();
}
