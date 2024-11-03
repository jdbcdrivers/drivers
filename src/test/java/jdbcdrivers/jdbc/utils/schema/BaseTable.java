package jdbcdrivers.jdbc.utils.schema;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class BaseTable {

    private final String tableName;
    private final String pkColumnName;

    private final List<Column> columns;

    BaseTable(String tableName, String pkColumnName) {

        this.tableName = Objects.requireNonNull(tableName);
        this.pkColumnName = Objects.requireNonNull(pkColumnName);

        this.columns = new ArrayList<>();
    }

    BaseTable(BaseTable toCopy) {

        Objects.requireNonNull(toCopy);

        this.tableName = toCopy.tableName;
        this.pkColumnName = toCopy.pkColumnName;
        this.columns = Collections.unmodifiableList(new ArrayList<>(toCopy.columns));
    }

    final String getTableName() {
        return tableName;
    }

    final String getPKColumnNameString() {
        return pkColumnName;
    }

    final List<Column> getColumnsList() {
        return columns;
    }

    final void addColumn(String columnName, JDBCType jdbcType) {

        columns.add(new Column(columnName, jdbcType));
    }

    final void addColumn(String columnName, JDBCType jdbcType, int length) {

        columns.add(new Column(columnName, jdbcType, length));
    }
}
