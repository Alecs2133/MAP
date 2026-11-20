package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyHeap;

class LogicExpTest {
    private final IHeap heap = new MyHeap();
    private IDictionary<String, IValue> symTable;

    private static IExp bool(boolean b) { return new ValueExp(new BoolValue(b)); }

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void andIsTrueOnlyWhenBothAreTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.AND, bool(true), bool(true)).eval(symTable, heap));
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(true), bool(false)).eval(symTable, heap));
    }

    @Test
    void orIsTrueWhenOneIsTrue() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(false), bool(true)).eval(symTable, heap));
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.OR, bool(false), bool(false)).eval(symTable, heap));
    }

    @Test
    void nonBooleanFirstOperandThrows() {
        IExp e = new LogicExp(LogicOp.AND, new ValueExp(new IntValue(1)), bool(true));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable, heap));
        assertEquals("first operand is not a boolean", ex.getMessage());
    }

    @Test
    void nonBooleanSecondOperandThrows() {
        IExp e = new LogicExp(LogicOp.OR, bool(true), new ValueExp(new IntValue(1)));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable, heap));
        assertEquals("second operand is not a boolean", ex.getMessage());
    }
}
