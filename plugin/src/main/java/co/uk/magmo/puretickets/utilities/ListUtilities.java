package co.uk.magmo.puretickets.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListUtilities {
    public static <T> ArrayList<T> filter(List<T> input, Predicate<T> predicate) {
        ArrayList<T> output = new ArrayList<>();

        for (T value : input) {
            if (predicate.test(value)) {
                output.add(value);
            }
        }

        return output;
    }

    public static <T> ArrayList<T> filter(T[] input, Predicate<T> predicate) {
        return filter(Arrays.asList(input), predicate);
    }

    public static <R, T> ArrayList<R> map(List<T> input, Function<T, R> function) {
        ArrayList<R> output = new ArrayList<>();

        input.forEach(value -> output.add(function.apply(value)));

        return output;
    }

    public static <R, T> ArrayList<R> map(T[] input, Function<T, R> function) {
        return map(Arrays.asList(input), function);
    }
}