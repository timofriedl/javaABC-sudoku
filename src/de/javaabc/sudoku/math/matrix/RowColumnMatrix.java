package de.javaabc.sudoku.math.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RowColumnMatrix<T> implements Matrix<T> {
    protected final List<List<T>> rows;
    protected final List<List<T>> columns;

    private RowColumnMatrix(List<List<T>> rows, List<List<T>> columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public RowColumnMatrix(int width, int height) {
        this(new ArrayList<>(height), new ArrayList<>(width));

        for (int y = 0; y < height; y++)
            rows.add(new ArrayList<>(width));

        for (int x = 0; x < height; x++)
            columns.add(new ArrayList<>(height));
    }

    @Override
    public T get(int x, int y) {
        return rows.get(y).get(x);
    }

    @Override
    public void set(int x, int y, T value) {
        rows.get(y).set(x, value);
        columns.get(x).set(y, value);
    }

    @Override
    public int getWidth() {
        return columns.size();
    }

    @Override
    public int getHeight() {
        return rows.size();
    }

    @Override
    public Stream<List<T>> streamRows() {
        return rows.stream();
    }

    @Override
    public Stream<List<T>> streamColumns() {
        return columns.stream();
    }

}
