package jdbcdrivers.generic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import jdbcdrivers.BaseTest;
import jdbcdrivers.generic.TimedFreeList.TimedListNode;

public final class TimedFreeListTest extends BaseTest {

    private static final class TestNode extends TimedListNode<TestNode> {

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
    public void testTimedFreeList() {

        final TimedFreeList<TestNode> timedFreeList = new TimedFreeList<>();

        assertThat(timedFreeList.allocate()).isNull();

        final long nowMillis = System.currentTimeMillis();

        final TestNode testNode1 = new TestNode(1);

        timedFreeList.free(testNode1, nowMillis);
        assertThat(timedFreeList.allocate()).isSameAs(testNode1);
        assertThat(timedFreeList.allocate()).isNull();

        final TestNode testNode2 = new TestNode(2);
        final TestNode testNode3 = new TestNode(3);

        timedFreeList.free(testNode1, nowMillis);
        timedFreeList.free(testNode2, nowMillis);
        timedFreeList.free(testNode3, nowMillis);

        assertThat(timedFreeList.allocate()).isSameAs(testNode3);

        timedFreeList.free(testNode3, nowMillis);

        assertThat(timedFreeList.allocate()).isSameAs(testNode3);
        assertThat(timedFreeList.allocate()).isSameAs(testNode2);
        assertThat(timedFreeList.allocate()).isSameAs(testNode1);
        assertThat(timedFreeList.allocate()).isNull();
    }

    @Test
    @Category(UnitTests.class)
    public void testTimedFreeTimedOut() {

        final TimedFreeList<TestNode> timedFreeList = new TimedFreeList<>();

        assertThat(timedFreeList.allocate()).isNull();

        final long nowMillis = System.currentTimeMillis();

        final TestNode testNode1 = new TestNode(1);
        final TestNode testNode2 = new TestNode(2);
        final TestNode testNode3 = new TestNode(3);
        final TestNode testNode4 = new TestNode(4);

        timedFreeList.free(testNode1, nowMillis);
        timedFreeList.free(testNode2, nowMillis - 10);
        timedFreeList.free(testNode3, nowMillis - 20);
        timedFreeList.free(testNode4, nowMillis - 30);

        timedFreeList.freeTimedOut(15L, nowMillis);
        assertThat(timedFreeList.allocate()).isSameAs(testNode2);
        assertThat(timedFreeList.allocate()).isSameAs(testNode1);
        assertThat(timedFreeList.allocate()).isNull();
    }
}
