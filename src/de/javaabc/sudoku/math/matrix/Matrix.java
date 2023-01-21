package de.javaabc.sudoku.math.matrix;

import java.util.List;
import java.util.stream.Stream;

public interface Matrix<T> {
    T get(int x, int y);

    void set(int x, int y, T value);

    int getWidth();

    int getHeight();

    Stream<List<T>> streamRows();

    Stream<List<T>> streamColumns();

}
