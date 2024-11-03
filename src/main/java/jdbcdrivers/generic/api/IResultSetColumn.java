package jdbcdrivers.generic.api;

/**
 * Interface for accessing metadata for a result set column.
 */
public interface IResultSetColumn {

    /**
     * Get the column name.
     *
     * @return column name
     */
    String getName();

    /**
     * Get the column label.
     *
     * @return column label
     */
    String getLabel();
}
