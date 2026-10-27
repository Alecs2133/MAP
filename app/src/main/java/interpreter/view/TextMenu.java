package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;

import java.util.List;
import java.util.Scanner;

/** Text menu: select one of the example programs and run it completely. */
public class TextMenu {
    private final List<IStmt> programs;
    private final Scanner in = new Scanner(System.in);
    private IStmt selected;

    public TextMenu(List<IStmt> programs) {
        this.programs = programs;
    }

    public void show() {
        while (true) {
            System.out.println();
            System.out.println("Selected program: " + (selected == null ? "none" : selected));
            System.out.println("1. Select a program");
            System.out.println("2. Run the selected program");
            System.out.println("0. Exit");
            System.out.print("> ");
            if (!in.hasNextLine()) return;
            switch (in.nextLine().trim()) {
                case "1" -> selectProgram();
                case "2" -> runSelected();
                case "0" -> { return; }
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private void selectProgram() {
        for (int i = 0; i < programs.size(); i++)
            System.out.println((i + 1) + ". " + programs.get(i));
        System.out.print("Program number: ");
        if (!in.hasNextLine()) return;
        try {
            int index = Integer.parseInt(in.nextLine().trim()) - 1;
            if (index < 0 || index >= programs.size()) {
                System.out.println("There is no program with this number.");
                return;
            }
            selected = programs.get(index);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number.");
        }
    }

    private void runSelected() {
        if (selected == null) {
            System.out.println("Select a program first.");
            return;
        }
        // every run gets a fresh program state
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), selected);
        Controller controller = new Controller(new Repository(state), true);
        try {
            controller.allStep();
            System.out.println("Output: " + state.getOut());
        } catch (MyException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
