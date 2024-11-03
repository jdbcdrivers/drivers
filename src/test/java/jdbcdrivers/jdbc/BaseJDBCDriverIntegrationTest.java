package jdbcdrivers.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import jdbcdrivers.jdbc.utils.schema.Table;
import jdbcdrivers.jdbc.utils.schema.TableBuilder;

abstract class BaseJDBCDriverIntegrationTest extends BaseDriverTest {

    @FunctionalInterface
    interface TestRunnable {

        void run(Connection connection) throws SQLException;
    }

    private static Connection connect(TestInformixDriver testInformixDriver) throws SQLException {

        Objects.requireNonNull(testInformixDriver);

        final String url = "jdbc:informix-sqli://localhost:50110/testdb";

        final String user = "testuser";
        final String password = "secret";

        return connect(testInformixDriver, url, user, password);
    }

    static void runTest(TestRunnable testRunnable) throws SQLException {

        runTest(TestInformixDriver.OFFICIAL, testRunnable);
        runTest(TestInformixDriver.REIMPLEMENTED, testRunnable);
    }

    private static void runTest(TestInformixDriver testInformixDriver, TestRunnable testRunner) throws SQLException {

        try (Connection connection = connect(testInformixDriver)) {

            testRunner.run(connection);
        }
    }

    static void runTableTest(TableTestRunnable tableTestRunnable) throws SQLException {

        runTableTest(false, tableTestRunnable);
    }

    static void runTableTest(boolean autoCommit, TableTestRunnable tableTestRunnable) throws SQLException {

        Objects.requireNonNull(tableTestRunnable);

        runTest(c -> checkTable(c, autoCommit, tableTestRunnable));
    }

    static void runTableTest(Table table, boolean autoCommit, TableTestRunnable tableTestRunnable) throws SQLException {

        Objects.requireNonNull(table);
        Objects.requireNonNull(tableTestRunnable);

        runTest(c -> checkTable(c, table, autoCommit, tableTestRunnable));
    }

    private static void checkTable(Connection connection, boolean autoCommit, TableTestRunnable tableTestRunnable) throws SQLException {

        final String tableName = "test_table";

        final Table table = TableBuilder.create(tableName, "id")
                .addVarcharColumn("varchar_column", 20)
                .addIntegerColumn("integer_column")
                .addCharColumn("char_column", 30)
                .build();

        checkTable(connection, table, autoCommit, tableTestRunnable);
    }

    private static void checkTable(Connection connection, Table table, boolean autoCommit, TableTestRunnable tableTestRunnable) throws SQLException {

        if (!autoCommit) {

            connection.setAutoCommit(false);
        }

        runForTable(connection, table, tableTestRunnable);
    }
}
