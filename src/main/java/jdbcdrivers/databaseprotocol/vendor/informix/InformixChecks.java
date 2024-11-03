package jdbcdrivers.databaseprotocol.vendor.informix;

import jdbcdrivers.util.DriverUtil;

/**
 * Various Informix protocol related value validations.
 */
class InformixChecks {

    static boolean isValidPreparedStatementIdentifier(int identifier) {

        return identifier >= 0 && identifier <= DriverUtil.MAX_UNSIGNED_SHORT;
    }

    static boolean isValidNumColumns(int numColumns) {

        return numColumns >= 0 && numColumns <= DriverUtil.MAX_UNSIGNED_SHORT;
    }
}
