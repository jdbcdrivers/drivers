package jdbcdrivers.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import jdbcdrivers.jdbc.PreparedStatements.PreparedSelect;

public final class ConcurrentResultSetsTest extends BaseJDBCDriverIntegrationTest {

    @Test
    public void testSelectWithinSelect() throws SQLException {

        runTableTest((c, t) -> {

            final int numRows = 10;

            final List<Object[]> insertedRows = insertRows(c, t, numRows);

            try (PreparedSelect preparedSelect1 = PreparedStatements.prepareAllSelect(c, t)) {

                try (PreparedSelect preparedSelect2 = PreparedStatements.prepareAllSelect(c, t)) {

                    ResultSet resultSet1 = null;
                    ResultSet resultSet2 = null;

                    try {
                        resultSet1 = preparedSelect1.getPreparedStatement().executeQuery();
                        resultSet2 = preparedSelect2.getPreparedStatement().executeQuery();

                        for (Object[] insertedRow : insertedRows) {

                            assertThat(resultSet1.next()).isTrue();
                            checkRowsAreEqual(insertedRow, retrieveRow(resultSet1, t));

                            assertThat(resultSet2.next()).isTrue();
                            checkRowsAreEqual(insertedRow, retrieveRow(resultSet2, t));
                        }

                        assertThat(resultSet1.next()).isFalse();
                        assertThat(resultSet2.next()).isFalse();
                    }
                    finally {

                        safeCloseAll(resultSet1, resultSet2);
                    }
                }
            }
        });
    }
}
