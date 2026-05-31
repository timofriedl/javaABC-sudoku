# Sudoku Solver

A small Java Sudoku solver that works by **shrinking candidate sets**, not by translating the puzzle into SAT or other external solvers.

![Sudoku solver in action](sudoku.gif)

The animation shows the grid while the solver runs: cells start as sets of possible values and narrow down as constraints propagate, until every cell holds a single digit.

## Idea

Each cell is an `IntBucket`—a set of numbers that might still go there. A known clue calls `define()` and leaves one value; empty cells keep every digit allowed for that puzzle size.

The solver alternates two steps:

1. **Propagation** — In every row, column, and block, if a cell is already determined, that digit is removed from all other cells in that unit. Repeat until nothing changes. This is the “human” elimination step, implemented once and reused for rows, columns, and blocks.
2. **Guessing** — If propagation stalls, pick an undecided cell with the fewest candidates left, try each possibility, and recurse. Invalid branches (a cell with no candidates left) are discarded.

There is no DIMACS encoding, no SAT library, and no generic CSP framework—just sets, streams, and recursion on the puzzle structure itself.

## Grid layout

`Sudoku` extends a row/column matrix: the same `IntBucket` instance sits in the row list, column list, and block list for each coordinate, so updating candidates through any view updates the cell everywhere. Backtracking copies the whole grid (`cloneModify`) so branches do not interfere.

Puzzle size is flexible: classic 9×9 (3×3 blocks) is inferred from the array dimensions; 16×16 *hexadoku* can be loaded from a hex string. `Solver.main` runs a sample 9×9 puzzle and prints the finished grid plus timing.