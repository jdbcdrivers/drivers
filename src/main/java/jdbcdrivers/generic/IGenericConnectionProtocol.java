package jdbcdrivers.generic;

interface IGenericConnectionProtocol<PREPARED_STATEMENT, DATA_TYPE> extends SQLExecutor, ResultRetrieval, ConnectionProtocolOperations<PREPARED_STATEMENT, DATA_TYPE> {

}
