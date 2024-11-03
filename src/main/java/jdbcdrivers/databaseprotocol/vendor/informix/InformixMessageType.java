package jdbcdrivers.databaseprotocol.vendor.informix;

import java.util.Objects;

import jdbcdrivers.util.EncodedEnum;

/**
 * Informix protocol message types.
 */
enum InformixMessageType implements EncodedEnum<InformixMessageType> {

    SET_ISOLATION(0x0001, Direction.OUTBOUND),
    EXECUTE_SQL(0x0002, Direction.OUTBOUND),

    OPERATION(0x0004, Direction.OUTBOUND),

    TERMINATOR(InformixProtocolConstants.TERMINATOR, Direction.BOTH),

    COMMIT(0x0013, Direction.OUTBOUND),
    BEGIN(0x0023, Direction.OUTBOUND),

    SELECT_DATABASE(0x0024, Direction.OUTBOUND),

    PROPERTIES(0x051, Direction.OUTBOUND),

    UNKNOWN1(0x007E, Direction.OUTBOUND),

    @Deprecated
    RESULT_ROW(0x0006, Direction.INBOUND),

    RESULT(0x0008, Direction.INBOUND),
    ERROR(0x000D, Direction.INBOUND),
    QUERY_RESPONSE(0x000E, Direction.INBOUND),
    RESPONSE_STATUS(0x000F, Direction.INBOUND),
    VALUES_FOR_PREPARED_RESPONSE(0x005E, Direction.INBOUND),
    COMMIT_RESPONSE(0x0063, Direction.INBOUND),

    EXECUTE_BATCH_RESPONSE(0x007D, Direction.INBOUND),

    AUTO_GENERATED_KEY(0x0088, Direction.INBOUND);

    static InformixMessageType fromCodeOrNull(int code) {

        return EncodedEnum.findEnumOrNull(InformixMessageType.class, code);
    }

    private final int code;
    private final Direction direction;

    private InformixMessageType(int code, Direction direction) {

        if (code <= 0) {

            throw new IllegalArgumentException();
        }

        Objects.requireNonNull(direction);

        this.code = code;
        this.direction = direction;
    }

    @Override
    public int getCode() {
        return code;
    }

    Direction getDirection() {
        return direction;
    }

    enum Direction {

        INBOUND,
        OUTBOUND,
        BOTH;
    }
}
