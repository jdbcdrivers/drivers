package jdbcdrivers.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import jdbcdrivers.BaseTest;
import jdbcdrivers.util.PrintDebug;

public final class ResultSetBufferTest extends BaseTest implements PrintDebug {

    private static final boolean DEBUG = Boolean.FALSE;

    @Test
    @Category(UnitTests.class)
    public void testResultSetBufferNegativeChunkSize() throws IOException {

        for (int chunkSize = -1; chunkSize >= -10; -- chunkSize) {

            assertThatThrownBy(() -> new ResultSetBuffer(0))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testResultSetBufferZeroChunkSize() throws IOException {

        assertThatThrownBy(() -> new ResultSetBuffer(0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Category(UnitTests.class)
    public void testResultSetBuffer() throws IOException {

        for (int chunkSize = 1; chunkSize <= 10; ++ chunkSize) {

            checkResultSetBuffer(chunkSize);
        }
    }

    private void checkResultSetBuffer(int chunkSize) throws IOException {

        final int maxChunks = 10;

        for (int numChunks = 1; numChunks < maxChunks; ++ numChunks) {

            checkResultSetBuffer(numChunks, chunkSize);
        }
    }

    private void checkResultSetBuffer(int numChunks, int chunkSize) throws IOException {

        final int totalNumBytes = numChunks * chunkSize;

        assertThatThrownBy(() -> checkResultSetBuffer(numChunks, chunkSize, 0, 1))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> checkResultSetBuffer(numChunks, chunkSize, 1, 0))
            .isInstanceOf(IllegalArgumentException.class);

        for (int numBytesToAddPerIteration = 1; numBytesToAddPerIteration <= totalNumBytes; ++ numBytesToAddPerIteration) {

            for (int numBytesToDecodePerIteration = 1; numBytesToDecodePerIteration <= chunkSize; ++ numBytesToDecodePerIteration) {

                checkResultSetBuffer(numChunks, chunkSize, numBytesToAddPerIteration, numBytesToDecodePerIteration);
            }

            final int aboveChunkSize = chunkSize * 2;

            final int closureToAdd = numBytesToAddPerIteration;

            for (int toDecode = chunkSize + 1; toDecode <= aboveChunkSize; ++ toDecode) {

                checkResultSetBufferToDecodeAboveChunkSize(numChunks, chunkSize, closureToAdd, toDecode);
            }
        }
    }

    private void checkResultSetBuffer(int numChunks, int chunkSize, int numBytesToAddPerIteration, int numBytesToDecodePerIteration) throws IOException {

        assertThat(numBytesToDecodePerIteration).isLessThanOrEqualTo(chunkSize);

        final int totalNumBytes = numChunks * chunkSize;

        final ResultSetBuffer resultSetBuffer = addData(chunkSize, numBytesToAddPerIteration, totalNumBytes);

        final byte[] decodeBuffer = new byte[numBytesToDecodePerIteration];
        final byte[] expectedBytes = new byte[numBytesToDecodePerIteration];

        final int closureDecodeLength = numBytesToDecodePerIteration;

        for (int decodeOffset = 0; decodeOffset < totalNumBytes; decodeOffset += numBytesToDecodePerIteration) {

            final int numBytes = Math.min(totalNumBytes - decodeOffset, numBytesToDecodePerIteration);

            if (DEBUG) {

                println("decode offset numChunks=" + numChunks + " chunkSize=" + chunkSize + " numBytesToAddPerIteration=" + numBytesToAddPerIteration +
                        " numBytesToDecodePerIteration=" + numBytesToDecodePerIteration + " decodeOffset=" + decodeOffset + " numBytes=" + numBytes + " totalNumBytes=" + totalNumBytes +
                        " resultSetBuffer=" + resultSetBuffer);
            }

            resultSetBuffer.decode(decodeOffset, numBytes, (d, b, o, l) -> System.arraycopy(d, o, decodeBuffer, 0, l));

            fillBytes(expectedBytes, decodeOffset, numBytes);

            assertThat(decodeBuffer).isEqualTo(expectedBytes);
        }

        final int aboveTotalNumBytes = totalNumBytes * 2;

        for (int decodeOffset = totalNumBytes - numBytesToDecodePerIteration + 1; decodeOffset < aboveTotalNumBytes; decodeOffset += numBytesToDecodePerIteration) {

            final int closureDecodeOffset = decodeOffset;

            if (DEBUG) {

                println("exception decodeOffset=" + decodeOffset + " totalNumBytes=" + totalNumBytes + " resultSetBuffer=" + resultSetBuffer);
            }

            assertThatThrownBy(() -> resultSetBuffer.decode(closureDecodeOffset, closureDecodeLength, (d, b, o, l) -> System.arraycopy(d, o, decodeBuffer, 0, l)))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private void checkResultSetBufferToDecodeAboveChunkSize(int numChunks, int chunkSize, int numBytesToAddPerIteration, int numBytesToDecodePerIteration) throws IOException {

        assertThat(numBytesToDecodePerIteration).isGreaterThan(chunkSize);

        final int totalNumBytes = numChunks * chunkSize;

        final ResultSetBuffer resultSetBuffer = addData(chunkSize, numBytesToAddPerIteration, totalNumBytes);

        final byte[] decodeBuffer = new byte[numBytesToDecodePerIteration];

        for (int decodeOffset = 0; decodeOffset < totalNumBytes; decodeOffset += numBytesToDecodePerIteration) {

            final int closureDecodeOffset = decodeOffset;

            if (DEBUG) {

                println("exception above decodeOffset=" + decodeOffset + " numBytesToDecodePerIteration=" + numBytesToDecodePerIteration + " totalNumBytes=" + totalNumBytes +
                        " resultSetBuffer=" + resultSetBuffer);
            }

            assertThatThrownBy(() -> resultSetBuffer.decode(closureDecodeOffset, numBytesToDecodePerIteration, (d, b, o, l) -> System.arraycopy(d, o, decodeBuffer, 0, l)))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private ResultSetBuffer addData(int chunkSize, int numBytesToAddPerIteration, int totalNumBytes) throws IOException {

        final ResultSetBuffer resultSetBuffer = new ResultSetBuffer(chunkSize);

        addData(resultSetBuffer, numBytesToAddPerIteration, totalNumBytes);

        return resultSetBuffer;
    }

    private void addData(ResultSetBuffer resultSetBuffer, int numBytesToAddPerIteration, int totalNumBytes) throws IOException {

        Objects.requireNonNull(resultSetBuffer);

        if (numBytesToAddPerIteration < 1) {

            throw new IllegalArgumentException();
        }

        if (totalNumBytes < numBytesToAddPerIteration) {

            throw new IllegalArgumentException();
        }

        if (DEBUG) {

            println("add data numBytesToAddPerIteration=" + numBytesToAddPerIteration + " totalNumBytes=" + totalNumBytes + " resultSetBuffer=" + resultSetBuffer);
        }

        final byte[] bytesToAdd = new byte[numBytesToAddPerIteration];

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesToAdd);

        for (int addOffset = 0; addOffset < totalNumBytes; addOffset += numBytesToAddPerIteration) {

            final int numBytes = Math.min(totalNumBytes - addOffset, numBytesToAddPerIteration);

            fillBytes(bytesToAdd, addOffset, numBytes);

            resultSetBuffer.addData(byteArrayInputStream, numBytes);

            byteArrayInputStream.reset();
        }

        if (DEBUG) {

            println("added data resultSetBuffer=" + resultSetBuffer);
        }
    }

    private static void fillBytes(byte[] dst, int offset, int length) {

        for (int i = 0; i < length; ++ i) {

            final int totalOffset = offset + i;

            final byte b = (byte)((totalOffset + 10) & 0x000000FF);

            dst[i] = b;
        }
    }
}
