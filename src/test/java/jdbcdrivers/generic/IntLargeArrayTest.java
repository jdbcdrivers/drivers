package jdbcdrivers.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import jdbcdrivers.BaseTest;
import jdbcdrivers.util.IntLargeArray;
import jdbcdrivers.util.PrintDebug;

public final class IntLargeArrayTest extends BaseTest implements PrintDebug {

    private static final boolean DEBUG = Boolean.FALSE;

    @Test
    @Category(UnitTests.class)
    public void testAddParameterValidation() {

        final IntLargeArray intLargeArray = new IntLargeArray();

        final int intArrayLength = 10;

        final int[] intArray = new int[intArrayLength];

        assertThatThrownBy(() -> intLargeArray.add(null, 0, 1)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> intLargeArray.add(intArray, -1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> intLargeArray.add(intArray, 0, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> intLargeArray.add(intArray, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> intLargeArray.add(intArray, intArrayLength, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> intLargeArray.add(intArray, 0, intArrayLength + 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> intLargeArray.add(intArray, intArrayLength - 2, 3)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> intLargeArray.add(intArray, intArrayLength - 1, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Category(UnitTests.class)
    public void testAdd() {

        final IntLargeArray intLargeArray = new IntLargeArray();

        final int numIntegersPerAdd = 100;
        final int numAddsPerIteration = 10;

        final int intArrayLength = numIntegersPerAdd * numAddsPerIteration;

        final int[] intArray = new int[intArrayLength];

        final int numIterations = 1000;

        final int baseInteger = numIterations * intArrayLength;

        int largeArrayIndex = 0;

        for (int i = 0; i < numIterations; ++ i) {

            for (int j = 0; j < intArrayLength; ++ j) {

                intArray[j] = largeArrayIndex + baseInteger;

                ++ largeArrayIndex;
            }

            for (int offset = 0; offset < intArrayLength; offset += numAddsPerIteration) {

                if (DEBUG) {

                    println("add to array " + offset + ' ' + numAddsPerIteration + ' ' + intArrayLength /* + ' ' + Arrays.toString(intArray) */);
                }

                intLargeArray.add(intArray, offset, numAddsPerIteration);
            }
        }

        assertThat(intLargeArray.getNumElements()).isEqualTo(largeArrayIndex);

        for (int i = 0; i < largeArrayIndex; ++ i) {

            assertThat(intLargeArray.getValue(i)).isEqualTo(i + baseInteger);
        }
    }
}
