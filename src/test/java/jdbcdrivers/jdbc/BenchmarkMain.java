package jdbcdrivers.jdbc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Objects;

import jdbcdrivers.jdbc.utils.schema.Table;
import jdbcdrivers.jdbc.utils.schema.TableBuilder;
import jdbcdrivers.jdbc.utils.schema.TableSQL;

public class BenchmarkMain extends BaseDriverTest {

    public static void main(String[] args) throws SQLException {

        if (args.length != 7) {

            System.err.println("Usage: <url> <db user> <db password> <driver> <num integer columns> <num varchar columns> <varchar maxlength>");
        }

        final String url = args[0];
        final String user = args[1];
        final String password = args[2];

        final TestInformixDriver testInformixDriver = TestInformixDriver.findFromCommanLineArgument(args[3]);

        final String tableName = "benchmark_table";

        final TableBuilder tableBuilder = TableBuilder.create(tableName, "id");

        final int numIntegerColumns = parseNum(args[4]);
        final int numVarcharColumns = parseNum(args[5]);
        final int varcharMaxLength = parseLength(args[6]);

        for (int i = 0; i < numIntegerColumns; ++ i) {

            tableBuilder.addIntegerColumn(TableSQL.getColumnTypeName(JDBCType.INTEGER) + i);
        }

        for (int i = 0; i < numVarcharColumns; ++ i) {

            tableBuilder.addVarcharColumn(TableSQL.getColumnTypeName(JDBCType.VARCHAR) + i, varcharMaxLength);
        }

        final Table table = tableBuilder.build();

        final boolean returnAutoGeneratedKeys = true;

        try (Connection connection = connect(testInformixDriver, url, user, password.trim().isEmpty() ? null : password)) {

            connection.setAutoCommit(false);

            runIterations(connection, table, returnAutoGeneratedKeys, "insert", BaseDriverTest::insertRows);

            printGCAndMemoryStats("after complete insertions run");

            runIterations(connection, table, returnAutoGeneratedKeys, "batch", BaseDriverTest::batchRows);

            printGCAndMemoryStats("after complete batches run");
        }

        printGCAndMemoryStats("after complete run");
    }

    private static void runIterations(Connection connection, Table table, boolean returnAutoGeneratedKeys, String action, RowsAdder rowsAdder) throws SQLException {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(table);
        Objects.requireNonNull(action);
        Objects.requireNonNull(rowsAdder);

        final TimePrinter timePrinter = new TimePrinter();

        for (int numRows = 1; numRows <= 10 * 1000 * 1000; numRows *= 10) {

            System.out.println("Start action '" + action + "' on " + numRows + " rows");

            final int closureNumRows = numRows;

            runForTable(connection, table, (c, t) -> checkInsertAndSelect(c, t, closureNumRows, returnAutoGeneratedKeys, action, timePrinter, rowsAdder));
        }
    }

    private static void checkInsertAndSelect(Connection connection, Table table, int numRows, boolean returnAutoGeneratedKeys, String action, TimePrinter timePrinter,
            RowsAdder rowsAdder) throws SQLException {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(table);

        if (numRows < 0) {

            throw new IllegalArgumentException();
        }

        Objects.requireNonNull(action);
        Objects.requireNonNull(timePrinter);
        Objects.requireNonNull(rowsAdder);

        timePrinter.runTimed(action + " add rows", numRows, () -> rowsAdder.addRows(connection, table, numRows, returnAutoGeneratedKeys));

        connection.commit();

        printGCAndMemoryStats("after adding " + numRows + " rows");

        System.gc();

        timePrinter.runTimed(action + " select rows", numRows, () -> selectAllRows(connection, table));

        printGCAndMemoryStats("after selecting " + numRows + " rows");
    }

    @FunctionalInterface
    interface SQLRunnable {

        void run() throws SQLException;
    }

    private static final class TimePrinter {

        private final long start;

        TimePrinter() {

            this.start = now();
        }

        void runTimed(String action, int numRows, SQLRunnable sqlRunnable) throws SQLException {

            final long before = now();

            sqlRunnable.run();

            final long after = now();

            System.out.println("Ran action '" + action + "', took " + (after - before) + " millis, elapsed " + (after - start) + " millis on " + numRows + " rows");
        }

        private static long now() {

            return System.currentTimeMillis();
        }
    }

    private static void printGCAndMemoryStats(String location) {

        Objects.requireNonNull(location);

        System.out.println("Memory and GC stats at " + location + ':');

        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        printMemoryUsage("heap", memoryMXBean.getHeapMemoryUsage());
        printMemoryUsage("non-heap", memoryMXBean.getNonHeapMemoryUsage());

        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {

            System.out.println("Garbage collection name=" + garbageCollectorMXBean.getName() + " count=" + garbageCollectorMXBean.getCollectionCount()
                    + " time=" + garbageCollectorMXBean.getCollectionTime());
        }
    }

    private static void printMemoryUsage(String name, MemoryUsage memoryUsage) {

        Objects.requireNonNull(name);
        Objects.requireNonNull(memoryUsage);

        System.out.println("Memory uage name=" + name + " used=" + memoryUsage.getUsed() + " init=" + memoryUsage.getInit() + " commited=" + memoryUsage.getCommitted()
                    + " max=" + memoryUsage.getMax());
    }

    private static int parseNum(String argument) {

        Objects.requireNonNull(argument);

        final int result = Integer.parseInt(argument);

        if (result < 0) {

            throw new IllegalArgumentException();
        }

        return result;
    }

    private static int parseLength(String argument) {

        Objects.requireNonNull(argument);

        final int result = Integer.parseInt(argument);

        if (result <= 0) {

            throw new IllegalArgumentException();
        }

        return result;
    }
}
