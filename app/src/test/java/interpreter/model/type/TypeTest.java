package interpreter.model.type;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;
import interpreter.model.value.StringValue;
import interpreter.model.value.RefValue;

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
    @Test
    void stringDefaultIsEmptyString() {
        assertEquals(new StringValue(""), new StringType().defaultValue());
    }

    @Test
    void equalStringValuesHaveTheSameHashCode() {
        StringValue a = new StringValue("test.in");
        StringValue b = new StringValue("test.in");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new StringValue("other.in"));
        assertEquals(new StringType(), a.getType());
        assertEquals("\"test.in\"", a.toString());
    }

    @Test
    void refDefaultIsNullAddressWithTheInnerType() {
        assertEquals(new RefValue(0, new IntType()), new RefType(new IntType()).defaultValue());
    }

    @Test
    void nestedRefTypesAreComparedRecursively() {
        IType refRefInt = new RefType(new RefType(new IntType()));
        assertEquals(new RefType(new RefType(new IntType())), refRefInt);
        assertNotEquals(new RefType(new IntType()), refRefInt);
        assertNotEquals(new RefType(new RefType(new BoolType())), refRefInt);
        assertEquals("Ref(Ref(int))", refRefInt.toString());
    }

    @Test
    void refValueKnowsItsType() {
        RefValue v = new RefValue(2, new RefType(new IntType()));
        assertEquals(new RefType(new RefType(new IntType())), v.getType());
        assertEquals("(2, Ref(int))", v.toString());
    }
}
