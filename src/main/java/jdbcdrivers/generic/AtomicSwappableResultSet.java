package jdbcdrivers.generic;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Threadsafe implementation for swapping out result sets, e.g. from direct connection to buffered.
 */
final class AtomicSwappableResultSet extends BaseSwappableResultSet {

    private final AtomicReference<RowDataResultSet> delegate;

    AtomicSwappableResultSet(ResultSetClosing resultSetClosing, RowDataResultSet delegate) {
        super(resultSetClosing);

        Objects.requireNonNull(delegate);

        this.delegate = new AtomicReference<>(delegate);
    }

    @Override
    RowDataResultSet getDelegate() {

        return delegate.get();
    }

    @Override
    void swap(RowDataResultSet newResultSet) {

        Objects.requireNonNull(newResultSet);

        if (newResultSet == delegate.get()) {

            throw new IllegalArgumentException();
        }

        delegate.set(newResultSet);
    }
}
