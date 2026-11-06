package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

class RelationalExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();

    private static ValueExp num(int n) { return new ValueExp(new IntValue(n)); }

    private IValue eval(RelOp op, int a, int b) throws MyException {
        return new RelationalExp(op, num(a), num(b)).eval(symTable);
    }

    @Test
    void lessThan() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.LT, 2, 3));
        assertEquals(new BoolValue(false), eval(RelOp.LT, 3, 3));
    }

    @Test
    void lessOrEqual() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.LE, 3, 3));
        assertEquals(new BoolValue(false), eval(RelOp.LE, 4, 3));
    }

    @Test
    void equal() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.EQ, 3, 3));
        assertEquals(new BoolValue(false), eval(RelOp.EQ, 2, 3));
    }

    @Test
    void notEqual() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.NE, 2, 3));
        assertEquals(new BoolValue(false), eval(RelOp.NE, 3, 3));
    }

    @Test
    void greaterThan() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.GT, 4, 3));
        assertEquals(new BoolValue(false), eval(RelOp.GT, 3, 3));
    }

    @Test
    void greaterOrEqual() throws MyException {
        assertEquals(new BoolValue(true), eval(RelOp.GE, 3, 3));
        assertEquals(new BoolValue(false), eval(RelOp.GE, 2, 3));
    }

    @Test
    void firstOperandNotIntegerThrows() {
        var exp = new RelationalExp(RelOp.LT, new ValueExp(new BoolValue(true)), num(1));
        var ex = assertThrows(ExpressionException.class, () -> exp.eval(symTable));
        assertEquals("first operand is not an integer", ex.getMessage());
    }

    @Test
    void secondOperandNotIntegerThrows() {
        var exp = new RelationalExp(RelOp.LT, num(1), new ValueExp(new BoolValue(true)));
        var ex = assertThrows(ExpressionException.class, () -> exp.eval(symTable));
        assertEquals("second operand is not an integer", ex.getMessage());
    }

    @Test
    void toStringIsInfix() {
        assertEquals("(a < b)", new RelationalExp(RelOp.LT, new VarExp("a"), new VarExp("b")).toString());
    }
}