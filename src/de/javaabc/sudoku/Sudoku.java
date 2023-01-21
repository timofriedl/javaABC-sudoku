package de.javaabc.sudoku;

import de.javaabc.sudoku.math.matrix.RowColumnMatrix;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

public class Sudoku extends RowColumnMatrix<IntBucket> {
    private final int blockWidth;
    private final int blockHeight;
    private final int radix;
    private final List<List<IntBucket>> blocks;

    private Sudoku(int blockWidth, int blockHeight, boolean init) {
        super(blockWidth * blockHeight, blockWidth * blockHeight);
        if (blockWidth < 1 || blockHeight < 1)
            throw new IllegalArgumentException("Sudoku must have block size of at least 1x1");

        this.blockWidth = blockWidth;
        this.blockHeight = blockHeight;
        radix = blockWidth * blockHeight;
        blocks = new ArrayList<>(radix);

        for (int i = 0; i < radix; i++)
            blocks.add(new ArrayList<>(radix));

        if (init) for (int y = 0; y < radix; y++)
            for (int x = 0; x < radix; x++)
                setAdd(x, y, new IntBucket(1, radix + 1));

    }

    private static int[] guessBlockSize(int size) {
        int width = size, height = 1;
        for (int w = width, h = height; h <= w; h++, w = size / h)
            if (w * h == size) {
                width = w;
                height = h;
            }
        return new int[]{width, height};
    }

    public Sudoku(int[][] knownNumbers) {
        this(guessBlockSize(knownNumbers.length)[0], guessBlockSize(knownNumbers.length)[1], true);
        for (int y = 0; y < getHeight(); y++)
            for (int x = 0; x < getWidth(); x++)
                if (knownNumbers[y][x] > 0) setNumber(x, y, knownNumbers[y][x]);
    }

    private static int[][] parseHex(String hexString) {
        String[] rows = hexString.split("\n");

        int[][] result = new int[16][16];
        for (int y = 0; y < 16; y++) {
            String row = rows[y].toLowerCase();
            for (int x = 0; x < 16; x++) {
                char c = row.charAt(x);
                result[y][x] = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') ? Integer.parseInt(Character.toString(c), 16) + 1 : 0;
            }
        }
        return result;
    }

    public Sudoku(String hexString) {
        this(parseHex(hexString));
    }

    public Sudoku(Sudoku toCopy) {
        this(toCopy.blockWidth, toCopy.blockHeight, false);

        for (int y = 0; y < getHeight(); y++)
            for (int x = 0; x < getWidth(); x++)
                setAdd(x, y, new IntBucket(toCopy.get(x, y)));
    }

    private void setAdd(int x, int y, IntBucket value) {
        rows.get(y).add(value);
        columns.get(x).add(value);
        blocks.get(y / blockHeight * blockHeight + x / blockWidth).add(value);
    }

    @Override
    public void set(int x, int y, IntBucket value) {
        super.set(x, y, value);
        int blockX = x / blockWidth;
        int blockY = y / blockHeight;
        int posX = x % blockWidth;
        int posY = y % blockHeight;
        blocks.get(blockY * blockHeight + blockX).set(posY * blockWidth + posX, value);
    }

    public void setNumber(int x, int y, int number) {
        get(x, y).define(number);
    }

    public Stream<List<IntBucket>> streamBlocks() {
        return blocks.stream();
    }

    public boolean isInvalid() {
        return streamRows().anyMatch(row -> row.stream().anyMatch(IntBucket::isInvalid));
    }

    public boolean isFull() {
        return streamRows().allMatch(row -> row.stream().allMatch(IntBucket::isDefined));
    }

    public Optional<Point> findEmptyPosition() {
        Map<Point, Integer> possibleNumbersByPosition = new HashMap<>();
        for (int y = 0; y < getHeight(); y++)
            for (int x = 0; x < getWidth(); x++) {
                var bucket = get(x, y);
                if (!bucket.isDefined()) possibleNumbersByPosition.put(new Point(x, y), bucket.size());
            }

        return possibleNumbersByPosition.entrySet().stream().min(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey);
    }

    public Sudoku cloneModify(int x, int y, int number) {
        Sudoku copy = new Sudoku(this);
        copy.setNumber(x, y, number);
        return copy;
    }

    private Stream<int[]> streamKnownNumbers() {
        return streamRows().map(row -> row.stream().mapToInt(IntBucket::getDefinedNumber).toArray());
    }

    private String numberToString(int number) {
        return radix <= 9 ? Integer.toString(number, radix + 1) : Integer.toString(number - 1, radix);
    }

    private String rowToString(int[] row) {
        List<String> rowList = Arrays.stream(row).mapToObj(n -> n == 0 ? " " : numberToString(n)).toList();

        String[] blockParts = new String[blockHeight];
        for (int blockX = 0; blockX < blockParts.length; blockX++)
            blockParts[blockX] = "  " + String.join("  ", rowList.subList(blockX * blockWidth, (blockX + 1) * blockWidth)) + "  ";

        return "│" + String.join("│", blockParts) + "│\n";
    }

    @Override
    public String toString() {
        String[] rowSepLines = new String[blockHeight];
        Arrays.fill(rowSepLines, "─".repeat((blockWidth - 1) * 3 + 5));

        List<String> rowsList = streamKnownNumbers().map(this::rowToString).toList();

        String firstRowSep = "┌" + String.join("┬", rowSepLines) + "┐\n";
        String rowSep = "├" + String.join("┼", rowSepLines) + "┤\n";
        String lastRowSep = "└" + String.join("┴", rowSepLines) + "┘\n";

        String[] parts = new String[blockWidth];
        for (int blockY = 0; blockY < blockWidth; blockY++)
            parts[blockY] = String.join("", rowsList.subList(blockY * blockHeight, (blockY + 1) * blockHeight));

        return firstRowSep + String.join(rowSep, parts) + lastRowSep;
    }
}
