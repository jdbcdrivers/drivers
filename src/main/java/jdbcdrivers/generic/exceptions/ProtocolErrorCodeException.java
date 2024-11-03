package jdbcdrivers.generic.exceptions;

/**
 * Exception thrown for database error codes and messages received from the database.
 */
public final class ProtocolErrorCodeException extends GenericProtocolException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public ProtocolErrorCodeException(int code) {

        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public <T, R> R visitProtocolException(GenericProtocolExceptionVisitor<T, R> visitor, T parameter) {

        return visitor.onErrorCode(this, parameter);
    }
}
