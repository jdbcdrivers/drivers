package jdbcdrivers.jdbc.utils.schema;

import java.sql.JDBCType;
import java.util.Objects;

public final class TableBuilder extends BaseTable {

    public static TableBuilder create(String tableName, String pkColumnName) {

        Objects.requireNonNull(tableName);
        Objects.requireNonNull(pkColumnName);

        return new TableBuilder(tableName, pkColumnName);
    }

    private TableBuilder(String tableName, String pkColumName) {
        super(tableName, pkColumName);
    }

    public TableBuilder addIntegerColumn(String columnName) {

        addColumn(columnName, JDBCType.INTEGER);

        return this;
    }

    public TableBuilder addCharColumn(String columnName, int length) {

        addColumn(columnName, JDBCType.CHAR, length);

        return this;
    }

    public TableBuilder addVarcharColumn(String columnName, int maxLength) {

        addColumn(columnName, JDBCType.VARCHAR, maxLength);

        return this;
    }

    public TableBuilder addLVarcharColumn(String columnName, int maxLength) {

        addColumn(columnName, JDBCType.LONGVARCHAR, maxLength);

        return this;
    }

    public Table build() {

        return new Table(this);
    }
}
