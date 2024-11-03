package jdbcdrivers.generic;

final class SynchronizedSwappableResultSetFactory implements SwappableResultSetFactory {

    static final SynchronizedSwappableResultSetFactory INSTANCE = new SynchronizedSwappableResultSetFactory();

    private SynchronizedSwappableResultSetFactory() {

    }

    @Override
    public BaseSwappableResultSet createSwappableResultSet(ResultSetClosing resultSetClosing, RowDataResultSet delegate) {

        return new AtomicSwappableResultSet(resultSetClosing, delegate);
    }
}
