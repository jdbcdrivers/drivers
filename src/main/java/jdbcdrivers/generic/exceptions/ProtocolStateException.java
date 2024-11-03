package jdbcdrivers.generic.exceptions;

/**
 * Exception thrown for errors with regards to the database interaction state machinery.
 */
public final class ProtocolStateException extends GenericProtocolException {

    private static final long serialVersionUID = 1L;

    @Override
    public <T, R> R visitProtocolException(GenericProtocolExceptionVisitor<T, R> visitor, T parameter) {

        return visitor.onState(this, parameter);
    }
}