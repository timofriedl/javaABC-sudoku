package de.javaabc.sudoku;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntBucket {
    private Set<Integer> numbers;

    public IntBucket(int min, int max) {
        numbers = new HashSet<>();
        for (int i = min; i < max; i++)
            numbers.add(i);
    }

    public IntBucket(IntBucket toCopy) {
        numbers = new HashSet<>();
        numbers.addAll(toCopy.numbers);
    }

    public int size() {
        return numbers.size();
    }

    public boolean remove(int number) {
        return numbers.remove(number);
    }

    public boolean isDefined() {
        return numbers.size() == 1;
    }

    public boolean isInvalid() {
        return numbers.isEmpty();
    }

    public void define(int value) {
        numbers = new HashSet<>(1);
        numbers.add(value);
    }

    public int getDefinedNumber() {
        //noinspection OptionalGetWithoutIsPresent
        return isDefined() ? numbers.stream()
                .findAny().get() : 0;
    }

    public IntStream stream() {
        return numbers.stream().mapToInt(Integer::intValue);
    }

    @Override
    public String toString() {
        return numbers.stream().map(Object::toString).collect(Collectors.joining(", ", "{", "}"));
    }
}
