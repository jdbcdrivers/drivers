package jdbcdrivers.generic;

final class NonSynchronizedSwappableResultSetFactory implements SwappableResultSetFactory {

    static final NonSynchronizedSwappableResultSetFactory INSTANCE = new NonSynchronizedSwappableResultSetFactory();

    private NonSynchronizedSwappableResultSetFactory() {

    }

    @Override
    public BaseSwappableResultSet createSwappableResultSet(ResultSetClosing resultSetClosing, RowDataResultSet delegate) {

        return new ReferenceSwappableResultSet(resultSetClosing, delegate);
    }
}
