package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
