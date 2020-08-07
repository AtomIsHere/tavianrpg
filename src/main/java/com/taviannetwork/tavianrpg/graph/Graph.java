package com.taviannetwork.tavianrpg.graph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Graph<T> {
    @Getter
    private final List<Node<T>> nodes;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    public Graph(List<Node<T>> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node<T> node) {
        nodes.add(node);
    }

    public Node<T> getNode(T value) {
        return nodes.stream().filter(n -> n.getValue().equals(value)).findFirst()
                .orElse(null);
    }

    public boolean containsNode(T value) {
        return nodes.stream().anyMatch(n -> n.getValue().equals(value));
    }

    public int getSize() {
        return nodes.size();
    }

    public void clear() {
        nodes.forEach(Node::clear);
        nodes.clear();
    }
}
