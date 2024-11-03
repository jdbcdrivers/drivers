package jdbcdrivers.databaseprotocol.api;

/**
 * Interface for adding rows information from database protocol
 */
public interface IRetrievedRows {

    /**
     * Add information about a retrieved row.
     *
     * @param numRowBytes the number of bytes in the row
     */
    void addRow(int numRowBytes);
}
