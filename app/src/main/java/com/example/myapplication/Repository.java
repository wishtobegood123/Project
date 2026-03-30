package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Repository<T extends SoccerEntity> {
    protected List<T> items = new ArrayList<>();

    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    public void add(T item) {
        items.add(item);
    }
    public List<T> filter(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
