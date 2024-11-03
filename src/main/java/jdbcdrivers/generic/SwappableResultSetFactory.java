package jdbcdrivers.generic;

interface SwappableResultSetFactory {

    BaseSwappableResultSet createSwappableResultSet(ResultSetClosing resultSetClosing, RowDataResultSet delegate);
}
