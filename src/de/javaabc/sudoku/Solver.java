package de.javaabc.sudoku;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Solver {
    private boolean trivialStep(List<IntBucket> dimension) {
        boolean change = false;
        for (int pos = 0; pos < dimension.size(); pos++) {
            var bucket = dimension.get(pos);
            if (bucket.isDefined())
                for (int i = 0; i < dimension.size(); i++)
                    if (i != pos && dimension.get(i).remove(bucket.getDefinedNumber()))
                        change = true;
        }
        return change;
    }

    private boolean trivialStep(Stream<List<IntBucket>> dimensions) {
        return dimensions.map(this::trivialStep).reduce(false, (res, cur) -> res | cur);
    }

    public Optional<Sudoku> solve(Sudoku sudoku, boolean print) {
        if (print)
            System.out.println(sudoku);

        if (trivialStep(sudoku.streamRows())
                | trivialStep(sudoku.streamColumns())
                | trivialStep(sudoku.streamBlocks()))
            return solve(sudoku, print);

        if (sudoku.isInvalid())
            return Optional.empty();

        if (sudoku.isFull())
            return Optional.of(sudoku);

        Optional<Point> p = sudoku.findEmptyPosition();
        if (p.isEmpty())
            return solve(sudoku, print);

        int x = p.get().x;
        int y = p.get().y;

        return sudoku.get(x, y)
                .stream()
                .mapToObj(number -> sudoku.cloneModify(x, y, number))
                .map(modifiedSudoku -> solve(modifiedSudoku, print))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    public static void main(String[] args) {
        var solver = new Solver();

        var sudoku0 = new Sudoku(new int[][]{
                {8, 0, 5, 3, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 2, 0},
                {0, 7, 0, 0, 1, 0, 5, 0, 0},
                {4, 0, 0, 0, 0, 5, 3, 0, 0},
                {0, 1, 0, 0, 7, 0, 0, 0, 6},
                {0, 0, 3, 2, 0, 0, 0, 8, 0},
                {0, 6, 0, 5, 0, 0, 0, 0, 9},
                {0, 0, 4, 0, 0, 0, 0, 3, 0},
                {0, 0, 0, 0, 0, 9, 7, 0, 0}
        });

        var sudoku1 = new Sudoku(new int[][]{
                {1, 0, 0, 0, 0, 7, 0, 9, 0},
                {0, 3, 0, 0, 2, 0, 0, 0, 8},
                {0, 0, 9, 6, 0, 0, 5, 0, 0},
                {0, 0, 5, 3, 0, 0, 9, 0, 0},
                {0, 1, 0, 0, 8, 0, 0, 0, 2},
                {6, 0, 0, 0, 0, 4, 0, 0, 0},
                {3, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 4, 0, 0, 0, 0, 0, 0, 7},
                {0, 0, 7, 0, 0, 0, 3, 0, 0}
        });

        var sudoku2 = new Sudoku(new int[][]{
                {8, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 3, 6, 0, 0, 0, 0, 0},
                {0, 7, 0, 0, 9, 0, 2, 0, 0},
                {0, 5, 0, 0, 0, 7, 0, 0, 0},
                {0, 0, 0, 0, 4, 5, 7, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 3, 0},
                {0, 0, 1, 0, 0, 0, 0, 6, 8},
                {0, 0, 8, 5, 0, 0, 0, 1, 0},
                {0, 9, 0, 0, 0, 0, 4, 0, 0},
        });

        var sudoku3 = new Sudoku("""
                ..9.865...BF..D.
                ......4F..D...0C
                5.A8.E.......2..
                D.C..3.7...5BEF.
                .1.9.0..E7C....8
                ...2.B1.36...97.
                ...C....F..8..5.
                .......A...D20..
                2C...4..7..0EF..
                6..F...B.3E.8..4
                ..5...83C.4.1..A
                .......6.8..3...
                37.E...0..1AC5..
                ..D.6..90.73....
                .B.A...D..56..19
                .4..1..........B""");

        long startTime = System.currentTimeMillis();
        Optional<Sudoku> result = solver.solve(sudoku3, false);
        long stopTime = System.currentTimeMillis();

        result.ifPresentOrElse(System.out::println, () -> System.out.println("No solution found."));
        System.out.println("(" + (stopTime - startTime) + "ms)");
    }
}
