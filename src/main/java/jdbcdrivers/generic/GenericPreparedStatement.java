package jdbcdrivers.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericPreparedStatement;
import jdbcdrivers.generic.api.IGenericPreparedStatementParameters;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.PreparedStatementClosedException;

final class GenericPreparedStatement<PREPARED_STATEMENT, DATA_TYPE> extends GenericCloseable<PreparedStatementClosedException>
        implements IGenericPreparedStatement, AutoCloseable {

    private final GenericDriverConnection<PREPARED_STATEMENT, DATA_TYPE> connection;
    private final PREPARED_STATEMENT preparedStatement;
    private final int numParameters;

    private final Collection<GenericPreparedStatementParameters> batches;

    GenericPreparedStatement(GenericDriverConnection<PREPARED_STATEMENT, DATA_TYPE> connection, PREPARED_STATEMENT preparedStatement, int numParameters) {
        super(PreparedStatementClosedException::new);

        Objects.requireNonNull(connection);
        Objects.requireNonNull(preparedStatement);

        if (numParameters < 0) {

            throw new IllegalArgumentException();
        }

        this.connection = connection;
        this.preparedStatement = preparedStatement;
        this.numParameters = numParameters;

        this.batches = new ArrayList<>();
    }

    @Override
    public void close() throws AlreadyClosedException, GenericProtocolException {

        try {
            connection.getProtocol().closePreparedStatement(preparedStatement);
        }
        finally {

            closeGeneric();
        }
    }

    @Override
    public IGenericPreparedStatementParameters allocateParameters() {

        return connection.allocateParameters(numParameters);
    }

    @Override
    public void freeParameters(IGenericPreparedStatementParameters parameters) {

        connection.freeParameters((GenericPreparedStatementParameters)parameters);
    }

    @Override
    public GenericResultSet exeuteQuery(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException {

        Objects.requireNonNull(parameters);

        checkNotClosed();

        return connection.getProtocol().executePreparedQuery(preparedStatement, (GenericPreparedStatementParameters)parameters);
    }

    @Override
    public int exeuteUpdate(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException {

        Objects.requireNonNull(parameters);

        checkNotClosed();

        return connection.getProtocol().executePreparedUpdate(preparedStatement, (GenericPreparedStatementParameters)parameters);
    }

    @Override
    public void addBatch(IGenericPreparedStatementParameters parameters) throws PreparedStatementClosedException, GenericProtocolException {

        Objects.requireNonNull(parameters);

        checkNotClosed();

        batches.add(connection.allocateParametersCopy((GenericPreparedStatementParameters)parameters));
    }

    @Override
    public int[] executeBatches() throws PreparedStatementClosedException, GenericProtocolException {

        checkNotClosed();

        final int[] updateCounts;

        try {
            updateCounts = connection.getProtocol().executeBatches(preparedStatement, batches);
        }
        finally {

            for (GenericPreparedStatementParameters preparedStatementParameters : batches) {

                connection.freeParameters(preparedStatementParameters);
            }

            batches.clear();
        }

        return updateCounts;
    }
}
