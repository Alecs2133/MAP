package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeapStmtTest {
    private PrgState state;

    private static ValueExp num(int n) { return new ValueExp(new IntValue(n)); }

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
    }

    @Test
    void newAllocatesAtAddressOneAndUpdatesTheVariable() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void secondNewAllocatesANewAddress() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new NewStmt("v", num(30)).execute(state);
        assertEquals(new RefValue(2, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void newOnAnUndeclaredVariableThrows() {
        var ex = assertThrows(StatementException.class, () -> new NewStmt("x", num(1)).execute(state));
        assertEquals("new: x is not declared", ex.getMessage());
    }

    @Test
    void newOnANonReferenceThrows() throws MyException {
        new VarDeclStmt("n", new IntType()).execute(state);
        var ex = assertThrows(StatementException.class, () -> new NewStmt("n", num(1)).execute(state));
        assertEquals("new: n is not a reference", ex.getMessage());
    }

    @Test
    void newWithAValueOfTheWrongTypeThrows() {
        var exp = new ValueExp(new BoolValue(true));
        assertThrows(StatementException.class, () -> new NewStmt("v", exp).execute(state));
    }

    @Test
    void writeHeapChangesTheLocation() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new WriteHeapStmt("v", num(30)).execute(state);
        assertEquals(new IntValue(30), state.getHeap().get(1));
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void writeHeapOnANonReferenceThrows() throws MyException {
        new VarDeclStmt("n", new IntType()).execute(state);
        var ex = assertThrows(StatementException.class, () -> new WriteHeapStmt("n", num(1)).execute(state));
        assertEquals("wH: n is not a reference", ex.getMessage());
    }

    @Test
    void writeHeapOnAnUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new WriteHeapStmt("x", num(1)).execute(state));
    }

    @Test
    void writeHeapToAnAddressNotInTheHeapThrows() {
        // v is still (0, int): address 0 is null, never in the heap
        var ex = assertThrows(StatementException.class, () -> new WriteHeapStmt("v", num(1)).execute(state));
        assertEquals("wH: address 0 is not in the heap", ex.getMessage());
    }

    @Test
    void writeHeapWithAValueOfTheWrongTypeThrows() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        var exp = new ValueExp(new BoolValue(true));
        assertThrows(StatementException.class, () -> new WriteHeapStmt("v", exp).execute(state));
    }
}