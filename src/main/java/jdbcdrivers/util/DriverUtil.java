package jdbcdrivers.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Various helper methods and constants for protocol implementations.
 */
public class DriverUtil {

    /**
     * Maximum value for unsigned bytes.
     */
    public static final int MAX_UNSIGNED_BYTE = (1 << 8) - 1;

    /**
     * Maximum value for unsigned shorts.
     */
    public static final int MAX_UNSIGNED_SHORT = (1 << 16) - 1;

    /**
     * Maximum value for ASCII characters.
     */
    public static final int MAX_ASCII = 127;

    /**
     * Create an unmodifiable copy of a {@link Collection}. Useful when a {@link Collection} is passed to and stored in a constructor,
     * to avoid impact of further modification of the {@link Collection} on the outside.
     *
     * @param <T> collection type
     * @param collection the {@link Collection} to copy and return an unmodifiable view to
     *
     * @return an unmodifiable copy of the {@link Collection}
     *
     * @throws NullPointerException if {@code collection} is {@code null}
     */
    public static <T> List<T> unmodifiableCopyOf(Collection<T> collection) {

        return Collections.unmodifiableList(new ArrayList<>(collection));
    }

    /**
     * Find the enum value, seleted by a {@link Predicate}.
     *
     * @param <E> enum type
     * @param enumClass the {@link Class} of the enum type
     * @param predicate the {@link Predicate} for selecting the applicable enum
     *
     * @return found enum value, or {@code null} if none found
     *
     * @throws IllegalStateException if more than one matching enum value was found
     */
    public static <E extends Enum<E>> E findEnumOrNull(Class<E> enumClass, Predicate<E> predicate) {

        return findAtMostOne(enumClass.getEnumConstants(), predicate);
    }

    /**
     * Find at most one value out of a {@link List}, returns {@code null} if none found. Throws {@link IllegalStateException} if more than one matching value is found.
     *
     * @param <T> list element type
     * @param list the {@link List} to search
     * @param predicate the {@link Predicate} for selecting the matching value
     *
     * @return matching value, or {@code null} if none found
     *
     * @throws IllegalStateException if more than one matching value was found
     */
    public static <T> T findAtMostOne(List<T> list, Predicate<T> predicate) {

        Objects.requireNonNull(list);
        Objects.requireNonNull(predicate);

        T found = null;

        final int numElements = list.size();

        for (int i = 0; i < numElements; ++ i) {

            final T element = list.get(i);

            if (element == null) {

                throw new IllegalArgumentException();
            }

            if (predicate.test(element)) {

                if (found != null) {

                    throw new IllegalStateException();
                }

                found = element;
            }
        }

        return found;
    }

    /**
     * Find at most one value out of an array, returns {@code null} if none found. Throws {@link IllegalStateException} if more than one matching value is found.
     *
     * @param <T> array type
     * @param array the array to search
     * @param predicate the {@link Predicate} for selecting the matching value
     *
     * @return matching value, or {@code null} if none found
     *
     * @throws IllegalStateException if more than one matching value was found
     */
    public static <T> T findAtMostOne(T[] array, Predicate<T> predicate) {

        Objects.requireNonNull(array);
        Objects.requireNonNull(predicate);

        T found = null;

        for (T element : array) {

            if (element == null) {

                throw new IllegalArgumentException();
            }

            if (predicate.test(element)) {

                if (found != null) {

                    throw new IllegalStateException();
                }

                found = element;
            }
        }

        return found;
    }

    /**
     * Find exactly most one value out of an array, returns {@code null} if none found. Throws {@link NoSuchElementException} if no matching value found
     * and {@link IllegalStateException} if more than one matching value is found.
     *
     * @param <T> array type
     * @param array the array to search
     * @param predicate the {@link Predicate} for selecting the matching value
     *
     * @return matching value, or {@code null} if none found
     *
     * @throws NoSuchElementException if no matching value was found
     * @throws IllegalStateException if more than one matching value was found
     */
    public static <T> T findExactlyOne(T[] array, Predicate<T> predicate) {

        Objects.requireNonNull(array);
        Objects.requireNonNull(predicate);

        T found = null;

        for (T element : array) {

            if (element == null) {

                throw new IllegalArgumentException();
            }

            if (predicate.test(element)) {

                if (found != null) {

                    throw new IllegalStateException();
                }

                found = element;
            }
        }

        if (found == null) {

            throw new NoSuchElementException();
        }

        return found;
    }

    /**
     * Write ASCII characters from a {@link String} to a {@link DataOutput}. The supplied {@link String} must contain only ASCII characters,
     * or {@link IllegalArgumentException} is thrown.
     *
     * @param dataOutput the {@link DataOutput} to write the {@link String} to
     * @param string the {@link String} to write
     *
     * @throws IOException forwarded from {@link DataInput}
     * @throws IllegalArgumentException if the supplied {@link String} contains non-ASCII characters
     */
    public static void writeASCIIStringCharacters(DataOutput dataOutput, String string) throws IOException {

        final int length = string.length();

        for (int i = 0; i < length; ++ i) {

            final char c = string.charAt(i);

            if (c > MAX_ASCII) {

                throw new IllegalArgumentException();
            }

            dataOutput.write(c);
        }
    }

    /**
     * Convert an unsigned {@code int} to a {@code byte}.
     *
     * @param value {@code int} value to convert
     *
     * @return the corresponding {@code byte} value
     */
    public static byte unsignedIntToByte(int value) {

        if (value < 0) {

            throw new IllegalArgumentException();
        }

        if (value > MAX_UNSIGNED_BYTE) {

            throw new IllegalArgumentException();
        }

        return (byte)value;
    }

    /**
     * Test for whether to pad a value, that is if the supplied {@code int} value is an odd value.
     *
     * @param value {@code int} value to test
     *
     * @return {@code true} if padding is necessary, {@code false} otherwise
     */
    public static boolean padToTwoBytes(int value) {

        if (value < 0) {

            throw new IllegalArgumentException();
        }

        return (value & 0x00000001) != 0;
    }
}
