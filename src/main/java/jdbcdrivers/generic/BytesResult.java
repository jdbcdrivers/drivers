package jdbcdrivers.generic;

import java.nio.ByteBuffer;

/**
 * For retrieving bytes to.
 */
final class BytesResult {

    private byte[] bytes;
    private ByteBuffer byteBuffer;
    private int bytesOffset;

    /**
     * Initialize with a buffer of bytes.
     *
     * @param bytes an array of bytes
     * @param byteBuffer a corresponding {@link ByteBuffer}
     * @param offset an offset into what bytes were buffered
     */
    void init(byte[] bytes, ByteBuffer byteBuffer, int offset) {

        this.bytes = bytes;
        this.byteBuffer = byteBuffer;
        this.bytesOffset = offset;
    }

    byte[] getBytes() {
        return bytes;
    }

    ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    int getBytesOffset() {
        return bytesOffset;
    }
}
