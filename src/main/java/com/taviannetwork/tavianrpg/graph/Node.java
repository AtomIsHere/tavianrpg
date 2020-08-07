package com.taviannetwork.tavianrpg.graph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    @Getter
    private final T value;
    @Getter
    private final List<T> dependencies;

    public Node(T value) {
        this.value = value;
        this.dependencies = new ArrayList<>();
    }

    public void addDependency(T dependency) {
        dependencies.add(dependency);
    }

    public void clear() {
        dependencies.clear();
    }
}
