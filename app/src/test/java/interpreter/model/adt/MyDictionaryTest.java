package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;

class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void putThenLookupReturnsValue() throws ADTException {
        dict.put("a", 2);
        assertTrue(dict.isDefined("a"));
        assertEquals(2, dict.lookup("a"));
    }

    @Test
    void updateChangesExistingValue() throws ADTException {
        dict.put("a", 2);
        dict.update("a", 5);
        assertEquals(5, dict.lookup("a"));
    }

    @Test
    void missingKeyIsNotDefined() {
        assertFalse(dict.isDefined("x"));
    }

    @Test
    void lookupOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.lookup("x"));
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.update("x", 1));
    }

    @Test
    void removeDeletesTheKey() throws ADTException {
        dict.put("a", 2);
        dict.remove("a");
        assertFalse(dict.isDefined("a"));
    }

    @Test
    void removeOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.remove("x"));
    }

    @Test
    void toMapIsAReadOnlyCopy() {
        dict.put("a", 2);
        Map<String, Integer> copy = dict.toMap();
        assertEquals(Map.of("a", 2), copy);
        assertThrows(UnsupportedOperationException.class, () -> copy.put("b", 3));
        dict.put("b", 3);
        assertEquals(1, copy.size());
    }
}
