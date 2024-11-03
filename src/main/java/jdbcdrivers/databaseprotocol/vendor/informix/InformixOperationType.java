package jdbcdrivers.databaseprotocol.vendor.informix;

import jdbcdrivers.util.EncodedEnum;

enum InformixOperationType implements EncodedEnum<InformixOperationType> {

    IFXC_0x3(0x0003),

    INSERT(0x0005),

    @Deprecated
    OP_0x07(0x0007),
    @Deprecated
    OP_0x0B(0x000B),

    @Deprecated
    COMMIT(0x0007),

    CLOSE(0x000B),
//    SET_AUTO_COMMIT_OFF(0x000A),

    @Deprecated
    SET_AUTO_COMMIT_OFF(0x000B),
    EXECUTE_QUERY(0x0064),

    EXECUTE_BATCH(0x007C);

    static InformixOperationType fromCodeOrNull(int code) {

        return EncodedEnum.findEnumOrNull(InformixOperationType.class, code);
    }

    private final int code;

    private InformixOperationType(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
