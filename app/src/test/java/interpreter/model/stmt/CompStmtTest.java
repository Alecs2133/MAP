package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.VarExp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();   // start every test with an empty ExeStack
    }

    @Test
    void pushesSecondThenFirstSoFirstIsOnTop() throws MyException {
        IStmt first = new PrintStmt(new VarExp("a"));
        IStmt second = new NopStmt();
        new CompStmt(first, second).execute(state);
        assertEquals(first, state.getExeStack().pop());
        assertEquals(second, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }
}
