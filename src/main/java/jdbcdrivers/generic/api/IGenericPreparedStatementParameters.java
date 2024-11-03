package jdbcdrivers.generic.api;

import java.sql.JDBCType;

/**
 * Parameter values for prepared statements.
 */
public interface IGenericPreparedStatementParameters {

    void setBoolean(int index, boolean value);

    void setByte(int index, byte value);
    void setShort(int index, short value);
    void setInt(int index, int value);
    void setLong(int index, long value);

    void setFloat(int index, float value);
    void setDouble(int index, double value);

    void setObject(int index, Object value);
    void setObject(int index, Object value, JDBCType jdbcType);

    void setNull(int index, JDBCType jdbcType);

    void clear();
}
