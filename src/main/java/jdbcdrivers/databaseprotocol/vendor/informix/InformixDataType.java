package jdbcdrivers.databaseprotocol.vendor.informix;

import java.sql.JDBCType;

import jdbcdrivers.util.EncodedEnum;

/**
 * Informix protocol data types.
 */
enum InformixDataType implements EncodedEnum<InformixDataType> {

    CHAR    (0x0000, JDBCType.CHAR,     true, InformixDataType.NO_RESPONSE_COLUMN_ADDITIONAL_LENGTH_BYTES, 2, true),
    INTEGER (0x0002, JDBCType.INTEGER,  true, InformixDataType.NO_RESPONSE_COLUMN_ADDITIONAL_LENGTH_BYTES, InformixDataType.NO_UPDATE_LENGTH_BYTES, false),
    SERIAL  (0x0006, JDBCType.INTEGER,  true, InformixDataType.NO_RESPONSE_COLUMN_ADDITIONAL_LENGTH_BYTES, InformixDataType.NO_UPDATE_LENGTH_BYTES, false),
    VARCHAR (0x000D, JDBCType.VARCHAR,  false, 1, 1, true);

    private static final int NO_RESPONSE_COLUMN_ADDITIONAL_LENGTH_BYTES = -1;
    private static final int NO_UPDATE_LENGTH_BYTES = -1;

    static InformixDataType fromCodeOrNull(int code) {

        return EncodedEnum.findEnumOrNull(InformixDataType.class, code);
    }

    private final int code;
    private final JDBCType jdbcType;
    private final boolean isConstantLength;
    private final int numResponseColumnAdditionalLengthBytes;
    private final int numUpdateLengthBytes;
    private final boolean isString;

    private InformixDataType(int code, JDBCType jdbcType, boolean constantLength, int numResponseColumnAdditionalLengthBytes, int numUpdateLengthBytes, boolean isString) {

        this.code = code;
        this.jdbcType = jdbcType;
        this.isConstantLength = constantLength;
        this.numResponseColumnAdditionalLengthBytes = numResponseColumnAdditionalLengthBytes;
        this.numUpdateLengthBytes = numUpdateLengthBytes;
        this.isString = isString;
    }

    @Override
    public int getCode() {
        return code;
    }

    JDBCType getJDBCType() {
        return jdbcType;
    }

    boolean isConstantLength() {
        return isConstantLength;
    }

    boolean hasResponseColumnAdditionalLengthBytes() {

        return numResponseColumnAdditionalLengthBytes != NO_RESPONSE_COLUMN_ADDITIONAL_LENGTH_BYTES;
    }

    int getNumResponseColumnAdditionalLengthBytes() {

        return numResponseColumnAdditionalLengthBytes;
    }

    boolean hasUpdateLengthBytes() {

        return numUpdateLengthBytes != NO_UPDATE_LENGTH_BYTES;
    }

    int getNumUpdateLengthBytes() {
        return numUpdateLengthBytes;
    }

    boolean isString() {
        return isString;
    }
}
