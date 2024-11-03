package jdbcdrivers.databaseprotocol.api;

import java.math.BigDecimal;

/**
 * Getters for prepared statement parameters.
 */
public interface IGenericPreparedStatementParameterGetters {

    /**
     * Types of parameters, corresponds to Java types.
     */
    public enum ParameterType {

        BOOLEAN,

        BYTE,
        SHORT,
        INT,
        LONG,

        FLOAT,
        DOUBLE,

        DECIMAL,

        STRING,

        NULL
    }

    /**
     * Get number of parameters.
     *
     * @return number of parameters
     */
    int getNumParameters();

    /**
     * Get parameter type at an index, counting from {@code 0}.
     *
     * @param index parameter index
     *
     * @return a {@link ParameterType} for the supplied index
     */
    ParameterType getParameterType(int index);

    boolean getBoolean(int index);

    byte getByte(int index);
    short getShort(int index);
    int getInt(int index);
    long getLong(int index);

    float getFloat(int index);
    double getDouble(int index);

    BigDecimal getDecimal(int index);

    String getString(int index);

    boolean isNull(int index);
}
