package jdbcdrivers.generic.util;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;
import java.util.Objects;

/**
 * For encoding strings to bytes.
 */
public final class StringEncoder {

    private static final boolean DEBUG = Boolean.FALSE;

    private final CharsetEncoder charsetEncoder;

    private final char[] encodeStringCharacters;
    private final CharBuffer encodeStringCharBuffer;

    private final byte[] encodedStringBytes;
    private final ByteBuffer encodedByteBuffer;

    public StringEncoder(Charset charset, int maxLength) {
        this(charset.newEncoder(), maxLength);
    }

    StringEncoder(CharsetEncoder charsetEncoder, int maxLength) {

        Objects.requireNonNull(charsetEncoder);

        if (maxLength < 1) {

            throw new IllegalArgumentException();
        }

        this.charsetEncoder = charsetEncoder;

        this.encodeStringCharacters = new char[maxLength];
        this.encodeStringCharBuffer = CharBuffer.wrap(encodeStringCharacters);

        this.encodedStringBytes = new byte[maxLength * 4];
        this.encodedByteBuffer = ByteBuffer.wrap(encodedStringBytes);
    }

    /**
     * Encode a {@link String}
     *
     * @param string the {@link String} to encode
     *
     * @return number of resulting bytes
     */
    public int encode(String string) {

        final int stringLength = string.length();

        string.getChars(0, stringLength, encodeStringCharacters, 0);

        if (DEBUG) {

            System.out.println("encode chars '" + string + "' " + Arrays.toString(encodeStringCharacters));
        }

        encodeStringCharBuffer.position(0);
        encodeStringCharBuffer.limit(stringLength);

        encodedByteBuffer.position(0);
        encodedByteBuffer.limit(encodedByteBuffer.capacity());

        final CoderResult coderResult = charsetEncoder.encode(encodeStringCharBuffer, encodedByteBuffer, true);

        if (coderResult.isError()) {

            throw new IllegalStateException();
        }

        return encodedByteBuffer.position();
    }

    /**
     * Write all encoded data to a {@link DataOutput}, and reset {@link StringEncoder}.
     *
     * @param dataOutput the {@link DataOutput} to write to
     *
     * @throws IOException forwarded from {@link DataOutput}
     */
    public void writeEncoded(DataOutput dataOutput) throws IOException {

        if (DEBUG) {

            System.out.println("write encoded " + encodedByteBuffer.position() + ' ' + Arrays.toString(encodedStringBytes));
        }

        dataOutput.write(encodedStringBytes, 0, encodedByteBuffer.position());
    }
}
