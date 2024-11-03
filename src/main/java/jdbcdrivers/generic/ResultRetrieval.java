package jdbcdrivers.generic;

import jdbcdrivers.generic.exceptions.GenericProtocolException;

/**
 * Interface for result rows retrieval and closing result sets.
 */
interface ResultRetrieval extends ResultSetClosing {

    void retrieveResultRows(byte[] dst, int maxRows, int numBytesPerRow, RetrievedRows retrievedRows) throws GenericProtocolException;
}
