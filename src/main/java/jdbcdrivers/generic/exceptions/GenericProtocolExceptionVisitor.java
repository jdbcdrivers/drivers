package jdbcdrivers.generic.exceptions;

/**
 * Visitor interface for exceptions thrown by the protocol implementation.
 */
public interface GenericProtocolExceptionVisitor<T, R> {

    R onState(ProtocolStateException ex, T parameter);

    R onError(ProtocolErrorException ex, T parameter);

    R onErrorCode(ProtocolErrorCodeException ex, T parameter);

    R onIO(ProtocolIOException ex, T parameter);
}
