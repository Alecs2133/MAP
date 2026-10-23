package interpreter.model.type;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

class TypeTest {
    @Test
    void intDefaultIsZero() {
        assertEquals(new IntValue(0), new IntType().defaultValue());
    }

    @Test
    void boolDefaultIsFalse() {
        assertEquals(new BoolValue(false), new BoolType().defaultValue());
    }

    @Test
    void valuesKnowTheirType() {
        assertEquals(new IntType(), new IntValue(7).getType());
        assertEquals(new BoolType(), new BoolValue(true).getType());
        assertNotEquals(new IntType(), new BoolValue(true).getType());
    }
}
