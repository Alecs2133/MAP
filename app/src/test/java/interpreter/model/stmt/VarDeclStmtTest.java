package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VarDeclStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
    }

    @Test
    void intVariableGetsDefaultZero() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void boolVariableGetsDefaultFalse() throws MyException {
        new VarDeclStmt("a", new BoolType()).execute(state);
        assertEquals(new BoolValue(false), state.getSymTable().lookup("a"));
    }

    @Test
    void redeclaredVariableThrows() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        var stmt = new VarDeclStmt("v", new BoolType());
        var ex = assertThrows(StatementException.class, () -> stmt.execute(state));
        assertEquals("variable v is already declared", ex.getMessage());
    }

    @Test
    void stringVariableGetsDefaultEmptyString() throws MyException {
        new VarDeclStmt("s", new StringType()).execute(state);
        assertEquals(new StringValue(""), state.getSymTable().lookup("s"));
    }
}
