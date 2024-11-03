package jdbcdrivers.databaseprotocol.vendor.informix;

import jdbcdrivers.util.EncodedEnum;

@Deprecated
enum InformixExecuteType implements EncodedEnum<InformixExecuteType> {

    DIRECT(0x0000),
    PREPARED_STATEMENT(0x0002);

    private final int code;

    private InformixExecuteType(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
