package com.taviannetwork.tavianrpg.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortingUtils {
    public static <T> Map<T, Boolean> buildDependencyReport(Graph<T> graph) {
        Map<T, Boolean> dependencyReport = new HashMap<>();

        for(Node<T> node : graph.getNodes()) {
            for(T dependency : node.getDependencies()) {
                dependencyReport.put(dependency, graph.containsNode(dependency));
            }
        }

        return dependencyReport;
    }

    public static <T> List<T> topoSort(Graph<T> graph) {
        int size = graph.getSize();

        List<T> order = new ArrayList<>();

        Map<T, Boolean> visited = new HashMap<>();
        for(Node<T> tmp : graph.getNodes())
            visited.put(tmp.getValue(), false);

        for(Node<T> tmp : graph.getNodes()) {
            if(!visited.get(tmp.getValue()))
                visitNode(graph, tmp.getValue(), visited, order);
        }

        return order;
    }

    private static <T> void visitNode(Graph<T> graph, T value, Map<T, Boolean> visited, List<T> order) {
        visited.replace(value, true);

        for(T dependency : graph.getNode(value).getDependencies()) {
            if(!visited.get(dependency))
                visitNode(graph, dependency, visited, order);
        }

        order.add(value);
    }
}
