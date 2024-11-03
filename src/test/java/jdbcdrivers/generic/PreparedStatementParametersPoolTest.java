package jdbcdrivers.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import jdbcdrivers.BaseTest;

public final class PreparedStatementParametersPoolTest extends BaseTest {

    @Test
    @Category(UnitTests.class)
    public void testParametersPoolZeroNumParametersThrowsException() {

        runForParameters((p, n, c) -> {

            assertThatThrownBy(() -> p.allocateParameters(-1)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> p.allocateParameters(0)).isInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    @Category(UnitTests.class)
    public void testParametersPool() {

        runForParameters((parametersPool, numParameters, expectCached) -> checkParametersPool(parametersPool, numParameters, expectCached,
                (p, n) -> new GenericPreparedStatementParameters(n)));
    }

    @Test
    @Category(UnitTests.class)
    public void testParametersWithAllocate() {

        runForParameters((parametersPool, numParameters, expectCached) -> checkParametersPool(parametersPool, numParameters, expectCached,
                (p, n) -> p.allocateParameters(n)));
    }

    @FunctionalInterface
    private interface PreparedStatementParametersAllocator {

        GenericPreparedStatementParameters allocate(PreparedStatementParametersPool parametersPool, int numParameters);
    }

    private static void checkParametersPool(PreparedStatementParametersPool parametersPool, int numParameters, boolean expectCached,
            PreparedStatementParametersAllocator allocator) {

        final long nowMillis = System.currentTimeMillis();

        final GenericPreparedStatementParameters parameters1 = allocator.allocate(parametersPool, numParameters);

        parametersPool.freeParameters(parameters1, nowMillis);

        checkAllocateParameters(parametersPool, numParameters, parameters1, expectCached);

        final GenericPreparedStatementParameters parameters2 = new GenericPreparedStatementParameters(numParameters);
        final GenericPreparedStatementParameters parameters3 = new GenericPreparedStatementParameters(numParameters);

        parametersPool.freeParameters(parameters1, nowMillis);
        parametersPool.freeParameters(parameters2, nowMillis);
        parametersPool.freeParameters(parameters3, nowMillis);

        checkAllocateParameters(parametersPool, numParameters, parameters3, expectCached);

        parametersPool.freeParameters(parameters3, nowMillis);

        checkAllocateParameters(parametersPool, numParameters, parameters3, expectCached);
        checkAllocateParameters(parametersPool, numParameters, parameters2, expectCached);
        checkAllocateParameters(parametersPool, numParameters, parameters1, expectCached);
    }

    private static void checkAllocateParameters(PreparedStatementParametersPool parametersPool, int numParameters, GenericPreparedStatementParameters expectedParameters,
            boolean expectCached) {

        Objects.requireNonNull(parametersPool);

        if (numParameters < 1) {

            throw new IllegalArgumentException();
        }

        Objects.requireNonNull(expectedParameters);

        final GenericPreparedStatementParameters allocated = parametersPool.allocateParameters(numParameters);

        assertThat(allocated).isNotNull();
        assertThat(allocated == expectedParameters).isEqualTo(expectCached);
    }

    @Test
    @Category(UnitTests.class)
    public void testTimedFreeTimedOut() {

        final long evictDeltaMillis = 15000L;

        runForParameters(evictDeltaMillis, (p, n, c) -> checkParameterPoolTimedOut(evictDeltaMillis, p, n, c));
    }

    private static void checkParameterPoolTimedOut(long evictDeltaMillis, PreparedStatementParametersPool parametersPool, int numParameters, boolean expectCached) {

        final long nowMillis = System.currentTimeMillis();

        final GenericPreparedStatementParameters parameters1 = new GenericPreparedStatementParameters(numParameters);
        final GenericPreparedStatementParameters parameters2 = new GenericPreparedStatementParameters(numParameters);
        final GenericPreparedStatementParameters parameters3 = new GenericPreparedStatementParameters(numParameters);
        final GenericPreparedStatementParameters parameters4 = new GenericPreparedStatementParameters(numParameters);

        parametersPool.freeParameters(parameters4, nowMillis - (evictDeltaMillis * 4), nowMillis);
        parametersPool.freeParameters(parameters3, nowMillis - (evictDeltaMillis * 3), nowMillis);
        parametersPool.freeParameters(parameters2, nowMillis - (evictDeltaMillis * 2), nowMillis);
        parametersPool.freeParameters(parameters1, nowMillis - (evictDeltaMillis * 1), nowMillis);

        final GenericPreparedStatementParameters allocated = parametersPool.allocateParameters(numParameters);

        assertThat(allocated).isNotNull();
        assertThat(allocated == parameters1).isEqualTo(expectCached);
        assertThat(allocated).isNotSameAs(parameters2);
        assertThat(allocated).isNotSameAs(parameters3);
        assertThat(allocated).isNotSameAs(parameters4);
    }

    @Test
    @Category(UnitTests.class)
    public void testAllocateCopy() {

        runForParameters((p, n, c) -> checkAllocateCopy(p, n));
    }

    private static void checkAllocateCopy(PreparedStatementParametersPool parametersPool, int numParameters) {

        final GenericPreparedStatementParameters parameters = new GenericPreparedStatementParameters(numParameters);

        final int offset = numParameters + 1;

        final int[] intArray = new int[numParameters];

        for (int i = 0; i < numParameters; ++ i) {

            intArray[i] = offset + i;
        }

        for (int i = 0; i < numParameters; ++ i) {

            parameters.setInt(i, intArray[i]);
        }

        final GenericPreparedStatementParameters parametersCopy = parametersPool.allocateCopy(parameters);

        assertThat(parametersCopy).isNotSameAs(parameters);

        for (int i = 0; i < numParameters; ++ i) {

            assertThat(parameters.getInt(i)).isEqualTo(intArray[i]);
        }
    }

    @FunctionalInterface
    private interface ParametersPoolConsumer {

        void accept(PreparedStatementParametersPool parametersPool, int numParameters, boolean expectCached);
    }

    private static void runForParameters(ParametersPoolConsumer consumer) {

        runForParameters(1000L, consumer);
    }

    private static void runForParameters(long evictDeltaMillis, ParametersPoolConsumer consumer) {

        final int maxCachedNumParameters = PreparedStatementParametersPool.MAX_CACHED_NUM_PARAMETERS;

        final PreparedStatementParametersPool parametersPool = new PreparedStatementParametersPool(evictDeltaMillis);

        for (int numParameters = 1; numParameters <= maxCachedNumParameters; ++ numParameters) {

            consumer.accept(parametersPool, numParameters, true);
        }

        final int maxNumParameters = maxCachedNumParameters * 2;

        for (int numParameters = maxCachedNumParameters + 1; numParameters <= maxNumParameters; ++ numParameters) {

            consumer.accept(parametersPool, numParameters, false);
        }
    }
}
