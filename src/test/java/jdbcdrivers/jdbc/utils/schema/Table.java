package jdbcdrivers.jdbc.utils.schema;

import java.util.List;

public final class Table extends BaseTable {

    Table(BaseTable toCopy) {
        super(toCopy);
    }

    public String getName() {

        return getTableName();
    }

    public final String getPKColumnName() {

        return getPKColumnNameString();
    }

    public int getNumColumns() {

        return getColumnsList().size();
    }

    public List<Column> getColumns() {

        return getColumnsList();
    }
}
