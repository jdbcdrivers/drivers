package jdbcdrivers.generic;

import jdbcdrivers.generic.api.IGenericResultSet;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;

/**
 * Base class for result sets.
 */
abstract class GenericResultSet extends GenericCloseable<ResultSetClosedException> implements IGenericResultSet {

    GenericResultSet() {
        super(ResultSetClosedException::new);
    }
}
