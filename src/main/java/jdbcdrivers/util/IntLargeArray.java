package jdbcdrivers.util;

public final class IntLargeArray extends Chunks<int[]> {

    public IntLargeArray() {
        super(10000, int[][]::new, int[]::new);
    }

    public void add(int[] integers, int offset, int length) {

        if (length < 1) {

            throw new IllegalArgumentException();
        }

        if (offset + length > integers.length) {

            throw new IllegalArgumentException();
        }

        addData(integers, offset, length, IntLargeArray::copyInts);
    }

    public int getValue(long index) {

        final int chunkIndex = getChunkIndex(index);
        final int chunkOffset = getChunkOffset(index);

        final int[] chunk = getChunk(chunkIndex);

//System.out.println("get value " + index + ' ' + chunkIndex + ' ' + chunkOffset + ' ' + Arrays.toString(chunk));

        return chunk[chunkOffset];
    }

    private static void copyInts(int[] src, int srcOffset, int[] dst, int dstOffset, int length) {
/*
System.out.println("copy ints " + src.length + ' ' + srcOffset + ' ' + dst.length + ' ' + dstOffset + ' ' + length + ' ' +
        Arrays.toString(Arrays.copyOfRange(src, srcOffset, srcOffset + length)));
*/
        System.arraycopy(src, srcOffset, dst, dstOffset, length);
    }
}
