package jdbcdrivers.jdbc;

import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.function.Supplier;

import jdbcdrivers.generic.StateObject;

/**
 * Base class for JDBC entities extending {@link Wrapper}.
 *
 * @param <STATE> state machine state type
 * @param <CHECK_STATE_EXCEPTION> type of exception thrown for state machine errors
 */
public abstract class JDBCWrapper<STATE, CHECK_STATE_EXCEPTION extends Exception> extends StateObject<STATE, CHECK_STATE_EXCEPTION> implements Wrapper {

    JDBCWrapper() {

    }

    JDBCWrapper(STATE initialState, STATE closeState, Supplier<CHECK_STATE_EXCEPTION> checkStateExceptionSupplier) {
        super(initialState, closeState, checkStateExceptionSupplier);
    }

    @Override
    public final <T> T unwrap(Class<T> iface) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isWrapperFor(Class<?> iface) throws SQLException {

        throw new UnsupportedOperationException();
    }
}
