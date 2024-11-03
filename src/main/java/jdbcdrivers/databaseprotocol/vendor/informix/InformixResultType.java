package jdbcdrivers.databaseprotocol.vendor.informix;

import jdbcdrivers.util.EncodedEnum;

enum InformixResultType implements EncodedEnum<InformixResultType> {

    SELECT(0x0002),
    INSERT_UPDATE(0x0006),
    EXECUTE_SQL(0x002D);

    static InformixResultType fromCodeOrNull(int code) {

        return EncodedEnum.findEnumOrNull(InformixResultType.class, code);
    }

    private final int code;

    private InformixResultType(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
