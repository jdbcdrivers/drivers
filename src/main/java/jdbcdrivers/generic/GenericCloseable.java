package jdbcdrivers.generic;

import java.util.Objects;
import java.util.function.Supplier;

import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericDriverException;

/**
 * Base class for entities that can be closed.
 */
abstract class GenericCloseable<CHECK_CLOSED_EXCEPTION extends GenericDriverException> {

    private final Supplier<CHECK_CLOSED_EXCEPTION> checkClosedExceptionSupplier;

    private boolean closed;

    GenericCloseable(Supplier<CHECK_CLOSED_EXCEPTION> checkClosedExceptionSupplier) {

        this.checkClosedExceptionSupplier = Objects.requireNonNull(checkClosedExceptionSupplier);

        this.closed = false;
    }

    final void closeGeneric() throws AlreadyClosedException {

        checkNotAlreadyClosed();

        this.closed = true;
    }

    final void checkNotAlreadyClosed() throws AlreadyClosedException {

        if (closed) {

            throw new AlreadyClosedException();
        }
    }

    final void checkNotClosed() throws CHECK_CLOSED_EXCEPTION {

        if (closed) {

            throw checkClosedExceptionSupplier.get();
        }
    }
}
