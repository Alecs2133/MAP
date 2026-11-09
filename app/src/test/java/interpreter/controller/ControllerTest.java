package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

class ControllerTest {
    @TempDir
    Path dir;

    private String logPath() {
        return dir.resolve("log.txt").toString();
    }

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        new Controller(new Repository(state, logPath()), false).allStep();   // display off
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
    void example4AssigningIntToBoolThrows() {
        var ex = assertThrows(StatementException.class, () -> run(Examples.EX4));
        assertEquals("declared type of variable a and type of the assigned expression do not match", ex.getMessage());
    }

    @Test
    void logFileContainsTheFourSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"),      d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }

    @Test
    void oneStepAppliesExactlyOneRule() throws MyException {
        PrgState state = PrgState.of(Examples.EX1);
        Controller controller = new Controller(new Repository(state, logPath()), false);
        controller.oneStep(state);
        assertEquals("{int v | (v = 2; print(v))}", state.getExeStack().toString());
        controller.oneStep(state);
        assertEquals("{v -> 0}", state.getSymTable().toString());
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = run(Examples.EX1);
        Controller controller = new Controller(new Repository(state, logPath()), false);
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }
}