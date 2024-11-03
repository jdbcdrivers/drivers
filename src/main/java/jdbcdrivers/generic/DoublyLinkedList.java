package jdbcdrivers.generic;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;

final class DoublyLinkedList<T extends DoublyLinkedList.DoublyLinkedNode<T>> {

    static abstract class DoublyLinkedNode<T> {

        private T previous;
        private T next;
    }

    private T head;
    private T tail;
    private int numElements;

    DoublyLinkedList() {

        clear();
    }

    boolean isEmpty() {

        return head == null;
    }

    int getNumElements() {
        return numElements;
    }

    void addHead(T node) {

        Objects.requireNonNull(node);

        if (head == null) {

            this.head = node;
            this.tail = node;
        }
        else {
            final T headNode = head;
            final DoublyLinkedNode<T> toAddNode = node;

            ((DoublyLinkedNode<T>)headNode).previous = node;
            toAddNode.next = headNode;
            this.head = node;
        }

        ++ numElements;
    }

    void addTail(T node) {

        Objects.requireNonNull(node);

        if (head == null) {

            this.head = node;
            this.tail = node;
        }
        else {
            final T tailNode = tail;
            final DoublyLinkedNode<T> toAddNode = node;

            ((DoublyLinkedNode<T>)tailNode).next = node;
            toAddNode.previous = tailNode;
            this.tail = node;
        }

        ++ numElements;
    }

    T removeHead() {

        if (head == null) {

            throw new IllegalStateException();
        }

        final T result = head;

        if (result == tail) {

            clear();
        }
        else {
            final DoublyLinkedNode<T> next = ((DoublyLinkedNode<T>)result).next;

            next.previous = null;
            this.head = ((DoublyLinkedNode<T>)result).next;

            -- numElements;
        }

        return result;
    }

    T removeTail() {

        if (head == null) {

            throw new IllegalStateException();
        }

        final T result = tail;

        if (result == head) {

            clear();
        }
        else {
            final T previous = ((DoublyLinkedNode<T>)result).previous;

            ((DoublyLinkedNode<T>)previous).next = null;
            this.tail = previous;

            -- numElements;
        }

        return result;
    }

    <P> void removeTrailing(P parameter, BiPredicate<T, P> predicate) {

        Objects.requireNonNull(predicate);

        boolean foundNonMatching = false;

        int numRemoved = 0;

        for (T node = tail; node != null; node = ((DoublyLinkedNode<T>)node).previous) {

            if (!predicate.test(node, parameter)) {

                ((DoublyLinkedNode<T>)node).next = null;
                this.tail = node;

                foundNonMatching = true;
                break;
            }

            ++ numRemoved;
        }

        if (foundNonMatching) {

            this.numElements -= numRemoved;
        }
        else {
            clear();
        }
    }

    T[] toArray(IntFunction<T[]> createArray) {

        final T[] result = createArray.apply(numElements);

        int dstIndex = 0;

        for (T node = head; node != null; node = ((DoublyLinkedNode<T>)node).next) {

            result[dstIndex ++] = node;
        }

        return result;
    }

    private void clear() {

        this.head = null;
        this.tail = null;
        this.numElements = 0;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();

        boolean first = true;

        for (T node = head; node != null; node = ((DoublyLinkedNode<T>)node).next) {

            if (first) {

                first = false;
            }
            else {
                sb.append(',');
            }

            sb.append(Objects.toString(node));
        }

        return getClass().getSimpleName() + " [numElements=" + numElements + ", list=" + sb.toString() + "]";
    }
}
