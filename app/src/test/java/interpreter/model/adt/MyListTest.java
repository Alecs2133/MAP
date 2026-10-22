package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyListTest {
    private IList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void newListHasSizeZero() {
        assertEquals(0, list.size());
    }

    @Test
    void addThenGetKeepsInsertionOrder() throws ADTException {
        list.add(1);
        list.add(2);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    void getOutOfBoundsThrows() {
        list.add(1);
        assertThrows(ADTException.class, () -> list.get(1));
        assertThrows(ADTException.class, () -> list.get(-1));
    }

    @Test
    void toStringShowsElementsInOrder() {
        list.add(1);
        list.add(2);
        assertEquals("{1, 2}", list.toString());
    }
}
