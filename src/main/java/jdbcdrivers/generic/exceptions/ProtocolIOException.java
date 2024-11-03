package jdbcdrivers.generic.exceptions;

import java.io.IOException;
import java.util.Objects;

/**
 * Exception thrown for {@link IOException} during protocol communication.
 */
public final class ProtocolIOException extends GenericProtocolException {

    private static final long serialVersionUID = 1L;

    public ProtocolIOException(IOException cause) {
        super(cause);

        Objects.requireNonNull(cause);
    }

    public IOException getIOException() {

        return (IOException)getCause();
    }

    @Override
    public <T, R> R visitProtocolException(GenericProtocolExceptionVisitor<T, R> visitor, T parameter) {

        return visitor.onError(null, parameter);
    }
}
