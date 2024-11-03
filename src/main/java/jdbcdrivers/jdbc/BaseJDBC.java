package jdbcdrivers.jdbc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.function.Supplier;

import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.GenericProtocolExceptionVisitor;
import jdbcdrivers.generic.exceptions.PreparedStatementClosedException;
import jdbcdrivers.generic.exceptions.ProtocolErrorCodeException;
import jdbcdrivers.generic.exceptions.ProtocolErrorException;
import jdbcdrivers.generic.exceptions.ProtocolIOException;
import jdbcdrivers.generic.exceptions.ProtocolStateException;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;
import jdbcdrivers.generic.exceptions.WrongColumnTypeException;

/**
 * Base class for JDBC entities.
 */
abstract class BaseJDBC<STATE, CHECK_STATE_EXCEPTION extends Exception> extends JDBCWrapper<STATE, CHECK_STATE_EXCEPTION> {

    BaseJDBC() {

    }

    BaseJDBC(STATE initialState, STATE closeState, Supplier<CHECK_STATE_EXCEPTION> checkStateExceptionSupplier) {
        super(initialState, closeState, checkStateExceptionSupplier);
    }

    static SQLException convert(WrongColumnTypeException ex) {

        return new SQLException(ex);
    }

    static SQLException convert(AlreadyClosedException ex) {

        return new SQLException(ex);
    }

    static SQLException convert(PreparedStatementClosedException ex) {

        return new SQLException(ex);
    }

    static SQLException convert(ResultSetClosedException ex) {

        return new SQLException(ex);
    }

    static SQLException convert(GenericProtocolException ex) {

        return ex.visitProtocolException(genericProtocolExceptionVisitor, null);
    }

    private static SQLException convert(ProtocolStateException ex) {

        return new SQLException(ex);
    }

    private static SQLException convert(ProtocolErrorException ex) {

        return new SQLException(ex);
    }

    private static SQLException convert(ProtocolErrorCodeException ex) {

        return new SQLException(null, null, ex.getCode(), ex);
    }

    private static SQLException convert(ProtocolIOException ex) {

        return convert(ex.getIOException());
    }

    static SQLException convert(IOException ex) {

        return new SQLException(ex);
    }

    static SQLException convert(URISyntaxException ex) {

        return new SQLException(ex);
    }

    private static final GenericProtocolExceptionVisitor<Void, SQLException> genericProtocolExceptionVisitor = new GenericProtocolExceptionVisitor<Void, SQLException>() {

        @Override
        public SQLException onState(ProtocolStateException ex, Void parameter) {

            return convert(ex);
        }

        @Override
        public SQLException onError(ProtocolErrorException ex, Void parameter) {

            return convert(ex);
        }

        @Override
        public SQLException onErrorCode(ProtocolErrorCodeException ex, Void parameter) {

            return convert(ex);
        }

        @Override
        public SQLException onIO(ProtocolIOException ex, Void parameter) {

            return convert(ex);
        }
    };
}
