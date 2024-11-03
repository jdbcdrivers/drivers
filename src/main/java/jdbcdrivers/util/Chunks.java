package jdbcdrivers.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public abstract class Chunks<T> implements PrintDebug {

    private static final boolean DEBUG = Boolean.FALSE;

    private final int chunkSize;
    private final IntFunction<T> createChunk;

    private T[] chunks;
    private int numChunks;
    private long numElements;

    protected Chunks(int chunkSize, IntFunction<T[]> createArray, IntFunction<T> createChunk) {

        if (chunkSize < 1) {

            throw new IllegalArgumentException();
        }

        Objects.requireNonNull(createArray);
        Objects.requireNonNull(createChunk);

        this.chunkSize = chunkSize;
        this.createChunk = createChunk;

        this.chunks = createArray.apply(1);
        this.numChunks = 0;
        this.numElements = 0L;
    }

    public final long getNumElements() {
        return numElements;
    }

    protected interface DataAdder<I, T, E extends Exception> {

        void add(I input, int inputOffset, T chunk, int chunkOffset, int length) throws E;
    }

    protected final <I, E extends Exception> void addData(I input, int offset, int length, DataAdder<I, T, E> dataAdder) throws E {

        Objects.requireNonNull(input);

        if (offset < 0) {

            throw new IllegalArgumentException();
        }

        if (length < 1) {

            throw new IllegalArgumentException();
        }

        int numChunkElements;
        T chunk;

        if (numChunks == 0) {

            chunk = addChunk();

            numChunkElements = 0;
        }
        else {
            chunk = chunks[numChunks - 1];

            numChunkElements = getNumElementsOfLastChunk();
        }

        int totalNumAddedElements = 0;

        do {
            int remainingOfChunk = chunkSize - numChunkElements;

            if (remainingOfChunk < 0) {

                throw new IllegalStateException();
            }
            else if (remainingOfChunk == 0) {

                chunk = addChunk();

                numChunkElements = 0;
                remainingOfChunk = chunkSize;
            }

            final int remainingOfTotal = length - totalNumAddedElements;

            final int numElementsToAddToChunk = Math.min(remainingOfChunk, remainingOfTotal);

            if (DEBUG) {

                println("add to chunk numChunkElements=" + numChunkElements + " remainingOfChunk=" + remainingOfChunk + " remainingOfTotal=" + remainingOfTotal +
                        " numElementsToAddToChunk=" + numElementsToAddToChunk + " chunkSize=" + chunkSize + " numChunks=" + numChunks);
            }

            dataAdder.add(input, offset + totalNumAddedElements, chunk, numChunkElements, numElementsToAddToChunk);

            addToNumElements(numElementsToAddToChunk);

            totalNumAddedElements += numElementsToAddToChunk;
            numChunkElements += numElementsToAddToChunk;
        }
        while (totalNumAddedElements != length);
    }

    private T addChunk() {

        final T chunk = createChunk.apply(chunkSize);

        final int chunksLength = chunks.length;

        if (numChunks == chunksLength) {

            this.chunks = Arrays.copyOf(chunks, chunksLength * 4);
        }

        chunks[numChunks ++] = chunk;

        return chunk;
    }

    private void addToNumElements(long numElements) {

        if (numElements < 1L) {

            throw new IllegalArgumentException();
        }

        this.numElements += numElements;
    }

    protected final int getChunkSize() {
        return chunkSize;
    }

    protected final T getChunk(int chunkIndex) {

        if (chunkIndex < 0) {

            throw new IllegalArgumentException();
        }

        if (chunkIndex >= numChunks) {

            throw new IllegalArgumentException();
        }

        return chunks[chunkIndex];
    }

    private List<T> getUnmodifiableChunks() {

        return Arrays.stream(chunks, 0, numChunks)
                .collect(Collectors.toUnmodifiableList());
    }

    protected final int getChunkIndex(long offset) {

        final long result = offset / chunkSize;

        if (result > Integer.MAX_VALUE) {

            throw new IllegalStateException();
        }

        return (int)result;
    }

    protected final int getChunkOffset(long offset) {

        return (int)(offset % chunkSize);
    }

    protected final int getNumElementsOfChunk(int chunkIndex) {

        if (chunkIndex < 0) {

            throw new IllegalArgumentException();
        }

        if (chunkIndex >= numChunks) {

            throw new IllegalArgumentException();
        }

        return chunkIndex == numChunks - 1 ? getNumElementsOfLastChunk() : chunkSize;
    }

    private int getNumElementsOfLastChunk() {

        final int remainder = (int)(numElements % chunkSize);

        return remainder == 0 ? chunkSize : remainder;
    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + " [chunkSize=" + chunkSize + ", numChunks=" + numChunks + ", numElements=" + numElements
                + ", chunks=" + Arrays.toString(chunks)+ "]";
    }
}
