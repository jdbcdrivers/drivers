package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Base class for result set implementations that row data can be retrieved from, and e.g. not only delegate.
 */
abstract class RowDataResultSet extends GenericResultSet {

    private final ResultSetClosing resultSetClosing;

    RowDataResultSet(ResultSetClosing resultSetClosing) {

        this.resultSetClosing = Objects.requireNonNull(resultSetClosing);
    }

    @Override
    public void close() throws AlreadyClosedException, GenericProtocolException {

        checkNotAlreadyClosed();

        try {
//            resultSetClosing.closeResultSet(this);
        }
        finally {

            closeGeneric();
        }
    }
}
