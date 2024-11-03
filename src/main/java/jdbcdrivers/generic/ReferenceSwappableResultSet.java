package jdbcdrivers.generic;

import java.util.Objects;

/**
 * Non-threadsafe implementation for swapping out result sets, e.g. from direct connection to buffered.
 */
final class ReferenceSwappableResultSet extends BaseSwappableResultSet {

    private RowDataResultSet delegate;

    ReferenceSwappableResultSet(ResultSetClosing resultSetClosing, RowDataResultSet delegate) {
        super(resultSetClosing);

        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    RowDataResultSet getDelegate() {

        return delegate;
    }

    @Override
    void swap(RowDataResultSet newResultSet) {

        Objects.requireNonNull(newResultSet);

        if (newResultSet == delegate) {

            throw new IllegalArgumentException();
        }

        this.delegate = newResultSet;
    }
}
