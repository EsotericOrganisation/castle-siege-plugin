package net.slqmy.castle_siege_plugin.util.classes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Pair<T> {
    private static final Random random = new Random();

    private final List<T> elements;

    public Pair() {
        this.elements = Arrays.asList(null, null);
    }

    public static <T> Pair<T> of(T left, T right) {
        Pair<T> pair = new Pair<>();
        pair.setLeft(left);
        pair.setRight(right);
        return pair;
    }

    public T getLeft() {
        return elements.getFirst();
    }

    public T getRight() {
        return elements.getLast();
    }

    public T getOther(T element) {
        return element.equals(elements.getFirst())
            ? elements.getLast()
            : elements.getFirst();
    }

    public void setLeft(T left) {
        elements.set(0, left);
    }

    public void setRight(T right) {
        elements.set(1, right);
    }

    public List<T> asList() {
        return Collections.unmodifiableList(elements);
    }

    public T getRandom() {
        return elements.get(random.nextInt(2));
    }
}
