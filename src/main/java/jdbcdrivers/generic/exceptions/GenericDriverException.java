package jdbcdrivers.generic.exceptions;

/**
 * Base class for exceptions thrown by generic database code.
 */
public abstract class GenericDriverException extends Exception {

    private static final long serialVersionUID = 1L;

    GenericDriverException() {

    }

    GenericDriverException(String message, Throwable cause) {
        super(message, cause);
    }

    GenericDriverException(String message) {
        super(message);
    }

    GenericDriverException(Throwable cause) {
        super(cause);
    }
}
