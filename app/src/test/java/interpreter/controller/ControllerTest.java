package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;

class ControllerTest {

    private static PrgState newState(IStmt program) {
        return new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), program);
    }

    private static PrgState run(IStmt program) throws MyException {
        PrgState state = newState(program);
        new Controller(new Repository(state), false).allStep();   // display off
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX1);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(18), state.getOut().get(0));
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX3);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void oneStepAppliesExactlyOneRule() throws MyException {
        PrgState state = newState(Examples.EX1);
        Controller controller = new Controller(new Repository(state), false);
        controller.oneStep(state);   // S1 splits the CompStmt
        assertEquals("{int v | (v = 2; print(v))}", state.getExeStack().toString());
        controller.oneStep(state);   // S3 declares v
        assertEquals("{v -> 0}", state.getSymTable().toString());
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = run(Examples.EX1);
        Controller controller = new Controller(new Repository(state), false);
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }
}
