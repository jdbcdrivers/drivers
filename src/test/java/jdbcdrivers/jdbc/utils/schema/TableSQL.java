package jdbcdrivers.jdbc.utils.schema;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class TableSQL {

    public static String makeCreateTableSQL(Table table, boolean tempTable) {

        Objects.requireNonNull(table);

        final StringBuilder sb = new StringBuilder();

        sb.append("create");

        if (tempTable) {

            sb.append(" temp");
        }

        sb.append(" table ").append(table.getTableName()).append(" (");

        final String pkColumnName = table.getPKColumnNameString();

        if (pkColumnName != null) {

            sb.append(pkColumnName).append(" serial").append(',');
        }

        final List<Column> columns = table.getColumns();

        final int numColumns = columns.size();

        for (int i = 0; i < numColumns; ++ i) {

            if (i > 0) {

                sb.append(',');
            }

            final Column column = columns.get(i);

            sb.append(column.getName()).append(' ').append(getColumnTypeName(column.getJDBCType()));

            final Integer length = column.getLength();

            if (length != null) {

                sb.append('(').append(length).append(')');
            }
        }

        sb.append(')');

        return sb.toString();
    }

    public static String makeDropTableSQL(Table table) throws SQLException {

        Objects.requireNonNull(table);

        return "drop table " + table.getTableName();
    }

    public static String makeInsertSQL(Table table) {

        Objects.requireNonNull(table);

        final StringBuilder sb = new StringBuilder();

        sb.append("insert into ").append(table.getName());

        sb.append(" (");

        final List<Column> columns = table.getColumns();

        final int numColumns = columns.size();

        for (int i = 0; i < numColumns; ++ i) {

            if (i > 0) {

                sb.append(',');
            }

            sb.append(columns.get(i).getName());
        }

        sb.append(')');

        sb.append(" values (");

        addSQLParameters(numColumns, sb);

        sb.append(')');

        return sb.toString();
    }

    private static void addSQLParameters(int numParameters, StringBuilder sb) {

        if (numParameters < 0) {

            throw new IllegalArgumentException();
        }

        Objects.requireNonNull(sb);

        for (int i = 0; i < numParameters; ++ i) {

            if (i > 0) {

                sb.append(',');
            }

            sb.append('?');
        }
    }

    public static String getColumnTypeName(JDBCType jdbcType) {

        final String result;

        switch (jdbcType) {

        case INTEGER:

            result = "integer";
            break;

        case CHAR:

            result = "char";
            break;

        case VARCHAR:

            result = "varchar";
            break;

        case LONGVARCHAR:

            result = "lvarchar";
            break;

        default:
            throw new UnsupportedOperationException();
        }

        return result;
    }
}
