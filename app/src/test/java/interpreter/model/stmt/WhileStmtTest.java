package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhileStmtTest {
    private PrgState state;

    private static ValueExp num(int n) { return new ValueExp(new IntValue(n)); }

    private final IStmt loop = new WhileStmt(
            new RelationalExp(RelOp.GT, new VarExp("v"), num(0)),
            new CompStmt(new PrintStmt(new VarExp("v")),
                    new AssignStmt("v", new ArithExp(ArithOp.SUB, new VarExp("v"), num(1)))));

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        new VarDeclStmt("v", new IntType()).execute(state);
    }

    private void runToEnd() throws MyException {
        var stack = state.getExeStack();
        while (!stack.isEmpty()) stack.pop().execute(state);
    }

    private void runLoopWith(int start) throws MyException {
        new AssignStmt("v", num(start)).execute(state);
        state.getExeStack().push(loop);
        runToEnd();
    }

    @Test
    void trueConditionPushesBodyOverTheLoop() throws MyException {
        var w = new WhileStmt(new ValueExp(new BoolValue(true)), new NopStmt());
        w.execute(state);
        assertEquals(new NopStmt(), state.getExeStack().pop());
        assertEquals(w, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void falseConditionPushesNothing() throws MyException {
        new WhileStmt(new ValueExp(new BoolValue(false)), new NopStmt()).execute(state);
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void loopRunsZeroTimes() throws MyException {
        runLoopWith(0);
        assertEquals(0, state.getOut().size());
    }

    @Test
    void loopRunsOnce() throws MyException {
        runLoopWith(1);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(1), state.getOut().get(0));
    }

    @Test
    void loopRunsSeveralTimes() throws MyException {
        runLoopWith(3);
        assertEquals("{3, 2, 1}", state.getOut().toString());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void whileWithANonBooleanConditionThrows() {
        var w = new WhileStmt(new ValueExp(new IntValue(1)), new NopStmt());
        var ex = assertThrows(StatementException.class, () -> w.execute(state));
        assertEquals("condition 1 is not a boolean", ex.getMessage());
    }

    @Test
    void toStringMatchesTheSyntax() {
        assertEquals("(while ((v > 0)) (print(v); v = (v - 1)))", loop.toString());
    }
}