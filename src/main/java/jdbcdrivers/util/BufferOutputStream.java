package jdbcdrivers.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;

/**
 * An {@link OutputStream} that write to a byte array, and allows for direct access to the byte array itself, for avoiding unnecessary copying.
 */
public class BufferOutputStream extends OutputStream {

    private byte[] outputBuffer;
    private ByteBuffer outputByteBuffer;
    private int numBytes;

    /**
     * Construct a new {@link BufferOutputStream}. Also maintains a {@link ByteBuffer}, useful when decoding strings with {@link CharsetDecoder}.
     */
    public BufferOutputStream() {
        this(1024);
    }

    /**
     * Construct a new {@link BufferOutputStream} with the supplied initial capacity.
     *
     * @param initialCapacity the initial capacity of the byte array
     *
     * @throws IllegalArgumentException if {@code initialCapacity} is less than or equal to {@code 0}
     */
    public BufferOutputStream(int initialCapacity) {

        if (initialCapacity <= 0) {

            throw new IllegalArgumentException();
        }

        this.outputBuffer = new byte[initialCapacity];
        this.outputByteBuffer = ByteBuffer.wrap(outputBuffer);
        this.numBytes = 0;
    }

    @Override
    public final void write(int b) throws IOException {

        final int requiredBytes = numBytes + 1;

        if (requiredBytes > outputBuffer.length) {

            resize(requiredBytes);
        }

        outputBuffer[numBytes ++] = DriverUtil.unsignedIntToByte(b);
    }

    @Override
    public final void write(byte[] b) throws IOException {

        write(b, 0, b.length);
    }

    @Override
    public final void write(byte[] b, int off, int len) throws IOException {

        final int requiredBytes = numBytes + len;

        if (requiredBytes > outputBuffer.length) {

            resize(requiredBytes);
        }

        System.arraycopy(b, off, outputBuffer, numBytes, len);

        numBytes += len;
    }

    /**
     * Get the number of bytes added to the byte array.
     *
     * @return number of bytes added
     */
    public final int getNumBytes() {
        return numBytes;
    }

    /**
     * Get the byte array added to, without copying.
     *
     * @return the {@link BufferedOutputStream} byte array
     */
    public final byte[] getOutputBuffer() {
        return outputBuffer;
    }

    /**
     * Reset the byte array write offset to {@code 0}
     */
    public final void reset() {

        this.numBytes = 0;

        outputByteBuffer.position(0);
    }

    /**
     * Get the byte array {@link ByteBuffer}.
     *
     * @return a {@link ByteBuffer} wrapping the byte array
     */
    public final ByteBuffer getOutputByteBuffer() {

        outputByteBuffer.position(0);
        outputByteBuffer.limit(numBytes);

        return outputByteBuffer;
    }

    private void resize(int requiredBytes) {

        if (requiredBytes <= outputBuffer.length) {

            throw new IllegalArgumentException();
        }

        final byte[] newBuffer = new byte[requiredBytes * 4];

        System.arraycopy(outputBuffer, 0, newBuffer, 0, numBytes);

        this.outputBuffer = newBuffer;
        this.outputByteBuffer = ByteBuffer.wrap(newBuffer);
    }
}
