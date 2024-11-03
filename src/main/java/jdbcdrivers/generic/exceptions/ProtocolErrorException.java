package jdbcdrivers.generic.exceptions;

/**
 * Exception thrown for errors with regards to protocol communication.
 */
public final class ProtocolErrorException extends GenericProtocolException {

    private static final long serialVersionUID = 1L;

    @Override
    public <T, R> R visitProtocolException(GenericProtocolExceptionVisitor<T, R> visitor, T parameter) {

        return visitor.onError(this, parameter);
    }
}
