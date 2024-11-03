package jdbcdrivers.generic.exceptions;

/**
 * Base class for exceptions thrown by the protocol implementation.
 */
public abstract class GenericProtocolException extends GenericDriverException {

    private static final long serialVersionUID = 1L;

    GenericProtocolException() {

    }

    GenericProtocolException(Throwable cause) {
        super(cause);
    }

    public abstract <T, R> R visitProtocolException(GenericProtocolExceptionVisitor<T, R> visitor, T parameter);
}
