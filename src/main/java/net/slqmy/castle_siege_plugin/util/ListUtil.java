package net.slqmy.castle_siege_plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ListUtil {
    private ListUtil() {}

    public static <E> List<E> except(List<? extends E> list, E except) {
        List<E> newList = new ArrayList<>(list);
        newList.remove(except);

        return newList;
    }

    public static <T> List<T> randomAmount(T item, int min, int max) {
        if (min > max) throw new IllegalArgumentException("Min cannot be greater than max");

        int amount = min + (int) (Math.random() * (max - min + 1));
        return IntStream.range(0, amount).mapToObj(i -> item).collect(Collectors.toList());
    }
}
