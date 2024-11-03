package jdbcdrivers.jdbc.utils.schema;

import java.sql.JDBCType;
import java.util.Objects;

public final class Column {

    private final String name;
    private final JDBCType jdbcType;
    private final Integer length;

    Column(String name, JDBCType jdbcType) {

        this.name = Objects.requireNonNull(name);
        this.jdbcType = Objects.requireNonNull(jdbcType);

        this.length = null;
    }

    Column(String name, JDBCType jdbcType, int length) {

        Objects.requireNonNull(name);
        Objects.requireNonNull(jdbcType);

        if (length <= 0) {

            throw new IllegalArgumentException();
        }

        this.name = name;
        this.jdbcType = jdbcType;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public JDBCType getJDBCType() {
        return jdbcType;
    }

    public Integer getLength() {
        return length;
    }
}
