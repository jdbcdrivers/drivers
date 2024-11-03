package jdbcdrivers.generic;

import java.util.Objects;

import jdbcdrivers.generic.DoublyLinkedList.DoublyLinkedNode;

final class TimedFreeList<T extends TimedFreeList.TimedListNode<T>> {

    static abstract class TimedListNode<T> extends DoublyLinkedNode<T> {

        private long freedTime;
    }

    private final DoublyLinkedList<T> freeList;

    // To avoid allocating closures, not thread-safe
    private final EvictTimes avoidClosureAllocationEvictTimes;

    TimedFreeList() {

        this.freeList = new DoublyLinkedList<>();
        this.avoidClosureAllocationEvictTimes = new EvictTimes();
    }

    T allocate() {

        return freeList.isEmpty() ? null : freeList.removeTail();
    }

    void free(T node, long nowMillis) {

        Objects.requireNonNull(node);

        ((TimedListNode<T>)node).freedTime = nowMillis;

        freeList.addTail(node);
    }

    void freeTimedOut(long evictDeltaMillis, long nowMillis) {

        if (evictDeltaMillis < 0) {

            throw new IllegalArgumentException();
        }

        if (nowMillis < 0) {

            throw new IllegalArgumentException();
        }

        avoidClosureAllocationEvictTimes.init(evictDeltaMillis, nowMillis);

        freeList.removeTrailing(avoidClosureAllocationEvictTimes, (n, p) -> p.shouldEvict(((TimedListNode<T>)n).freedTime));
    }

    static final class EvictTimes {

        private long evictDeltaMillis;
        private long nowMillis;

        void init(long evictDeltaMillis, long nowMillis) {

            this.evictDeltaMillis = evictDeltaMillis;
            this.nowMillis = nowMillis;
        }

        boolean shouldEvict(long freedTime) {

            final long timeSinceFreed = nowMillis - freedTime;

            if (timeSinceFreed < 0) {

                throw new IllegalStateException();
            }

            return timeSinceFreed > evictDeltaMillis;
        }
    }
}
