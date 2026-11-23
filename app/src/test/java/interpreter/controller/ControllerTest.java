package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.RefValue;
import interpreter.model.stmt.NewStmt;
import interpreter.model.exp.ReadHeapExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.stmt.VarDeclStmt;


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
        var ex = assertThrows(StatementException.class, () -> run(Examples.TYPE_ERROR));
        assertEquals("declared type of variable a and type of the assigned expression do not match", ex.getMessage());
    }

    @Test
    void logFileContainsTheFiveSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"), c = log.indexOf("Out:"), d = log.indexOf("FileTable:"), e = log.indexOf("Heap:");
        assertTrue(0 <= a && a < b && b < c && c < d && d < e);
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

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(15), s.getOut().get(0));
        assertEquals(new IntValue(50), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithAnEmptyFilePrintsZeroTwice() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(0), s.getOut().get(0));
        assertEquals(new IntValue(0), s.getOut().get(1));
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState s = run(Examples.EX5);
        assertEquals(1, s.getOut().size());
        assertEquals(new IntValue(5), s.getOut().get(0));
    }

    @Test
    void exampleEX_LABPrintsTrue() throws MyException {
        PrgState s = run(Examples.EX_LAB);
        assertEquals(new BoolValue(true), s.getOut().get(0));
    }

    @Test
    void example6PrintsTheTwoReferences() throws MyException {
        PrgState s = run(Examples.EX6);
        assertEquals("{(1, int), (2, Ref(int))}", s.getOut().toString());
        assertEquals("{1 -> 20, 2 -> (1, int)}", s.getHeap().toString());
        assertEquals(new RefValue(1, new IntType()), s.getSymTable().lookup("v"));
        assertEquals(new RefValue(2, new RefType(new IntType())), s.getSymTable().lookup("a"));
    }

    @Test
    void example7Prints20And25() throws MyException {
        PrgState s = run(Examples.EX7);
        assertEquals("{20, 25}", s.getOut().toString());
        assertEquals("{1 -> 20, 2 -> (1, int)}", s.getHeap().toString());
    }

    @Test
    void example8Prints20And35() throws MyException {
        PrgState s = run(Examples.EX8);
        assertEquals("{20, 35}", s.getOut().toString());
        assertEquals("{1 -> 30}", s.getHeap().toString());
    }

    @Test
    void example9Prints20WithTheSafeCollector() throws MyException {
        PrgState s = run(Examples.EX9);
        assertEquals("{20}", s.getOut().toString());
        assertEquals("{1 -> 20, 2 -> (1, int), 3 -> 30}", s.getHeap().toString());
    }

    @Test
    void garbageIsRemovedAfterEveryStep() throws MyException {
        IStmt prg = new CompStmt(new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(new NewStmt("v", new ValueExp(new IntValue(30))),
                                new PrintStmt(new ReadHeapExp(new VarExp("v"))))));
        PrgState s = run(prg);
        assertEquals("{30}", s.getOut().toString());
        assertEquals("{2 -> 30}", s.getHeap().toString());
    }

    @Test
    void example10CountsDownFrom4To0() throws MyException {
        PrgState s = run(Examples.EX10);
        assertEquals("{4, 3, 2, 1, 0}", s.getOut().toString());
    }
}