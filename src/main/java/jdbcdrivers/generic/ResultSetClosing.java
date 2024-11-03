package jdbcdrivers.generic;

import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Interface for closing of result sets.
 */
interface ResultSetClosing {

    void closeResultSet(BaseSwappableResultSet resultSet) throws GenericProtocolException;
}
