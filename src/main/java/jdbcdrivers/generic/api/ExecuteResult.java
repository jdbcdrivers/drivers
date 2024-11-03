package jdbcdrivers.generic.api;

import java.util.Objects;

/**
 * Result from SQL execution.
 */
public final class ExecuteResult {

    private static final int NO_UPDATE_COUNT = -1;

    /**
     * Type of result from SQL execution.
     */
    public enum ExecuteResultType {

        RESULT_SET,
        UPDATE_COUNT,
        NONE;
    }

    private final ExecuteResultType resultType;
    private final IGenericResultSet resultSet;
    private final int updateCount;

    public ExecuteResult() {

        this.resultType = ExecuteResultType.NONE;
        this.resultSet = null;
        this.updateCount = NO_UPDATE_COUNT;
    }

    public ExecuteResult(IGenericResultSet resultSet) {

        this.resultType = ExecuteResultType.RESULT_SET;
        this.resultSet = Objects.requireNonNull(resultSet);
        this.updateCount = NO_UPDATE_COUNT;
    }

    public ExecuteResult(int updateCount) {

        if (updateCount < 0) {

            throw new IllegalArgumentException();
        }

        this.resultType = ExecuteResultType.UPDATE_COUNT;
        this.resultSet = null;
        this.updateCount = updateCount;
    }

    public ExecuteResultType getResultType() {
        return resultType;
    }

    public IGenericResultSet getResultSet() {
        return resultSet;
    }

    public int getUpdateCount() {
        return updateCount;
    }
}
