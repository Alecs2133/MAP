package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

import java.util.Map;

class MyHeapTest {
    private final IHeap heap = new MyHeap();

    @Test
    void allocateReturnsConsecutiveAddressesStartingAtOne() {
        assertEquals(1, heap.allocate(new IntValue(20)));
        assertEquals(2, heap.allocate(new RefValue(1, new IntType())));
        assertEquals(3, heap.allocate(new IntValue(30)));
        assertEquals(new IntValue(20), assertDoesNotThrow(() -> heap.get(1)));
    }

    @Test
    void updateChangesTheValue() throws ADTException {
        heap.allocate(new IntValue(20));
        heap.update(1, new IntValue(30));
        assertEquals(new IntValue(30), heap.get(1));
    }

    @Test
    void getOfAMissingAddressThrows() {
        var ex = assertThrows(ADTException.class, () -> heap.get(1));
        assertEquals("address 1 is not in the heap", ex.getMessage());
    }

    @Test
    void updateOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> heap.update(5, new IntValue(1)));
    }

    @Test
    void addressesAreNotReusedAfterSetContent() {
        heap.allocate(new IntValue(1));
        heap.allocate(new IntValue(2));
        heap.setContent(Map.of(1, new IntValue(1)));
        assertFalse(heap.isDefined(2));
        assertEquals(3, heap.allocate(new IntValue(3)));
    }

    @Test
    void getContentIsAReadOnlyCopy() {
        heap.allocate(new IntValue(1));
        assertThrows(UnsupportedOperationException.class,
                () -> heap.getContent().put(9, new IntValue(9)));
    }

    @Test
    void toStringIsSortedByAddress() {
        heap.allocate(new IntValue(20));
        heap.allocate(new RefValue(1, new IntType()));
        assertEquals("{1 -> 20, 2 -> (1, int)}", heap.toString());
    }
}