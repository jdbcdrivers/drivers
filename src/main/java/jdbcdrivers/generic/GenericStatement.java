package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.generic.api.ExecuteResult;
import jdbcdrivers.generic.api.GenericStatementExecutionOptions;
import jdbcdrivers.generic.api.IGenericStatement;
import jdbcdrivers.generic.exceptions.GenericProtocolException;

final class GenericStatement implements IGenericStatement {

    private final SQLExecutor sqlExecutor;
    private final GenericStatementExecutionOptions statementExecutionOptions;

    GenericStatement(SQLExecutor sqlExecutor, GenericStatementExecutionOptions statementParameters) {

        this.sqlExecutor = Objects.requireNonNull(sqlExecutor);
        this.statementExecutionOptions = Objects.requireNonNull(statementParameters);
    }

    @Override
    public GenericResultSet executeQuery(String sql) throws GenericProtocolException {

        return sqlExecutor.executeQuery(sql, statementExecutionOptions);
    }

    @Override
    public int executeUpdate(String sql) throws GenericProtocolException {

        return sqlExecutor.executeUpdate(sql, statementExecutionOptions);
    }

    @Override
    public ExecuteResult execute(String sql) throws GenericProtocolException {

        return sqlExecutor.execute(sql, statementExecutionOptions);
    }
}
