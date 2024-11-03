package jdbcdrivers.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import jdbcdrivers.util.DriverUtil;

final class ProtocolData<PREPARED_STATEMENT> {

    private final Function<PREPARED_STATEMENT, Object> preparedStatementIdentifierGetter;

    private final Map<StatementKey, StatementData> statements;
    private final Map<PreparedStatementKey, PreparedStatementData<PREPARED_STATEMENT>> preparedStatements;

    ProtocolData(Function<PREPARED_STATEMENT, Object> preparedStatementIdentifierGetter) {

        this.preparedStatementIdentifierGetter = Objects.requireNonNull(preparedStatementIdentifierGetter);

        this.statements = new HashMap<>();
        this.preparedStatements = new HashMap<>();
    }

    void addPreparedStatement(PREPARED_STATEMENT preparedStatement) {

        Objects.requireNonNull(preparedStatement);

        final Object identifier = preparedStatementIdentifierGetter.apply(preparedStatement);

        preparedStatements.put(new PreparedStatementKey(identifier), new PreparedStatementData<>(preparedStatement));
    }

    void addResultSet(PREPARED_STATEMENT preparedStatement, BaseSwappableResultSet resultSet) {

        Objects.requireNonNull(preparedStatement);
        Objects.requireNonNull(resultSet);

        final Object identifier = preparedStatementIdentifierGetter.apply(preparedStatement);

        final PreparedStatementData<PREPARED_STATEMENT> preparedStatementData = preparedStatements.get(new PreparedStatementKey(identifier));

        if (preparedStatementData == null) {

            throw new IllegalStateException();
        }

        preparedStatementData.addResultSet(resultSet);
    }

    void removeResultSet(PREPARED_STATEMENT preparedStatement, BaseSwappableResultSet resultSet) {

        Objects.requireNonNull(preparedStatement);
        Objects.requireNonNull(resultSet);

        final Object identifier = preparedStatementIdentifierGetter.apply(preparedStatement);

        final PreparedStatementData<PREPARED_STATEMENT> preparedStatementData = preparedStatements.get(new PreparedStatementKey(identifier));

        if (preparedStatementData == null) {

            throw new IllegalStateException();
        }

        preparedStatementData.removeResultSet(resultSet);
    }

    final BaseSwappableResultSet findDirectConnectionResultSet() {

        final BaseSwappableResultSet statementSwappableResultSet = findDirectConnectionResultSet(statements);
        final BaseSwappableResultSet preparedStatementSwappableResultSet = findDirectConnectionResultSet(preparedStatements);

        final BaseSwappableResultSet found;

        if (statementSwappableResultSet != null && preparedStatementSwappableResultSet != null) {

            throw new IllegalStateException();
        }
        else if (statementSwappableResultSet != null) {

            found = statementSwappableResultSet;
        }
        else if (preparedStatementSwappableResultSet != null) {

            found = preparedStatementSwappableResultSet;
        }
        else {
            found = null;
        }

        return found;
    }

    private static BaseSwappableResultSet findDirectConnectionResultSet(Map<?, ? extends BaseStatementData> map) {

        BaseSwappableResultSet found = null;

        for (BaseStatementData baseStatementData : map.values()) {

            final BaseSwappableResultSet swappableResultSet = baseStatementData.findDirectConnectionResultSet();

            if (swappableResultSet != null) {

                if (found != null) {

                    throw new IllegalStateException();
                }

                found = swappableResultSet;
            }
        }

        return found;
    }

    private static abstract class BaseStatementData {

        private final List<BaseSwappableResultSet> resultSets;

        BaseStatementData() {

            this.resultSets = new ArrayList<>();
        }

        final void addResultSet(BaseSwappableResultSet resultSet) {

            Objects.requireNonNull(resultSet);

            if (resultSets.contains(resultSet)) {

                throw new IllegalStateException();
            }

            resultSets.add(resultSet);
        }

        final void removeResultSet(BaseSwappableResultSet resultSet) {

            Objects.requireNonNull(resultSet);

            if (!resultSets.contains(resultSet)) {

                throw new IllegalStateException();
            }

            resultSets.remove(resultSet);
        }

        final BaseSwappableResultSet findDirectConnectionResultSet() {

            return DriverUtil.findAtMostOne(resultSets, r -> r.getDelegate() instanceof ConnectionDirectResultSet<?, ?>);
        }
    }

    private static final class StatementData extends BaseStatementData {

    }

    private static final class PreparedStatementData<PREPARED_STATEMENT> extends BaseStatementData {

        private final PREPARED_STATEMENT preparedStatement;

        PreparedStatementData(PREPARED_STATEMENT preparedStatement) {

            this.preparedStatement = Objects.requireNonNull(preparedStatement);
        }
    }

    private static abstract class BaseStatementKey {

        private final Object identifier;

        BaseStatementKey(Object identifier) {

            this.identifier = Objects.requireNonNull(identifier);
        }

        @Override
        public final int hashCode() {

            return identifier.hashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            BaseStatementKey other = (BaseStatementKey) obj;
            return Objects.equals(identifier, other.identifier);
        }
    }

    private static final class StatementKey extends BaseStatementKey {

        public StatementKey(Object identifier) {
            super(identifier);
        }
    }

    private static final class PreparedStatementKey extends BaseStatementKey {

        public PreparedStatementKey(Object identifier) {
            super(identifier);
        }
    }
}
