package jdbcdrivers.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import jdbcdrivers.BaseTest;
import jdbcdrivers.generic.DoublyLinkedList.DoublyLinkedNode;

public final class DoublyLinkedListTest extends BaseTest {

    private static final class TestNode extends DoublyLinkedNode<TestNode> {

        private final int value;

        TestNode(int value) {

            this.value = value;
        }

        @Override
        public String toString() {

            return String.valueOf(value);
        }
    }

    @Test
    @Category(UnitTests.class)
    public void testIsEmpty() {

        final DoublyLinkedList<TestNode> list = new DoublyLinkedList<>();

        assertThat(list.isEmpty()).isTrue();

        addTail(list, 1);
        assertThat(list.isEmpty()).isFalse();

        addTail(list, 2);
        assertThat(list.isEmpty()).isFalse();

        addTail(list, 3);
        assertThat(list.isEmpty()).isFalse();

        list.removeTail();
        assertThat(list.isEmpty()).isFalse();

        list.removeHead();
        assertThat(list.isEmpty()).isFalse();

        list.removeTail();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    @Category(UnitTests.class)
    public void testGetNumElements() {

        final DoublyLinkedList<TestNode> list = new DoublyLinkedList<>();

        assertThat(list.getNumElements()).isEqualTo(0);

        addTail(list, 1);
        assertThat(list.getNumElements()).isEqualTo(1);

        addTail(list, 2);
        assertThat(list.getNumElements()).isEqualTo(2);

        addTail(list, 3);
        assertThat(list.getNumElements()).isEqualTo(3);

        list.removeTail();
        assertThat(list.getNumElements()).isEqualTo(2);

        list.removeHead();
        assertThat(list.getNumElements()).isEqualTo(1);

        list.removeTail();
        assertThat(list.getNumElements()).isEqualTo(0);
    }

    @Test
    @Category(UnitTests.class)
    public void testAddHead() {

        final DoublyLinkedList<TestNode> list = new DoublyLinkedList<>();

        checkList(list);

        checkAddHead(list);
        checkAddHead(list);
    }

    private void checkAddHead(DoublyLinkedList<TestNode> list) {

        addHead(list, 1);
        checkList(list, 1);

        addHead(list, 2);
        checkList(list, 2, 1);

        addHead(list, 3);
        checkList(list, 3, 2, 1);

        removeThree(list);
    }

    @Test
    @Category(UnitTests.class)
    public void testAddTail() {

        final DoublyLinkedList<TestNode> list = new DoublyLinkedList<>();

        checkList(list);

        checkAddTail(list);
        checkAddTail(list);
    }

    private void checkAddTail(DoublyLinkedList<TestNode> list) {

        addTail(list, 1);
        checkList(list, 1);

        addTail(list, 2);
        checkList(list, 1, 2);

        addTail(list, 3);
        checkList(list, 1, 2, 3);

        removeThree(list);
    }

    private void removeThree(DoublyLinkedList<TestNode> list) {

        assertThat(list.getNumElements()).isEqualTo(3);

        list.removeHead();
        list.removeTail();
        list.removeHead();

        assertThat(list.isEmpty());
        assertThat(list.getNumElements()).isEqualTo(0);
    }

    @Test
    @Category(UnitTests.class)
    public void testRemoveHead() {

        final DoublyLinkedList<TestNode> list = new DoublyLinkedList<>();

        assertThatThrownBy(() -> list.removeHead()).isInstanceOf(IllegalStateException.class);

        checkRemoveHead(list);
        checkRemoveHead(list);
    }

    private static void checkRemoveHead(DoublyLinkedList<TestNode> list) {

        addTail(list, 1, 2, 3);
        checkList(list, 1, 2, 3);

        list.removeHead();
        checkList(list, 2, 3);

        list.removeHead();
        checkList(list, 3);

        list.removeHead();
        checkList(list);
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.getNumElements()).isEqualTo(0);

        assertThatThrownBy(() -> list.removeHead()).isInstanceOf(IllegalStateException.class);
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.getNumElements()).isEqualTo(0);
    }

    @Test
    @Category(UnitTests.class)
    public void testRemoveTail() {

        final DoublyLinkedList<TestNode> list = new DoublyLinkedList<>();

        assertThatThrownBy(() -> list.removeTail()).isInstanceOf(IllegalStateException.class);

        checkRemoveTail(list);
        checkRemoveTail(list);
    }

    private static void checkRemoveTail(DoublyLinkedList<TestNode> list) {

        addTail(list, 1, 2, 3);
        checkList(list, 1, 2, 3);

        list.removeTail();
        checkList(list, 1, 2);

        list.removeTail();
        checkList(list, 1);

        list.removeTail();
        checkList(list);
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.getNumElements()).isEqualTo(0);

        assertThatThrownBy(() -> list.removeTail()).isInstanceOf(IllegalStateException.class);
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.getNumElements()).isEqualTo(0);
    }

    @Test
    @Category(UnitTests.class)
    public void testRemoveTrailing() {

        DoublyLinkedList<TestNode> list;

        // Create new list for ever test
        list = addAndRemoveTrailing(n -> n.value > 3, 1, 2, 3);
        checkList(list, 1, 2, 3);

        list = addAndRemoveTrailing(n -> n.value > 2, 1, 2, 3);
        checkList(list, 1, 2);

        list = addAndRemoveTrailing(n -> n.value > 1, 1, 2, 3);
        checkList(list, 1);

        list = addAndRemoveTrailing(n -> n.value > 0, 1, 2, 3);
        checkList(list);

        // Add to same list
        list = new DoublyLinkedList<>();

        addAndRemoveTrailing(list, n -> n.value > 3, 1, 2, 3);
        checkList(list, 1, 2, 3);

        addAndRemoveTrailing(list, n -> n.value > 2, 1, 2, 3);
        checkList(list, 1, 2, 3, 1, 2);

        addAndRemoveTrailing(list, n -> n.value > 1, 1, 2, 3);
        checkList(list, 1, 2, 3, 1, 2, 1);

        addAndRemoveTrailing(list, n -> n.value > 0, 1, 2, 3);
        checkList(list);
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.getNumElements()).isEqualTo(0);

        addAndRemoveTrailing(list, n -> n.value > 1, 1, 2, 3);
        checkList(list, 1);

        // Remove so that only one left, then re-add
        list = addAndRemoveTrailing(n -> n.value > 1, 1, 2, 3);
        checkList(list, 1);

        addHead(list, 1, 2, 3);
        list.removeTrailing(null, (n, p) -> n.value > 2);
        checkList(list, 3, 2, 1, 1);

        addAndRemoveTrailing(list, n -> n.value > 0, 1, 2, 3);
        checkList(list);
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.getNumElements()).isEqualTo(0);

        list = addAndRemoveTrailing(n -> n.value > 1, 1, 2, 3);
        checkList(list, 1);

        addAndRemoveTrailing(list, n -> n.value > 2, 1, 2, 3);
        checkList(list, 1, 1, 2);

        addAndRemoveTrailing(list, n -> n.value > 0, 1, 2, 3);
        checkList(list);
        assertThat(list.isEmpty()).isTrue();
        assertThat(list.getNumElements()).isEqualTo(0);
    }

    private static DoublyLinkedList<TestNode> addAndRemoveTrailing(Predicate<TestNode> predicate, int ... values) {

        final DoublyLinkedList<TestNode> list = new DoublyLinkedList<>();

        addAndRemoveTrailing(list, predicate, values);

        return list;
    }

    private static void addAndRemoveTrailing(DoublyLinkedList<TestNode> list, Predicate<TestNode> predicate, int ... values) {

        addTail(list, values);

        list.removeTrailing(null, (n, p) -> predicate.test(n));
    }

    private static void addHead(DoublyLinkedList<TestNode> list, int ... values) {

        addTestNodes(list::addHead, values);
    }

    private static void addTail(DoublyLinkedList<TestNode> list, int ... values) {

        addTestNodes(list::addTail, values);
    }

    private static void addTestNodes(Consumer<TestNode> consumer, int ... values) {

        for (int value : values) {

            consumer.accept(new TestNode(value));
        }
    }

    private static void checkList(DoublyLinkedList<TestNode> list, int ... values) {

        final int expectedNumElements = values.length;

        assertThat(list.isEmpty()).isEqualTo(expectedNumElements == 0);
        assertThat(list.getNumElements()).isEqualTo(expectedNumElements);

        final int[] actualValues = Arrays.stream(list.toArray(TestNode[]::new))
                .mapToInt(n -> n.value)
                .toArray();

        assertThat(actualValues).isEqualTo(values);
    }
}
