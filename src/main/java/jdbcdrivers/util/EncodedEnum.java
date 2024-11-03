package jdbcdrivers.util;

/**
 * Interface for enums that correspond to codes sent over a protocol.
 */
public interface EncodedEnum<E extends Enum<E> & EncodedEnum<E>> {

    /**
     * Return the protocol code for an enum value.
     *
     * @return protocol code
     */
    int getCode();

    /**
     * Find the enum value corresponding to a protocol code.
     *
     * @param <E> protocol code enum type
     * @param enumClass the {@link Class} of the enum type
     * @param code protocol code
     *
     * @return found enum value, or {@code null} if none found
     *
     * @throws IllegalStateException if more than one matching enum value was found
     */
    public static <E extends Enum<E> & EncodedEnum<E>> E findEnumOrNull(Class<E> enumClass, int code) {

        return DriverUtil.findEnumOrNull(enumClass, e -> e.getCode() == code);
    }
}
