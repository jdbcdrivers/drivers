package jdbcdrivers.databaseprotocol.vendor.informix;

import jdbcdrivers.util.DriverUtil;

/**
 * Base class for Informix prepared statements.
 */
abstract class InformixPreparedStatement {

    private final int identifier;

    InformixPreparedStatement(int identifier) {

        if (!InformixChecks.isValidPreparedStatementIdentifier(identifier)) {

            throw new IllegalArgumentException();
        }

        if (identifier > DriverUtil.MAX_UNSIGNED_SHORT) {

            throw new IllegalStateException();
        }

        this.identifier = identifier;
    }

    final int getIdentifier() {
        return identifier;
    }
}
