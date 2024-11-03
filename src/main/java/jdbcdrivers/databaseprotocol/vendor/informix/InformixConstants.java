package jdbcdrivers.databaseprotocol.vendor.informix;

/**
 * Various Informix protocol related constants.
 */
class InformixProtocolConstants {

    static final short TERMINATOR = 0x000C;

    static final int MAX_STRING_CHARACTERS = 1 << 16;
    static final int MAX_STRING_BYTES = MAX_STRING_CHARACTERS * 4;
}
