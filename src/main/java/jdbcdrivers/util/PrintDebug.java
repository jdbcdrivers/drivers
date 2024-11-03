package jdbcdrivers.util;

import java.util.Objects;

public interface PrintDebug {

    default PrintDebug println(String message) {

        println(getClass(), message);

        return this;
    }

    default PrintDebug formatln(String format, Object ... parameters) {

        formatln(getClass(), format, parameters);

        return this;
    }

    public static void println(Class<?> javaClass, String message) {

        Objects.requireNonNull(javaClass);
        Objects.requireNonNull(message);

        System.out.println(javaClass.getSimpleName() + ' ' + message);
    }

    public static void formatln(Class<?> javaClass, String format, Object ... parameters) {

        Objects.requireNonNull(javaClass);
        Objects.requireNonNull(format);

        println(javaClass, String.format(format, parameters));
    }
}
