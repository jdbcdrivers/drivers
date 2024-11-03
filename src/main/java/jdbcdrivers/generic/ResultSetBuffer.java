package jdbcdrivers.generic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import jdbcdrivers.util.Chunks;
import jdbcdrivers.util.PrintDebug;

final class ResultSetBuffer extends Chunks<ResultSetBuffer.Chunk> implements PrintDebug {

    private static final boolean DEBUG = Boolean.FALSE;

    static final class Chunk {

        private final byte[] buffer;
        private final ByteBuffer byteBuffer;

        Chunk(int chunkSize) {

            this.buffer = new byte[chunkSize];
            this.byteBuffer = ByteBuffer.wrap(buffer);
        }

        @Override
        public String toString() {

            return getClass().getSimpleName() + " [buffer=" + buffer.length + " byteBuffer.getCapacity()=" + byteBuffer.capacity() + "]";
        }
    }

    private final byte[] decodeBuffer;
    private final ByteBuffer decodeByteBuffer;

    private long getTotalNumBytes() {

        return getNumElements();
    }

    ResultSetBuffer(int chunkSize) {
        super(chunkSize, Chunk[]::new, Chunk::new);

        this.decodeBuffer = new byte[chunkSize];
        this.decodeByteBuffer = ByteBuffer.wrap(decodeBuffer);
    }

    void addData(byte[] bytes, int offset, int length) {

        Objects.requireNonNull(bytes);

        if (offset < 0) {

            throw new IllegalArgumentException();
        }

        if (length < 1) {

            throw new IllegalArgumentException();
        }

        if (offset >= length) {

            throw new IllegalArgumentException();
        }

        addData(bytes, offset, length, (i, s, c, d, l) -> copyBytes(i, s, c.buffer, d, l));
    }

    void addData(InputStream inputStream, int numBytes) throws IOException {

        Objects.requireNonNull(inputStream);

        if (numBytes < 1) {

            throw new IllegalArgumentException();
        }

        addData(inputStream, 0, numBytes, (i, s, c, d, l) -> i.read(c.buffer, d, l));
    }

    @FunctionalInterface
    interface BufferDecoder {

        void decode(byte[] data, ByteBuffer byteBuffer, int offset, int length);
    }

    @FunctionalInterface
    private interface BufferResultDecoder<T> {

        void decode(byte[] data, ByteBuffer byteBuffer, int offset, int length, T parameter);
    }

    void decodeMaxLength(long offset, int maxLength, BytesResult dst) {

        final long totalNumBytes = getTotalNumBytes();

        if (offset >= totalNumBytes) {

            throw new IllegalArgumentException();
        }

        final long remaining = totalNumBytes - offset;

        final int length = remaining < maxLength ? (int)remaining : maxLength;

        decode(offset, length, dst);
    }

    void decode(long offset, int length, BytesResult dst) {

        decodeResult(offset, length, dst, (d, b, o, l, p) -> p.init(d, b, o));
    }

    void decode(long offset, int length, BufferDecoder bufferDecoder) {

        decodeResult(offset, length, null, (d, b, o, l, p) -> bufferDecoder.decode(d, b, o, l));
    }

    private <T> void decodeResult(long offset, int length, T parameter, BufferResultDecoder<T> bufferResultDecoder) {

        if (offset < 0L) {

            throw new IllegalArgumentException();
        }

        if (length <= 0) {

            throw new IllegalArgumentException();
        }

        if (length > getChunkSize()) {

            throw new IllegalArgumentException();
        }

        final long totalNumBytes = getTotalNumBytes();

        if (offset >= totalNumBytes) {

            throw new IllegalArgumentException();
        }

        if (offset + length > totalNumBytes) {

            throw new IllegalArgumentException();
        }

        final int chunkIndex = getChunkIndex(offset);

        Chunk chunk = getChunk(chunkIndex);

        final int chunkOffset = getChunkOffset(offset);

        final int numElementsOfChunk = getNumElementsOfChunk(chunkIndex);

        final int chunkRemaining = numElementsOfChunk - chunkOffset;

        if (DEBUG) {

            println("decode offset=" + offset + " length=" + length + " totalNumBytes=" + totalNumBytes + " chunkIndex=" + chunkIndex + " chunkOffset=" + chunkOffset
                    + " chunkRemaining=" + chunkRemaining + " numElementsOfChunk=" + numElementsOfChunk + " super=" + super.toString());
        }

        if (chunkRemaining >= length) {

            bufferResultDecoder.decode(chunk.buffer, chunk.byteBuffer, chunkOffset, length, parameter);
        }
        else {
            copyBytes(chunk.buffer, chunkOffset, decodeBuffer, 0, chunkRemaining);

            final Chunk nextChunk = getChunk(chunkIndex + 1);

            copyBytes(nextChunk.buffer, 0, decodeBuffer, chunkRemaining, length - chunkRemaining);

            bufferResultDecoder.decode(decodeBuffer, decodeByteBuffer, 0, length, parameter);
        }
    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + " [super=" + super.toString() + ", totalNumBytes=" + getTotalNumBytes() + ", decodeBuffer=" + decodeBuffer.length + "]";
    }

    private static void copyBytes(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) {

        System.arraycopy(src, srcOffset, dst, dstOffset, length);
    }
}
