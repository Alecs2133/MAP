package interpreter.model.adt;

import interpreter.exceptions.ADTException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class MyStack<T> implements IStack<T> {
    private final Deque<T> stack = new ArrayDeque<>();

    @Override
    public void push(T v) {
        stack.push(v);
    }

    @Override
    public T pop() throws ADTException {
        if (stack.isEmpty()) throw new ADTException("pop from an empty stack");
        return stack.pop();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /** Top of the stack first, e.g. {s1 | s2 | s3}. */
    @Override
    public String toString() {
        return stack.stream().map(Object::toString).collect(Collectors.joining(" | ", "{", "}"));
    }
}
