package com.example.myapplication;

import java.util.List;

public class TeamIterator implements CustomIterator<Team> {
    private final List<Team> list;
    private int index = 0;

    public TeamIterator(List<Team> list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    @Override
    public Team next() {
        return list.get(index++);
    }
}
