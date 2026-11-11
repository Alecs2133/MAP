package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import java.io.BufferedReader;

class FileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private ValueExp file;
    private StringValue fileName;

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n12\n");
        fileName = new StringValue(f.toString());
        file = new ValueExp(fileName);
        state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void closeOpenFiles() throws Exception {
        for (BufferedReader reader : state.getFileTable().toMap().values())
            reader.close();
    }

    @Test
    void openAddsTheFileToTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void openWithNonStringExpressionThrows() {
        var stmt = new OpenRFileStmt(new ValueExp(new IntValue(3)));
        assertThrows(StatementException.class, () -> stmt.execute(state));
    }

    @Test
    void openingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new OpenRFileStmt(file).execute(state));
    }

    @Test
    void openingAMissingFileThrows() {
        var missing = new ValueExp(new StringValue(dir.resolve("missing.in").toString()));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(missing).execute(state));
    }

    @Test
    void readGivesTheNumbersInOrderThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        var read = new ReadFileStmt(file, "x");
        read.execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));
        read.execute(state);
        assertEquals(new IntValue(12), state.getSymTable().lookup("x"));
        read.execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void readIntoUndeclaredVariableThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "y").execute(state));
    }

    @Test
    void readIntoNonIntVariableThrows() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "b").execute(state));
    }

    @Test
    void readFromAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));
    }

    @Test
    void readANonIntegerLineThrows() throws Exception {
        Path bad = dir.resolve("bad.in");
        Files.writeString(bad, "abc\n");
        var badFile = new ValueExp(new StringValue(bad.toString()));
        new OpenRFileStmt(badFile).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(badFile, "x").execute(state));
    }

    @Test
    void closeRemovesTheFileFromTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(fileName));
    }

    @Test
    void closingAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
    }

    @Test
    void toStringIsTheProgramText() {
        var f = new interpreter.model.exp.VarExp("varf");
        assertEquals("openRFile(varf)", new OpenRFileStmt(f).toString());
        assertEquals("readFile(varf, varc)", new ReadFileStmt(f, "varc").toString());
        assertEquals("closeRFile(varf)", new CloseRFileStmt(f).toString());
    }
}