package jdbcdrivers.generic;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * State machine helper class.
 */
public abstract class StateObject<STATE, CHECK_STATE_EXCEPTION extends Exception> {

    private static final boolean DEBUG = Boolean.FALSE;

    private final STATE closedState;
    private final Supplier<CHECK_STATE_EXCEPTION> checkStateExceptionSupplier;

    private STATE state;

    protected StateObject() {

        this.closedState = null;
        this.checkStateExceptionSupplier = null;
    }

    protected StateObject(STATE initialState, STATE closedState, Supplier<CHECK_STATE_EXCEPTION> checkStateExceptionSupplier) {

        this.closedState = closedState;
        this.checkStateExceptionSupplier = checkStateExceptionSupplier;

        this.state = Objects.requireNonNull(initialState);
    }

    protected final void checkState(STATE expectedState) throws CHECK_STATE_EXCEPTION {

        Objects.requireNonNull(expectedState);

        checkState(s -> s == expectedState);
    }

    protected final void checkNotState(STATE notExpectedState) throws CHECK_STATE_EXCEPTION {

        Objects.requireNonNull(notExpectedState);

        checkState(s -> s != notExpectedState);
    }

    protected final void checkState(Predicate<STATE> predicate) throws CHECK_STATE_EXCEPTION {

        Objects.requireNonNull(predicate);

        if (DEBUG) {

            System.out.println("checkstate with predicate " + state);
        }

        if (!predicate.test(state)) {

System.out.println("wrong state with predicate " + state);

            throw checkStateExceptionSupplier.get();
        }
    }

    protected final void setState(STATE nextState) {

        Objects.requireNonNull(nextState);

        if (state == nextState) {

            throw new IllegalArgumentException();
        }

        this.state = nextState;
    }

    private void checkNotClosed() throws CHECK_STATE_EXCEPTION {

        checkNotState(closedState);
    }

    @FunctionalInterface
    protected interface StateExecutable<T, E extends Exception> {

        T execute() throws E;
    }

    protected final <T, E extends Exception> T executeForStates(Predicate<STATE> statePredicate, StateExecutable<T, E> stateExecutable) throws CHECK_STATE_EXCEPTION, E {

        Objects.requireNonNull(statePredicate);
        Objects.requireNonNull(stateExecutable);

        checkNotClosed();
        checkState(statePredicate);

        return stateExecutable.execute();
    }

    protected final <T, E extends Exception> T executeWithTemporaryState(STATE temporaryState, StateExecutable<T, E> stateExecutable) throws CHECK_STATE_EXCEPTION, E {

        Objects.requireNonNull(temporaryState);
        Objects.requireNonNull(stateExecutable);

        return executeWithTemporaryStateAndSetNextState(temporaryState, state, s -> true, stateExecutable);
    }

    protected final <T, E extends Exception> T executeWithTemporaryStateAndSetNextState(STATE temporaryState, STATE nextState, StateExecutable<T, E> stateExecutable)
            throws CHECK_STATE_EXCEPTION, E {

        Objects.requireNonNull(temporaryState);
        Objects.requireNonNull(nextState);
        Objects.requireNonNull(stateExecutable);

        return executeWithTemporaryStateAndSetNextState(temporaryState, nextState, s -> true, stateExecutable);
    }

    private <T, E extends Exception> T executeWithTemporaryStateAndSetNextState(STATE temporaryState, STATE nextState, Predicate<STATE> statePredicate,
            StateExecutable<T, E> stateExecutable) throws CHECK_STATE_EXCEPTION, E {

        Objects.requireNonNull(temporaryState);
        Objects.requireNonNull(nextState);
        Objects.requireNonNull(statePredicate);
        Objects.requireNonNull(stateExecutable);

        checkNotClosed();
        checkState(statePredicate);

        setState(temporaryState);

        final T result;

        try {
            result = stateExecutable.execute();
        }
        finally {

            setState(nextState);
        }

        return result;
    }

    protected final <T, E extends Exception> T executeForStates(STATE successState, STATE errorState, Predicate<STATE> statePredicate, StateExecutable<T, E> stateExecutable)
            throws CHECK_STATE_EXCEPTION, E {

        Objects.requireNonNull(successState);
        Objects.requireNonNull(errorState);
        Objects.requireNonNull(statePredicate);
        Objects.requireNonNull(stateExecutable);

        checkNotClosed();
        checkState(statePredicate);

        boolean ok = false;

        final T result;

        try {
            result = stateExecutable.execute();

            ok = true;
        }
        finally {

            setState(ok ? successState : errorState);
        }

        return result;
    }
}
