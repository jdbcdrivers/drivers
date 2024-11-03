package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.util.PrintDebug;

final class PreparedStatementParametersPool implements PrintDebug {

    private static final boolean DEBUG = Boolean.FALSE;

    private static final long MIN_EVICT_DELTA_MILLIS = 1000;
    private static final long MAX_EVICT_DELTA_MILLIS = 24 * 60 * 60 * 1000;

    private static final int NUM_ARRAY_ELEMENTS = 30;
    static final int MAX_CACHED_NUM_PARAMETERS = NUM_ARRAY_ELEMENTS;

    private final long evictDeltaMillis;

    private final TimedFreeList<GenericPreparedStatementParameters>[] cachedByNumParametersPlusOne;

    PreparedStatementParametersPool(long evictDeltaMillis) {

        if (evictDeltaMillis < MIN_EVICT_DELTA_MILLIS || evictDeltaMillis > MAX_EVICT_DELTA_MILLIS) {

            throw new IllegalArgumentException();
        }

        this.evictDeltaMillis = evictDeltaMillis;
        this.cachedByNumParametersPlusOne = allocateTimedFreeLists(NUM_ARRAY_ELEMENTS);
    }

    @SuppressWarnings("unchecked")
    private static TimedFreeList<GenericPreparedStatementParameters>[] allocateTimedFreeLists(int numArrayElements) {

        return new TimedFreeList[numArrayElements];
    }

    GenericPreparedStatementParameters allocateParameters(int numParameters) {

        if (numParameters <= 0) {

            throw new IllegalArgumentException();
        }

        if (DEBUG) {

            println("allocate parameters " + numParameters);
        }

        GenericPreparedStatementParameters result;

        final int index = numParameters - 1;

        if (index >= NUM_ARRAY_ELEMENTS) {

            if (DEBUG) {

                println("> num array elements " + numParameters + ' ' + NUM_ARRAY_ELEMENTS);
            }

            result = new GenericPreparedStatementParameters(numParameters);
        }
        else {
            TimedFreeList<GenericPreparedStatementParameters> preparedStatementParametersFreeList = cachedByNumParametersPlusOne[index];

            if (DEBUG) {

                println("free list " + preparedStatementParametersFreeList);
            }

            if (preparedStatementParametersFreeList != null) {

                result = preparedStatementParametersFreeList.allocate();

                if (DEBUG) {

                    println("free list result " + result);
                }

                if (result == null) {

                    result = new GenericPreparedStatementParameters(numParameters);
                }
            }
            else {
                result = new GenericPreparedStatementParameters(numParameters);
            }
        }

        return result;
    }


    GenericPreparedStatementParameters allocateCopy(GenericPreparedStatementParameters toCopy) {

        final GenericPreparedStatementParameters result = allocateParameters(toCopy.getNumParameters());

        result.copy(toCopy);

        return result;
    }

    void freeParameters(GenericPreparedStatementParameters preparedStatementParameters, long nowMillis) {

        freeParameters(preparedStatementParameters, nowMillis, nowMillis);
    }

    void freeParameters(GenericPreparedStatementParameters preparedStatementParameters, long freedMillis, long nowMillis) {

        Objects.requireNonNull(preparedStatementParameters);

        final int numParameters = preparedStatementParameters.getNumParameters();

        final int index = numParameters - 1;

        if (index < NUM_ARRAY_ELEMENTS) {

            TimedFreeList<GenericPreparedStatementParameters> preparedStatementParametersFreeList = cachedByNumParametersPlusOne[index];

            if (preparedStatementParametersFreeList == null) {

                preparedStatementParametersFreeList = new TimedFreeList<>();
                cachedByNumParametersPlusOne[index] = preparedStatementParametersFreeList;
            }

            freeTimedOut(nowMillis);

            preparedStatementParametersFreeList.free(preparedStatementParameters, freedMillis);
        }
    }

    private void freeTimedOut(long nowMillis) {

        for (TimedFreeList<GenericPreparedStatementParameters> preparedStatementParametersFreeList : cachedByNumParametersPlusOne) {

            if (preparedStatementParametersFreeList != null) {

                preparedStatementParametersFreeList.freeTimedOut(evictDeltaMillis, nowMillis);
            }
        }
    }
}
