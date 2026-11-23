package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

class ReadHeapExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();
    private final IHeap heap = new MyHeap();

    @Test
    void readsTheValueAtTheAddress() throws MyException {
        int a = heap.allocate(new IntValue(20));
        symTable.put("v", new RefValue(a, new IntType()));
        assertEquals(new IntValue(20), new ReadHeapExp(new VarExp("v")).eval(symTable, heap));
    }

    @Test
    void nestedReadFollowsTwoReferences() throws MyException {
        int a1 = heap.allocate(new IntValue(20));
        int a2 = heap.allocate(new RefValue(a1, new IntType()));
        symTable.put("a", new RefValue(a2, new RefType(new IntType())));
        var exp = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));
        assertEquals(new IntValue(20), exp.eval(symTable, heap));
        assertEquals("rH(rH(a))", exp.toString());
    }

    @Test
    void nonReferenceThrows() {
        var exp = new ReadHeapExp(new ValueExp(new IntValue(3)));
        var ex = assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
        assertEquals("rH: 3 is not a reference", ex.getMessage());
    }

    @Test
    void addressNotInTheHeapThrows() {
        symTable.put("v", new RefValue(1, new IntType()));
        var ex = assertThrows(ExpressionException.class, () -> new ReadHeapExp(new VarExp("v")).eval(symTable, heap));
        assertEquals("rH: address 1 is not in the heap", ex.getMessage());
    }
}