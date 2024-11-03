package jdbcdrivers.generic;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.Arrays;
import java.util.Objects;

import jdbcdrivers.databaseprotocol.api.IGenericPreparedStatementParameterGetters;
import jdbcdrivers.generic.TimedFreeList.TimedListNode;
import jdbcdrivers.generic.api.IGenericPreparedStatementParameters;

/**
 * Parameter values for prepared statements.
 *
 * @implNote allows for reusing objects by way of a free list, to reduce the number of object allocations
 */
final class GenericPreparedStatementParameters extends TimedListNode<GenericPreparedStatementParameters>
        implements IGenericPreparedStatementParameters, IGenericPreparedStatementParameterGetters {

    private static final Object notIsSet = new Object();

    private final Object[] parameters;

    GenericPreparedStatementParameters(int numParameters) {

        this.parameters = new Object[numParameters];

        clear();
    }

    @Deprecated
    private GenericPreparedStatementParameters(GenericPreparedStatementParameters toCopy) {

        this.parameters = new Object[toCopy.parameters.length];

        copy(toCopy);
    }

    @Override
    public final int getNumParameters() {

        return parameters.length;
    }

    @Override
    public void setBoolean(int index, boolean value) {

        setParameter(index, value ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override
    public void setByte(int index, byte value) {

        setParameter(index, CachedValues.getInstance().getByte(value));
    }

    @Override
    public void setShort(int index, short value) {

        setParameter(index, CachedValues.getInstance().getShort(value));
    }

    @Override
    public void setInt(int index, int value) {

        setParameter(index, CachedValues.getInstance().getInt(value));
    }

    @Override
    public void setLong(int index, long value) {

        setParameter(index, CachedValues.getInstance().getLong(value));
    }

    @Override
    public void setFloat(int index, float value) {

        setParameter(index, CachedValues.getInstance().getFloat(value));
    }

    @Override
    public void setDouble(int index, double value) {

        setParameter(index, CachedValues.getInstance().getDouble(value));
    }

    @Override
    public void setObject(int index, Object value) {

        Objects.requireNonNull(value);

        setParameter(index, value);
    }

    @Override
    public void setObject(int index, Object value, JDBCType jdbcType) {

        Objects.requireNonNull(value);
        Objects.requireNonNull(jdbcType);

        setParameter(index, value);
    }

    @Override
    public void setNull(int index, JDBCType jdbcType) {

        setParameter(index, null);
    }

    /**
     * Clear for object reuse.
     */
    @Override
    public void clear() {

        Arrays.fill(parameters, notIsSet);
    }

    @Override
    public ParameterType getParameterType(int index) {

        final Object parameter = getParameter(index);

        final ParameterType result;

        if (parameter == null) {

            result = ParameterType.NULL;
        }
        else {
            if (parameter instanceof Boolean) {

                result = ParameterType.BOOLEAN;
            }
            else if (parameter instanceof Byte) {

                result = ParameterType.BYTE;
            }
            else if (parameter instanceof Short) {

                result = ParameterType.SHORT;
            }
            else if (parameter instanceof Integer) {

                result = ParameterType.INT;
            }
            else if (parameter instanceof Long) {

                result = ParameterType.LONG;
            }
            else if (parameter instanceof Float) {

                result = ParameterType.FLOAT;
            }
            else if (parameter instanceof Double) {

                result = ParameterType.DOUBLE;
            }
            else if (parameter instanceof BigDecimal) {

                result = ParameterType.DECIMAL;
            }
            else if (parameter instanceof String) {

                result = ParameterType.STRING;
            }
            else {
                throw new UnsupportedOperationException();
            }
        }

        return result;
    }

    @Override
    public boolean getBoolean(int index) {

        return (Boolean)getParameter(index);
    }

    @Override
    public byte getByte(int index) {

        return (Byte)getParameter(index);
    }

    @Override
    public short getShort(int index) {

        return (Short)getParameter(index);
    }

    @Override
    public int getInt(int index) {

        return (Integer)getParameter(index);
    }

    @Override
    public long getLong(int index) {

        return (Long)getParameter(index);
    }

    @Override
    public float getFloat(int index) {

        return (Float)getParameter(index);
    }

    @Override
    public double getDouble(int index) {

        return (Double)getParameter(index);
    }

    @Override
    public BigDecimal getDecimal(int index) {

        return (BigDecimal)getParameter(index);
    }

    @Override
    public String getString(int index) {

        return (String)getParameter(index);
    }

    @Override
    public boolean isNull(int index) {

        return getParameter(index) == null;
    }

    private void setParameter(int index, Object parameter) {

        checkIndex(index);

        parameters[index] = parameter;
    }

    public boolean areAllSet() {

        boolean areAllSet = true;

        final int numParameters = parameters.length;

        for (int i = 0; i < numParameters; ++ i) {

            if (parameters[i] == notIsSet) {

                areAllSet = false;
                break;
            }
        }

        return areAllSet;
    }

    @Deprecated
    GenericPreparedStatementParameters makeCopy() {

        return new GenericPreparedStatementParameters(this);
    }

    void copy(GenericPreparedStatementParameters toCopy) {

        final int numParameters = parameters.length;

        if (numParameters != toCopy.parameters.length) {

            throw new IllegalArgumentException();
        }

        System.arraycopy(toCopy.parameters, 0, toCopy.parameters, 0, numParameters);
    }

    private Object getParameter(int index) {

        checkIndex(index);

        final Object parameter = parameters[index];

        if (parameter == notIsSet) {

            throw new IllegalStateException();
        }

        return parameter;
    }

    private void checkIndex(int index) {

        if (index < 0) {

            throw new IllegalArgumentException();
        }

        if (index >= parameters.length) {

            throw new IllegalArgumentException();
        }
    }
}
