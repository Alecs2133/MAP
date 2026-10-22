package interpreter.model.adt;

import interpreter.exceptions.ADTException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyList<T> implements IList<T> {
    private final List<T> list = new ArrayList<>();

    @Override
    public void add(T v) {
        list.add(v);
    }

    @Override
    public T get(int index) throws ADTException {
        if (index < 0 || index >= list.size())
            throw new ADTException("index " + index + " is out of bounds");
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    /** e.g. {1, 2} */
    @Override
    public String toString() {
        return list.stream().map(Object::toString).collect(Collectors.joining(", ", "{", "}"));
    }
}
