package jdbcdrivers.generic.api;

/**
 * Result set metadata, describing information about a {@link IGenericResultSet}.
 */
public interface IGenericResultSetMetaData {

    /**
     * Get the number of columns per row.
     *
     * @return number of columns
     */
    int getNumColumns();

    /**
     * Get index of result row column by column name. Indices are counted from {@code 0}, column name comparison is case insensitive.
     *
     * @param columnName name of column to get index for
     *
     * @return index of column, or {@code null} if the result set does not contain a column by that name
     */
    Integer getColumnIndex(String columnName);

    /**
     * Get metadata for one column.
     *
     * @param index the index of column to get metadata for, counted from {@code 0}
     *
     * @return a {@link IResultSetColumn} through which metadata for the column can be retrieved
     */
    IResultSetColumn getResultSetColumn(int index);
}
