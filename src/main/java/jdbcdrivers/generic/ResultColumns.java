package jdbcdrivers.generic;

import java.util.List;

/**
 * Base class for result columns information.
 *
 * @param <DATA_TYPE> database protocol datatype
 */
public abstract class ResultColumns<DATA_TYPE> {

    private final ResultColumn<DATA_TYPE>[] resultColumns;

    @SuppressWarnings("unchecked")
    public ResultColumns(List<? extends ResultColumn<DATA_TYPE>> resultColumns) {

        this.resultColumns = resultColumns.toArray(ResultColumn[]::new);
    }

    public int getNumColumns() {

        return resultColumns.length;
    }

    public ResultColumn<DATA_TYPE> getResultColumn(int index) {

        return resultColumns[index];
    }
}
